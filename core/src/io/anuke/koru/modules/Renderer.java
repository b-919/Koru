package io.anuke.koru.modules;

import arc.Core;
import arc.input.KeyCode;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.math.geom.Rect;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.types.Player;
import io.anuke.koru.graphics.*;
import io.anuke.koru.graphics.lsystems.LSystems;
import io.anuke.koru.input.InputHandler;
import io.anuke.koru.items.BlockRecipe;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.items.ItemType;
import io.anuke.koru.traits.InventoryTrait;
import io.anuke.koru.utils.Resources;
import io.anuke.koru.world.BreakType;
import io.anuke.koru.world.Chunk;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.world.materials.MaterialLayer;
import io.anuke.koru.world.materials.MaterialTypes.*;
import io.anuke.koru.world.materials.Materials;
import io.anuke.koru.modules.RendererModule;
import arc.math.Mathf;
import arc.util.Time;

public class Renderer extends RendererModule{
	//TODO why are these final?
	// FIX ALLLLL

	public static final int viewrangex = 32;
	public static final int viewrangey = 26;
	
	public static final int scale = 4;
	public static final Color outlineColor = new Color(0.5f, 0.7f, 1f, 1f);
	
	private Player player;
	private FacetList[][] renderables = new FacetList[World.chunksize * World.loadrange * 2][World.chunksize * World.loadrange * 2];
	private TextureRegion shadowSprite;
	private int lastcamx, lastcamy;
	
	//TODO round alpha, but not colors
	public Object rays; //TODO RayHandler
	private Object light; //TODO PointLight
	private float darkness = 1f;
	
	public Renderer() {
		//Timers.mark();
		Fx.blockbreak.getClass();
		
		//arc.Core.cameraScale = scale;
		
		atlas = new arc.graphics.g2d.TextureAtlas(arc.Core.files.internal("sprites/sprites.atlas"));
		arc.Core.atlas = atlas;
		for(arc.graphics.g2d.TextureAtlas.AtlasRegion region : atlas.getRegions()){
			int slash = region.name.lastIndexOf('/');
			if(slash != -1){
				String base = region.name.substring(slash + 1);
				if(!atlas.getRegionMap().containsKey(base))
					atlas.getRegionMap().put(base, region);
			}
		}
		//arc's Lines/Draw require a 1x1 white region; alias it to the blank pixel.
		if(!atlas.getRegionMap().containsKey("white")){
			arc.graphics.g2d.TextureAtlas.AtlasRegion blank = atlas.getRegionMap().get("blank");
			if(blank != null)
				atlas.getRegionMap().put("white", blank);
		}
		
		font = Resources.font();
		//font.setUseIntegerPositions(false);
		//font.getData().markupEnabled = true;
		
		//RayHandler.isDiffuse = true;
		//rays = new RayHandler();
		//rays.setBlurNum(3);
		//light = new PointLight(rays, 300, arc.graphics.Color.white, 1135, 0, 0);
		//light.setSoftnessLength(40f);

		Facets.instance().setLayerManager(new FacetLayerHandler(){{
			allDrawLayers.add(KoruFacetLayers.water);
		}});
		
		KoruCursors.setCursor("cursor");
		KoruCursors.updateCursor();

		loadMaterialColors();

		//Koru.log("Loaded resources. Time taken: " + Timers.elapsed() + " ms.");
		
		pixelate();
		//Graphics.addSurface("trees", 1);
		//Graphics.addSurface("dark", arc.Core.cameraScale, 5);
	}

	void loadMaterialColors(){

		for(Material material : Material.getAll()){
			if(material.isLayer(MaterialLayer.floor))
				continue;

			TextureRegion region = atlas.getRegionMap().get(material.name());
			if(region == null)
				continue;

			//arc.graphics.Pixmap pixmap = atlas.getPixmapOf(region);
			//material.setColor(new Color(pixmap.getPixel(region.getRegionX(), region.getRegionY())));
		}
	}
	
