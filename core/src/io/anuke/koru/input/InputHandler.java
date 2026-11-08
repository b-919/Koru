package io.anuke.koru.input;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.pooling.*;
import io.anuke.koru.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.items.*;
import io.anuke.koru.modules.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.world.*;
import io.anuke.koru.world.materials.*;
import io.anuke.koru.world.materials.MaterialTypes.*;

public class InputHandler{
    public final static float reach = 75;
    public float mouseangle;
    private ObjectMap<InputType, Boolean> keys = new ObjectMap<InputType, Boolean>();
    Entity spark;
    int blockx, blocky;
    float blockhold;
    float cooldown = 0;

    public InputHandler(Entity spark) {
        this.spark = spark;
    }

    public void update(float delta){
        ItemStack stack = spark.inventory.hotbarStack();

        if(key(InputType.leftclickDown)){
            Tile tile = Koru.world.getTile(blockx, blocky);
            Material block = tile.wall();
            Material floor = tile.topFloor();

            Material select = null;

            if(Mathf.dst(World.world(blockx), World.world(blocky), spark.pos.x, spark.pos.y) < reach
                    && stack != null && stack.item.isType(ItemType.tool)){

                if(stack.breaks(block.breakType()) && block.isBreakable()){
                    select = block;
                }else if(stack.breaks(floor.breakType()) && floor.isBreakable() && tile.canRemoveTile()){
                    select = floor;
                }else{
                    blockhold = 0;
                    return;
                }


                blockhold += delta * stack.item.getBreakSpeed(select.breakType());

                if((int) (blockhold) % 20 == 1){
                    Effects.effect(Fx.blockparticle, copyColor(select.getColor()), World.world(blockx),
                            select instanceof Wall ? World.world(blocky) - 6.5f : World.world(blocky) - 1f);

                }
                if(blockhold >= select.breaktime()){
                    Effects.effect(Fx.blockbreak, copyColor(select.getColor()), World.world(blockx), World.world(blocky) - 1);

                    if(select instanceof Tree){
                        BlockAnimation.create(select, World.world(blockx), World.world(blocky) - 1);
                    }

                    spark.inventory.addItems(tile.wall().getDrops());
                    spark.inventory.sendUpdate(spark);

                    ItemDrop.create(World.world(blockx), World.world(blocky), select.getDrops());

                    if(select == floor){
                        tile.removeTile();
                    }else{
                        tile.setWall(Materials.air);

                    }
                    Koru.world.updateLater(blockx, blocky);
                    blockhold = 0;
                }

            }else{
                blockhold = 0;
            }

        }else{
            blockhold = 0;
        }

    }

    public void inputEvent(InputType type, Object... data){
        inputKey(type, data);
        if(type.name().contains("Up")){
            keys.put(InputType.values()[type.ordinal() - 1], false);
        }else if(type.name().contains("Down")){
            keys.put(type, true);
        }
    }

    private boolean key(InputType type){
        return keys.get(type, false);
    }

    private void tileDownEvent(){
        ItemStack stack = spark.inventory.hotbarStack();

        if(stack == null){
            return;

        }
        Item item = stack.item;


        if(item.isType(ItemType.placer)){

            Tile tile = Koru.world.getTile(blockx, blocky);

            InventoryTrait inv = spark.inventory;

            BlockRecipe recipe = null;

            if(inv.recipe != -1) recipe = BlockRecipe.getRecipe(inv.recipe);

            if(spark.pos.dst(World.world(blockx), World.world(blocky)) < reach
                    && inv.recipe != -1 && inv.hasAll(recipe.requirements())
                    && World.isPlaceable(recipe.result(), tile)){

                Effects.effect(Fx.blockplace, World.world(blockx), World.world(blocky));

                if(recipe.result().isLayer(MaterialLayer.floor)){
                    tile.addFloor(recipe.result());
                }else{
                    tile.setWall(recipe.result());
                }

                Koru.world.updateTile(tile);

                inv.removeAll(recipe.requirements());
                inv.sendUpdate(spark);
            }
        }
    }

    private void onInteract(){
        Tile tile = Koru.world.getTile(blockx, blocky);

        if(tile.wall().interactable()){
            tile.wall().onInteract(tile, spark);

            Koru.world.updateTile(tile);
        }
    }

    private void rawClick(boolean left){
        ItemStack stack = spark.inventory.hotbarStack();

        if(stack == null){
            return;
        }
    }

    private void inputKey(InputType type, Object... data){
        if(type == InputType.leftclickDown){
            blockx = (int) data[0];
            blocky = (int) data[1];
            click(true);
            rawClick(true);
        }else if(type == InputType.rightclickDown){
            rawClick(false);
        }else if(type == InputType.blockMoved){
            click(false);

            blockhold = 0;
            blockx = (int) data[0];
            blocky = (int) data[1];

            click(true);
        }else if(type == InputType.interact){
            blockx = (int) data[0];
            blocky = (int) data[1];
            onInteract();
        }
    }



    private void click(boolean down){

        if(down){
            tileDownEvent();

        }
        InventoryTrait inv = spark.inventory;
        int slot = inv.hotbar;
        ItemStack stack = inv.inventory[slot];
        if(stack == null){
            return;
        }
        Tile tile = Koru.world.getTile(blockx, blocky);

        ClickEvent event = Pools.obtain(ClickEvent.class, ClickEvent::new).set(down, blockx, blocky, tile, inv);
        stack.item.onClickEvent(event);
        event.free();
    }

    static arc.graphics.Color copyColor(arc.graphics.Color color){
        return new arc.graphics.Color(color.r, color.g, color.b, color.a);
    }

    public static class ClickEvent{
        public int x, y;
        public Tile tile;
        public InventoryTrait component;
        public ItemStack stack;
        public boolean down;

        public ClickEvent set(boolean click, int x, int y, Tile tile, InventoryTrait component){
            this.down = click;
            this.x = x;
            this.y = y;
            this.tile = tile;
            this.component = component;
            return this;
        }

        public void free(){
            Pools.free(this);
        }
    }

    public static enum ClickType{
        up, down;
    }
}
