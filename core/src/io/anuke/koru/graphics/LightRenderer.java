package io.anuke.koru.graphics;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Pixmap;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.VertexAttribute;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;

import io.anuke.koru.Koru;
import io.anuke.koru.modules.World;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.MaterialTypes.Wall;

/**Tile-based light map. Lights are masked by wall raycasts, blurred and multiplied into the scene,*/
public class LightRenderer{
	private static final int maxRays = 240;
	private static final Seq<Light> lights = new Seq<>();

	private static FrameBuffer buffer, blurBuffer;
	private static Mesh fanMesh;
	private static Shader fanShader, blurShader, compositeShader;

	private static float ambient = 1f, target = 1f;

	public static void addLight(float x, float y, float radius, Color color){
		Light light = new Light(x, y, radius, color);
		light.build();
		lights.add(light);
	}

	public static void clearLights(){
		lights.clear();
	}

	public static void setAmbient(float value){
		target = Mathf.clamp(value);
	}

	public static void draw(){
		ambient = Mathf.lerp(ambient, target, Math.min(0.08f * Time.delta, 1f));

		// Daylight with no relevant lights changes nothing.
		if(ambient >= 0.999f && target >= 0.999f) return;

		if(buffer == null) resize(Core.graphics.getWidth(), Core.graphics.getHeight());

		int width = Core.graphics.getWidth(), height = Core.graphics.getHeight();

		buffer.begin();
		Gl.clearStencil(0);
		Gl.clearColor(ambient, ambient, ambient, 1f);
		Gl.clear(Gl.colorBufferBit | Gl.stencilBufferBit);

		arc.graphics.g2d.Draw.proj(Core.camera);
		arc.graphics.g2d.Draw.blend(Blending.additive);

		for(Light light : lights){
			if(!onScreen(light)) continue;

			drawMask(light);

			arc.graphics.g2d.Draw.color(light.color);
			arc.graphics.g2d.Draw.rect("light", light.x, light.y, light.radius * 2f, light.radius * 2f);
			arc.graphics.g2d.Draw.endStencil();
		}

		arc.graphics.g2d.Draw.flush();
		buffer.end();

		blur(width, height);

		// Multiply the scene by the light map so terrain stays visible. This bypasses the sprite
		// batch so the custom blend factors are guaranteed to reach the framebuffer.
		arc.graphics.g2d.Draw.flush();
		Gl.blendEquation(Gl.funcAdd);
		Gl.enable(Gl.blend);
		Gl.blendFuncSeparate(Gl.dstColor, Gl.zero, Gl.dstColor, Gl.zero);
		buffer.blit(compositeShader);
		Gl.disable(Gl.blend);

		arc.graphics.g2d.Draw.blend();
		arc.graphics.g2d.Draw.color();
		arc.graphics.g2d.Draw.proj(Core.camera);
	}

	private static void drawMask(Light light){
		Gl.clearStencil(0);
		Gl.clear(Gl.stencilBufferBit);

		arc.graphics.g2d.Draw.beginStencil();

		fanShader.bind();
		fanShader.setUniformMatrix4("u_projTrans", Core.camera.mat);
		fanMesh.setVertices(light.vertices, 0, light.vertices.length);
		fanMesh.render(fanShader, Gl.triangleFan, 0, light.vertices.length / 2);

		arc.graphics.g2d.Draw.beginStenciled();
	}

	private static void blur(int width, int height){
		Gl.disable(Gl.blend);

		for(int i = 0; i < 3; i++){
			blurShader.bind();
			blurShader.setUniformf("size", width, height);
			blurShader.setUniformf("dir", 1f, 0f);
			blurBuffer.begin();
			buffer.blit(blurShader);
			blurBuffer.end();

			blurShader.bind();
			blurShader.setUniformf("dir", 0f, 1f);
			buffer.begin();
			blurBuffer.blit(blurShader);
			buffer.end();
		}

		Gl.enable(Gl.blend);
	}

	private static boolean onScreen(Light light){
		float halfW = Core.camera.width / 2f + light.radius;
		float halfH = Core.camera.height / 2f + light.radius;
		return Math.abs(light.x - Core.camera.position.x) < halfW && Math.abs(light.y - Core.camera.position.y) < halfH;
	}

	public static void resize(int width, int height){
		width = Math.max(2, width);
		height = Math.max(2, height);

		if(buffer != null) buffer.dispose();
		if(blurBuffer != null) blurBuffer.dispose();

		buffer = new FrameBuffer(Pixmap.Format.rgba8888, width, height, false, true);
		buffer.getTexture().setFilter(TextureFilter.linear);
		blurBuffer = new FrameBuffer(Pixmap.Format.rgba8888, width, height, false);
		blurBuffer.getTexture().setFilter(TextureFilter.linear);

		init();
	}