	@Override
	public void init(){
		player = Koru.control.player;
		new BaseFacet(this::drawBlockOverlay).add();
		new BaseFacet(-Sorter.light.ordinal()-1, Sorter.object, this::drawTileOverlay).add();
		new BaseFacet(-Sorter.light.ordinal()-1, Sorter.object, this::drawSelectOverlay).add();

		shadowSprite = Draw.region("lightshadow");
	}

	@Override
	public void update(){
		//updateLight();
		
		if(Koru.control.canMove()){
			KoruCursors.setCursor("cursor");
		}
		
		float addx = 1f-(Core.graphics.getWidth()/scale) % 1f;
		float addy = 1f-(Core.graphics.getHeight()/scale) % 1f;
		
		//if(Settings.getBool("smoothcam")){
			smoothCamera(player.pos().x + addx, player.pos().y + addy, 0.08f);
		//}else{
		//	addx = addy = 0;
		//	camera.position.set(player.pos().x + addx, player.pos().y + addy);
		//}
		
		float lim = 7;
		
		if(Math.abs(player.pos().x - camera.position.x) > lim){
			camera.position.x = player.pos().x - Mathf.clamp(player.pos().x - camera.position.x, -lim, lim);
		}
		
		if(Math.abs(player.pos().y - camera.position.y) > lim){
			camera.position.y = player.pos().y - Mathf.clamp(player.pos().y - camera.position.y, -lim, lim);
		}
		
		float lastx = camera.position.x;
		float lasty = camera.position.y;
		
		camera.position.set((int)lastx + addx, (int)lasty + addy);

		draw();
		
		camera.position.set(lastx, lasty);
		
		if(Koru.control.canMove())
			KoruCursors.updateCursor();
	}
	
	@Override
	public void draw(){
		camera.update();
		arc.graphics.g2d.Draw.proj(camera);

		if(pixelate) beginPixel();
		
		clearScreen();
		
		LSystems.cacheAll();
		
		drawMap();
		Facets.instance().renderAll();
		
		//drawShadows();
		
		if(pixelate) endPixel();
		
		//Graphics.end();
		
		//rays.setCombinedMatrix(camera);
		//rays.updateAndRender();
		
		if(Koru.control.canMove()){
			record();
		}
	}
	
	void updateLight(){
		/*
		//TODO more performant lights?
		float w = screen.x*2;
		float h = screen.y*2;
		rays.setBounds(camera.position.x-w/2, camera.position.y-h/2, w, h);
		
		//debug controls?
		if(Inputs.buttonUp(Buttons.LEFT) && Inputs.keyDown(Keys.CONTROL_LEFT)){
			Light add = new PointLight(rays, 50, arc.graphics.Color.white, 100, Graphics.mouseWorld().x, Graphics.mouseWorld().y);
			add.setNoise(3, 2, 1.5f);
		}
		
		light.setPosition(camera.position.x, camera.position.y+7);
		Tile tile = world.getWorldTile(player.pos().x, player.pos().y);
		
		if(tile == null) return;
		
		float l = tile.light();
		darkness = Mathf.lerp(darkness, l, 0.03f * Timers.delta());
		
		light.setColor(darkness, darkness, darkness, 1f);
		light.setDistance(Mathf.dst(0, 0, w/2f, h/2f));
		*/
	}
	
	void drawSelectOverlay(BaseFacet r){
		if(Koru.ui.menuOpen()) return;

		Tile tile = Koru.world.getTile(Koru.control.cursorX(), Koru.control.cursorY());
		ItemStack stack = player.inventory.hotbarStack();
		
		if(stack != null && tile != null && Koru.control.playerReachesBlock() && !tile.wall().interactable()){
			Material select = null;
			
			if(stack.breaks(tile.wall().breakType()) && tile.wall().isBreakable()){
				select = tile.wall();
			}else if(stack.breaks(tile.topFloor().breakType()) && tile.canRemoveTile() && tile.topFloor().isBreakable()){
				select = tile.topFloor();
			}
			
			if(select == null) return;
			
			if(select.breakType() == BreakType.stone){
				KoruCursors.setCursor("pickaxe");
			}else if(select.breakType() == BreakType.wood){
				KoruCursors.setCursor("axe");
			}
			
			FacetList list = getRenderable(Koru.control.cursorX(), Koru.control.cursorY());
			
			if(list.facets.size == 0) return;
			
			KoruFacet facet = null;
			
			for(int i = list.facets.size - 1; i >= 0; i --){
				if(list.facets.get(i) instanceof KoruFacet){
					facet = (KoruFacet)list.facets.get(i);
					break;
				}
			}
			
			Shaders.inline.region = facet.sprite;
			Shaders.inline.color = outlineColor;
			
			Graphics.end();
			Graphics.shader(Shaders.inline);
			Graphics.begin();
			facet.draw();
			Graphics.shader();
		}
		
	}

