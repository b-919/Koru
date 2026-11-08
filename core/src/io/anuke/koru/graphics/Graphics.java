package io.anuke.koru.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.ObjectMap;

import java.util.Stack;

public class Graphics{
	private static final ObjectMap<String, Surface> surfaces = new ObjectMap<>();
	private static final Stack<Surface> surfaceStack = new Stack<>();

	public static void addSurface(String name, int scale, int priority){
		Surface surface = surfaces.get(name);
		if(surface == null || surface.scale != scale){
			if(surface != null) surface.dispose();
			surfaces.put(name, new Surface(scale));
		}
	}

	public static void surface(String name){
		surface(name, true);
	}

	public static void surface(String name, boolean clear){
		if(!surfaceStack.isEmpty()){
			end();
			surfaceStack.peek().end();
		}

		Surface surface = surfaces.get(name);
		if(surface == null) return;

		surfaceStack.push(surface);

		end();

		surface.begin(clear);
		begin();
	}

	public static void surface(){
		if(surfaceStack.isEmpty()) return;

		Surface surface = surfaceStack.pop();

		end();
		surface.end();

		Surface current = surfaceStack.isEmpty() ? null : surfaceStack.peek();
		if(current != null) current.begin(false);

		begin();
	}

	public static void flushSurface(){
		if(surfaceStack.isEmpty()) return;

		Surface surface = surfaceStack.pop();

		end();
		surface.end();

		Surface current = surfaceStack.isEmpty() ? null : surfaceStack.peek();
		if(current != null) current.begin(false);

		setScreen();

		TextureRegion region = surface.region();
		arc.graphics.g2d.Draw.rect(region, Core.graphics.getWidth()/2f, Core.graphics.getHeight()/2f,
				Core.graphics.getWidth(), Core.graphics.getHeight());

		beginCam();
	}

	public static void setScreen(){
		arc.graphics.g2d.Draw.proj(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());
	}

	public static void beginCam(){
		arc.graphics.g2d.Draw.proj(Core.camera);
	}

	public static void shader(Shader shader){
		arc.graphics.g2d.Draw.shader(shader);
	}

	public static void shader(){
		arc.graphics.g2d.Draw.shader();
	}

	public static void begin(){
	}

	public static void end(){
		arc.graphics.g2d.Draw.flush();
	}

	public static arc.math.geom.Vec2 mouseWorld(){
		return Core.camera.unproject(Core.input.mouseX(), Core.input.mouseY());
	}

	private static class Surface{
		final int scale;
		FrameBuffer buffer;
		TextureRegion region;

		Surface(int scale){
			this.scale = Math.max(scale, 1);
		}

		void begin(boolean clear){
			int width = Math.max(1, Core.graphics.getWidth() / scale);
			int height = Math.max(1, Core.graphics.getHeight() / scale);

			if(buffer == null || buffer.getWidth() != width || buffer.getHeight() != height){
				if(buffer != null) buffer.dispose();
				buffer = new FrameBuffer(Pixmap.Format.rgba8888, width, height, false);
				region = null;
			}

			buffer.begin();
			if(clear) Core.graphics.clear(Color.clear);
		}

		void end(){
			if(buffer != null) buffer.end();
		}

		TextureRegion region(){
			if(region == null) region = new TextureRegion(buffer.getTexture());
			return region;
		}

		void dispose(){
			if(buffer != null) buffer.dispose();
			buffer = null;
			region = null;
		}
	}
}