	private static void init(){
		if(fanShader != null) return;

		fanShader = new Shader(
			"attribute vec4 a_position;\n" +
			"uniform mat4 u_projTrans;\n" +
			"void main(){\n" +
			"    gl_Position = u_projTrans * a_position;\n" +
			"}\n",
			"void main(){\n" +
			"    gl_FragColor = vec4(1.0);\n" +
			"}\n"
		);

		blurShader = new Shader(
			"attribute vec4 a_position;\n" +
			"attribute vec2 a_texCoord0;\n" +
			"varying vec2 v_texCoords;\n" +
			"void main(){\n" +
			"    v_texCoords = a_texCoord0;\n" +
			"    gl_Position = a_position;\n" +
			"}\n",
			"varying vec2 v_texCoords;\n" +
			"uniform sampler2D u_texture;\n" +
			"uniform vec2 dir;\n" +
			"uniform vec2 size;\n" +
			"void main(){\n" +
			"    vec2 off = dir / size * 2.0;\n" +
			"    vec4 sum = texture2D(u_texture, v_texCoords) * 0.2270270270;\n" +
			"    sum += texture2D(u_texture, v_texCoords + off * 1.3846153846) * 0.3162162162;\n" +
			"    sum += texture2D(u_texture, v_texCoords - off * 1.3846153846) * 0.3162162162;\n" +
			"    sum += texture2D(u_texture, v_texCoords + off * 3.2307692308) * 0.0702702703;\n" +
			"    sum += texture2D(u_texture, v_texCoords - off * 3.2307692308) * 0.0702702703;\n" +
			"    gl_FragColor = sum;\n" +
			"}\n"
		);

		blurShader.bind();
		blurShader.setUniformi("u_texture", 0);

		compositeShader = new Shader(
			"attribute vec4 a_position;\n" +
			"attribute vec2 a_texCoord0;\n" +
			"varying vec2 v_texCoords;\n" +
			"void main(){\n" +
			"    v_texCoords = a_texCoord0;\n" +
			"    gl_Position = a_position;\n" +
			"}\n",
			"varying vec2 v_texCoords;\n" +
			"uniform sampler2D u_texture;\n" +
			"void main(){\n" +
			"    gl_FragColor = texture2D(u_texture, v_texCoords);\n" +
			"}\n"
		);

		compositeShader.bind();
		compositeShader.setUniformi("u_texture", 0);

		fanMesh = new Mesh(true, maxRays + 2, 0, VertexAttribute.position);
	}

	private static float castRay(float x, float y, float dx, float dy, float maxDist){
		float ts = World.tilesize;
		int cellX = Mathf.floor(x / ts), cellY = Mathf.floor(y / ts);

		int stepX = dx > 0 ? 1 : dx < 0 ? -1 : 0;
		int stepY = dy > 0 ? 1 : dy < 0 ? -1 : 0;

		float tDeltaX = dx != 0 ? Math.abs(ts / dx) : Float.MAX_VALUE;
		float tDeltaY = dy != 0 ? Math.abs(ts / dy) : Float.MAX_VALUE;
		float tMaxX = dx != 0 ? (stepX > 0 ? (cellX + 1) * ts - x : x - cellX * ts) / Math.abs(dx) : Float.MAX_VALUE;
		float tMaxY = dy != 0 ? (stepY > 0 ? (cellY + 1) * ts - y : y - cellY * ts) / Math.abs(dy) : Float.MAX_VALUE;

		while(true){
			float t;

			if(tMaxX < tMaxY){
				t = tMaxX;
				cellX += stepX;
				tMaxX += tDeltaX;
			}else{
				t = tMaxY;
				cellY += stepY;
				tMaxY += tDeltaY;
			}

			if(t >= maxDist) return maxDist;
			if(blocks(cellX, cellY)) return Math.max(t, 0f);
		}
	}

	private static boolean blocks(int x, int y){
		Tile tile = Koru.world.getTile(x, y);
		return tile == null || tile.wall() instanceof Wall;
	}

	private static class Light{
		final float x, y, radius;
		final Color color;
		float[] vertices;

		Light(float x, float y, float radius, Color color){
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.color = new Color(color);
		}

		void build(){
			int rays = Mathf.clamp((int)(radius * 1.2f), 64, maxRays);
			vertices = new float[(rays + 2) * 2];
			vertices[0] = x;
			vertices[1] = y;

			float step = Mathf.PI2 / rays;

			for(int i = 0; i < rays; i++){
				float angle = i * step;
				float dx = Mathf.cos(angle), dy = Mathf.sin(angle);
				float dist = castRay(x, y, dx, dy, radius);

				vertices[(i + 1) * 2] = x + dx * dist;
				vertices[(i + 1) * 2 + 1] = y + dy * dist;
			}

			vertices[(rays + 1) * 2] = vertices[2];
			vertices[(rays + 1) * 2 + 1] = vertices[3];
		}
	}
}