	void drawTileOverlay(BaseFacet r){
		if(Koru.ui.menuOpen()) return;

		int x = Koru.control.cursorX();
		int y = Koru.control.cursorY();

		Tile tile = Koru.world.getTile(x, y);
		
		//TODO
		if(tile != null && tile.wall().interactable() && Koru.control.playerReachesBlock()){
			KoruCursors.setCursor("select");
			
			FacetList list = getRenderable(Koru.control.cursorX(), Koru.control.cursorY());
			SpriteFacet facet = null;
			
			for(Facet check : list.facets){
				if(check instanceof SpriteFacet && !(check.provider == Sorter.tile && check.getLayer() > 0) &&
						check.getLayer() != Sorter.shadow.ordinal() && 
						(facet == null || check.compareTo(facet) > 0)){
					facet = (SpriteFacet)check;
				}
			}
			
			if(facet == null) return;
			
			Shaders.outline.color = outlineColor;
			Shaders.outline.region = facet.sprite;
			
			Graphics.end();
			Graphics.shader(Shaders.outline);
			Graphics.begin();
			
			facet.draw();
			
			Graphics.shader();
		}
	}

	void drawBlockOverlay(BaseFacet r){

		int x = Koru.control.cursorX();
		int y = Koru.control.cursorY();

		r.layer = y * World.tilesize;

		r.sort(Sorter.object);

		InventoryTrait inv = player.inventory;

		Tile tile = Koru.world.getTile(x, y);
		
		if(inv.recipe != -1 && inv.hotbarStack() != null && inv.hotbarStack().item.isType(ItemType.placer) 
				&& inv.hasAll(BlockRecipe.getRecipe(inv.recipe).requirements())){

			if(Mathf.dst(World.world(x), World.world(y), player.pos().x, player.pos().y) < InputHandler.reach && World.isPlaceable(BlockRecipe.getRecipe(inv.recipe).result(), tile)){
				Draw.color(0.5f, 1f, 0.5f, 0.3f);
			}else{
				Draw.color(1f, 0.5f, 0.5f, 0.3f);
			}

			Material result = BlockRecipe.getRecipe(inv.recipe).result();
			
			if(result.isLayer(MaterialLayer.floor)){
				Draw.rect("blank", x*World.tilesize, y*World.tilesize, 12, 12);
			}else{
				Draw.grect("block", x*World.tilesize, y*World.tilesize- World.tilesize/2, 12, 20);
			}

			Draw.color();
		}
		
	}

	void drawMap(){
		if(Core.graphics.getFrameId() == 5)
			updateTiles();
		
		int camx = Math.round(camera.position.x / World.tilesize), camy = Math.round(camera.position.y / World.tilesize);

		if(lastcamx != camx || lastcamy != camy){
			updateTiles();
		}

		lastcamx = camx;
		lastcamy = camy;
	}
	
	FacetList getRenderable(int worldx, int worldy){
		int camx = Koru.world.toChunkCoords(camera.position.x), camy = Koru.world.toChunkCoords(camera.position.y);
		return renderables[(worldx + renderables.length/2 - camx*World.chunksize)][(worldy + renderables[0].length/2 - camy*World.chunksize)];
	}
	
	public void invalidateTiles(){
		lastcamx = -9999999;
		lastcamy = -9999999;
	}
	
