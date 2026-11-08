package io.anuke.koru.modules;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import io.anuke.koru.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.graphics.Graphics;
import io.anuke.koru.graphics.lsystems.*;
import io.anuke.koru.input.*;
import io.anuke.koru.items.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.utils.*;
import io.anuke.koru.world.*;
import io.anuke.koru.world.materials.*;

public class Renderer extends RendererModule{
    public static final int viewrangex = 32;
    public static final int viewrangey = 26;

    public static final int scale = 4;
    public static final Color outlineColor = new Color(0.5f, 0.7f, 1f, 1f);

    private Player player;
    private FacetList[][] renderables = new FacetList[World.chunksize * World.loadrange * 2][World.chunksize * World.loadrange * 2];
    private int lastcamx, lastcamy;

    public Renderer() {
        Fx.blockbreak.getClass();

        atlas = new arc.graphics.g2d.TextureAtlas(arc.Core.files.internal("sprites/sprites.atlas"));
        arc.Core.atlas = atlas;
        for(arc.graphics.g2d.TextureAtlas.AtlasRegion region : atlas.getRegions()){
            int slash = region.name.lastIndexOf('/');
            if(slash != -1){
                String base = region.name.substring(slash + 1);
                if(!atlas.getRegionMap().containsKey(base)){
                    atlas.getRegionMap().put(base, region);
                }
            }
        }
        if(!atlas.getRegionMap().containsKey("white")){
            arc.graphics.g2d.TextureAtlas.AtlasRegion blank = atlas.getRegionMap().get("blank");
            if(blank != null){
                atlas.getRegionMap().put("white", blank);
            }
        }

        font = Resources.font();

        Facets.instance().setLayerManager(new FacetLayerHandler(){{
            allDrawLayers.add(KoruFacetLayers.water);
        }});

        KoruCursors.setCursor("cursor");
        KoruCursors.updateCursor();

        Graphics.addSurface("trees", 1, 0);
        pixelate();
    }

    @Override
    public void init(){
        player = Koru.control.player;
        new BaseFacet(0, Sorter.object, this::drawBlockOverlay).add();
        new BaseFacet(-Sorter.light - 1, Sorter.object, this::drawTileOverlay).add();
        new BaseFacet(-Sorter.light - 1, Sorter.object, this::drawSelectOverlay).add();
    }

    @Override
    public void update(){
        if(Koru.control.canMove()){
            KoruCursors.setCursor("cursor");
        }

        float addx = 1f-(Core.graphics.getWidth()/scale) % 1f;
        float addy = 1f-(Core.graphics.getHeight()/scale) % 1f;

        smoothCamera(player.pos().x + addx, player.pos().y + addy, 0.08f);

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

        Tile playerTile = Koru.world.getWorldTile(player.pos().x, player.pos().y);
        LightRenderer.setAmbient(playerTile == null ? 1f : playerTile.light());

        draw();

        camera.position.set(lastx, lasty);

        if(Koru.control.canMove()){
            KoruCursors.updateCursor();
        }
    }

    @Override
    public void draw(){
        camera.update();
        arc.graphics.g2d.Draw.proj(camera);

        if(pixelate) beginPixel();

        clearScreen();

        LSystems.cacheAll();

        drawMap();
        Facets.instance().renderTiles();

        Seq<Entity> sorted = null;
        if(Entities.instance != null){
            sorted = new Seq<>(Entities.all());
            sorted.sort((a, b) -> Float.compare(a.pos().y, b.pos().y));
            for(Entity entity : sorted){
                entity.drawShadow();
            }
            Draw.z(0);
        }

        ShadowRenderer.draw();
        Facets.instance().renderObjects();

        if(sorted != null){
            for(Entity entity : sorted){
                entity.draw();
            }
        }

        if(pixelate) endPixel();

        LightRenderer.draw();

        if(Koru.control.canMove()){
            record();
        }
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

        if(tile != null && tile.wall().interactable() && Koru.control.playerReachesBlock()){
            KoruCursors.setCursor("select");

            FacetList list = getRenderable(Koru.control.cursorX(), Koru.control.cursorY());
            SpriteFacet facet = null;

            for(Facet check : list.facets){
                if(check instanceof SpriteFacet && !(check.provider == Sorter.tile && check.getLayer() > 0) &&
                        check.getLayer() != Sorter.shadow &&
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
        if(Core.graphics.getFrameId() == 5){
            updateTiles();

        }
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

    public void updateTiles(){
        LightRenderer.clearLights();

        int camx = Math.round(camera.position.x / World.tilesize), camy = Math.round(camera.position.y / World.tilesize);
        for(int chunkx = 0; chunkx < World.loadrange * 2; chunkx++){
            for(int chunky = 0; chunky < World.loadrange * 2; chunky++){
                Chunk chunk = Koru.world.chunks[chunkx][chunky];
                if(chunk == null){
                    continue;
                }
                for(int x = 0; x < World.chunksize; x++){
                    for(int y = 0; y < World.chunksize; y++){
                        int worldx = chunk.worldX() + x;
                        int worldy = chunk.worldY() + y;
                        int rendx = chunkx * World.chunksize + x, rendy = chunky * World.chunksize + y;

                        Tile tile = chunk.tiles[x][y];

                        if(renderables[rendx][rendy] != null){
                            renderables[rendx][rendy].free();

                        }
                        if(Math.abs(worldx - camx) > viewrangex || Math.abs(worldy - camy) > viewrangey){
                            continue;

                        }
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
                    }
                }
            }
        }
    }

    public void resize(int width, int height){
        super.resize(width, height);
        camera.resize(width / scale, height / scale);
        ShadowRenderer.resize(width, height);
        LightRenderer.resize(width, height);
    }
}


