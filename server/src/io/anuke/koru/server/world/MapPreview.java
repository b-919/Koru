package io.anuke.koru.server.world;

import java.nio.ByteBuffer;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.SpriteBatch;
import arc.graphics.g2d.TextureAtlas;
import arc.input.KeyCode;
import arc.struct.GridMap;
import arc.struct.IntIntMap;
import arc.struct.ObjectMap;
import arc.util.Tmp;
import arc.util.noise.VoronoiNoise;

import io.anuke.koru.world.Generator;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.*;
import io.anuke.koru.world.materials.MaterialTypes.Grass;
import io.anuke.koru.world.materials.MaterialTypes.Wall;

public class MapPreview implements arc.ApplicationListener{
	Generator gen;
	GridMap<ChunkPix> blocks = new GridMap<ChunkPix>();
	IntIntMap colors = new IntIntMap();
	SpriteBatch batch;
	int viewrange = 4;
	float scl = 3f;
	float vx, vy;
	float speed = 32;
	int pixsize = 32;
	int percision = 1;
	boolean showDark = false;
	static double maxe;
	static double maxt;

	public void init(){
		batch = new SpriteBatch();
		gen = new EntryTerrainGenerator();

		Fi atlasFile = Core.files.internal("sprites/sprites.atlas");
		TextureAtlas.TextureAtlasData data = new TextureAtlas.TextureAtlasData(atlasFile, atlasFile.parent(), false);
		ObjectMap<TextureAtlas.TextureAtlasData.AtlasPage, Pixmap> pixmapCache = new ObjectMap<>();

		for(Material material : Material.getAll()){
			if(!material.isLayer(MaterialLayer.floor) && !(material instanceof Wall))
				continue;

			if(material instanceof Grass){
				Color color = Tmp.c1.set(MaterialTypes.grasscolor.r * material.foilageTint().x,
						MaterialTypes.grasscolor.g * material.foilageTint().y,
						MaterialTypes.grasscolor.b * material.foilageTint().z, 1f);

				colors.put(material.id(), Color.rgba8888(color.r, color.g, color.b, color.a));
			}else{
				for(TextureAtlas.TextureAtlasData.Region region : data.getRegions()){
					if(!region.name.equals(material.name())) continue;
					Pixmap page = pixmapCache.get(region.page);
					if(page == null){
						page = new Pixmap(region.page.textureFile);
						pixmapCache.put(region.page, page);
					}
					colors.put(material.id(), page.get(region.left, region.top));
					break;
				}
			}
		}

		for(int x = -viewrange; x <= viewrange; x++){
			for(int y = -viewrange; y <= viewrange; y++){
				gen(x, y);
			}
		}

		colors.put(Materials.floweryellow.id(), Color.rgba8888(Color.yellow.r, Color.yellow.g, Color.yellow.b, Color.yellow.a));
		colors.put(Materials.flowerblue.id(), Color.rgba8888(Color.royal.r, Color.royal.g, Color.royal.b, Color.royal.a));
		colors.put(Materials.flowerpurple.id(), Color.rgba8888(Color.purple.r, Color.purple.g, Color.purple.b, Color.purple.a));
		colors.put(Materials.flowerred.id(), Color.rgba8888(Color.red.r, Color.red.g, Color.red.b, Color.red.a));
	}

	public void update(){
		if(Core.input.keyDown(KeyCode.escape))
			Core.app.exit();

		float speed = Core.input.keyDown(KeyCode.shiftLeft) ? this.speed * 5 : this.speed;

		if(Core.input.keyDown(KeyCode.w))
			vy += speed;
		if(Core.input.keyDown(KeyCode.a))
			vx -= speed;
		if(Core.input.keyDown(KeyCode.s))
			vy -= speed;
		if(Core.input.keyDown(KeyCode.d))
			vx += speed;

		Core.graphics.clear(0f, 0f, 0f, 1f);
		Draw.batch(batch, () -> {
			int viewx = (int) (vx / pixsize);
			int viewy = (int) (vy / pixsize);
			for(int x = -viewrange + viewx; x <= viewrange + viewx; x++){
				for(int y = -viewrange + viewy; y <= viewrange + viewy; y++){
					ChunkPix pix = blocks.get(x, y);
					if(pix != null){
						Draw.rect(Draw.wrap(pix.texture), x * pixsize * scl + Core.graphics.getWidth() / 2 - vx*scl,
								y * pixsize * scl + Core.graphics.getHeight() / 2 - vy*scl, pixsize * scl, pixsize * scl);
					}else{
						gen(x, y);
					}
				}
			}
		});
	}

	void gen(int x, int y){
		blocks.put(x, y, new ChunkPix(x, y));
	}


	VoronoiNoise noise = new VoronoiNoise(0, true);

	RidgedPerlin per = new RidgedPerlin(1, 1, 0.4f);

	Color temp = new Color();

	int getPix(int x, int y){
		Tile tile = gen.generate(x, y);
		float light = showDark ? tile.light() : 1f;

		if(tile.wall() instanceof Wall){
			temp.set(colors.get(tile.wallid, 0));
			temp.mul(light, light, light, 1f);
			return Color.rgba8888(temp.r, temp.g, temp.b, temp.a);
		}

		if(!tile.isWallEmpty()){
			if(colors.containsKey(tile.wallid)){
				return colors.get(tile.wallid, 0);
			}
			temp.set(colors.get(tile.topFloor().id(), 0)+1000);
			temp.mul(light, light, light, 1f);
			return Color.rgba8888(temp.r, temp.g, temp.b, temp.a);
		}

		temp.set(colors.get(tile.topFloor().id(), 0));
		temp.mul(light, light, light, 1f);

		return Color.rgba8888(temp.r, temp.g, temp.b, temp.a);
	}

	class ChunkPix{
		Pixmap pixmap;
		Texture texture;

		public ChunkPix(int x, int y) {
			pixmap = new Pixmap(pixsize/percision, pixsize/percision);

			for(int px = 0; px < pixmap.getWidth(); px++){
				for(int py = 0; py < pixmap.getHeight(); py++){


					setPixel(pixmap, px, (pixmap.getHeight() - 1 - py), getPix(x * pixsize + px * percision, y * pixsize + py * percision));
				}
			}
texture = new Texture(pixmap);
		}
	}

	static void setPixel(Pixmap pixmap, int x, int y, int color){
		ByteBuffer buf = pixmap.getPixels();
		int i = (y * pixmap.getWidth() + x) * 4;
		buf.put(i, (byte)(color >>> 24));
		buf.put(i + 1, (byte)(color >>> 16));
		buf.put(i + 2, (byte)(color >>> 8));
		buf.put(i + 3, (byte)color);
	}

public void resize(int width, int height){
		Draw.proj(0, 0, width, height);
	}
}