	public void drawShadows(){
		int camx = Math.round(camera.position.x / World.tilesize), camy = Math.round(camera.position.y / World.tilesize);
		Graphics.surface("dark");
		
		for(int chunkx = 0; chunkx < World.loadrange * 2; chunkx++){
			for(int chunky = 0; chunky < World.loadrange * 2; chunky++){
				Chunk chunk = Koru.world.chunks[chunkx][chunky];
				if(chunk == null)
					continue;
				for(int x = 0; x < World.chunksize; x++){
					for(int y = 0; y < World.chunksize; y++){
						int worldx = chunk.worldX() + x;
						int worldy = chunk.worldY() + y;

						Tile tile = chunk.tiles[x][y];
						
						if(Math.abs(worldx - camx) > viewrangex || Math.abs(worldy - camy) > viewrangey)
							continue;

						if(Math.abs(lastcamx - camx) > viewrangex || Math.abs(lastcamy - camy) > viewrangey)
							continue;
						
						if(tile.light() < 0.999f){
							Draw.color(0, 0, 0, 1f-tile.light());
							Draw.rect("blur", tile.worldx(), tile.worldy(), 12, 12);
						}
					}
				}
			}
		}
		Draw.color();
		
		Graphics.end();
		Graphics.shader(Shaders.round);
		Graphics.begin();
		Graphics.flushSurface();
		Graphics.end();
		Graphics.shader();
		Graphics.begin();
	}

	public void updateTiles(){
		// rays.clearRects();
		
		int camx = Math.round(camera.position.x / World.tilesize), camy = Math.round(camera.position.y / World.tilesize);

		for(int chunkx = 0; chunkx < World.loadrange * 2; chunkx++){
			for(int chunky = 0; chunky < World.loadrange * 2; chunky++){
				Chunk chunk = Koru.world.chunks[chunkx][chunky];
				if(chunk == null)
					continue;
				for(int x = 0; x < World.chunksize; x++){
					for(int y = 0; y < World.chunksize; y++){
						int worldx = chunk.worldX() + x;
						int worldy = chunk.worldY() + y;
						int rendx = chunkx * World.chunksize + x, rendy = chunky * World.chunksize + y;

						Tile tile = chunk.tiles[x][y];

						if(renderables[rendx][rendy] != null)
							renderables[rendx][rendy].free();

						if(Math.abs(worldx - camx) > viewrangex || Math.abs(worldy - camy) > viewrangey)
							continue;

						if(renderables[rendx][rendy] != null){
							renderables[rendx][rendy].free();
						}else{
							renderables[rendx][rendy] = new FacetList();
						}

						if(tile.topFloor() != Materials.air && Math.abs(worldx * 12 - camera.position.x + 6) < camera.width / 2 + 24 && Math.abs(worldy * 12 - camera.position.y + 6) < camera.height / 2 + 36){
							tile.topFloor().draw(tile, renderables[rendx][rendy]);
						}

						if(!tile.isWallEmpty() && Math.abs(worldx * 12 - camera.position.x + 6) < 
								camera.width / 2 + 12 + tile.wall().cullSize()
								&& Math.abs(worldy * 12 - camera.position.y + 6) < 
								camera.height / 2 + 12 + tile.wall().cullSize()){
							tile.wall().draw(tile, renderables[rendx][rendy]);
						}
						

						if(!tile.isWallEmpty() && tile.wall() instanceof Wall &&
								Koru.world.isAccesible(worldx, worldy) && tile.light < 127){
							
							arc.math.geom.Rect r = tile.wall().getHitbox(worldx, worldy, new arc.math.geom.Rect());
							arc.math.geom.Rect rect = new arc.math.geom.Rect(r.x, r.y + World.tilesize/4 + 1f, r.width, r.height);
							// rays.addRect(rect);
						}
					}
				}
			}
		}
		
		// rays.updateRects();
	}
	
	public void resize(int width, int height){
		super.resize(width, height);
		// rays.resizeFBO((int)(screen.x/4), (int)(screen.y/4));
		// rays.pixelate();
	}
}


