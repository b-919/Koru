package io.anuke.koru.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.graphics.Draw;
import io.anuke.koru.items.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.utils.*;
import io.anuke.koru.world.materials.*;

public class BlockView extends Table{
    final int slotsize = 64;
    float pscale = slotsize/16;
    Requirement req = new Requirement();
    Slot selected;

    public BlockView(){

        label(() -> (selected == null ? "" : selected.recipe.result().formalName()));
        row();
        add(req).size(64);

        row();

        Table blocks = new Table();

        for(BlockRecipe recipe : BlockRecipe.getAll()){
            Slot slot = new Slot(recipe, recipe.id());
            blocks.add(slot).size(slotsize);
        }

        add(blocks).padTop(32);

        background(Skin.instance.get("button-window-bg", Drawable.class));
        margin(2);
    }

    class Requirement extends Element{
        ItemStack[] stacks;

        @Override
        public void draw(){
            float alpha = parentAlpha;
            Batch batch = Core.batch;
            Skin.instance.get("slot", Drawable.class).draw(x, y, width, height);

            if(stacks == null) return;

            InventoryTrait inv = Koru.control.player.inventory;

            for(int i = 0; i < stacks.length; i ++){
                TextureRegion region = Draw.region(stacks[i].item.name() + "item");
                float w = region.width*pscale, h = region.height*pscale;
                float x = this.x + width/2 - region.width/2*pscale - (stacks.length-1)*8*pscale + i*16*pscale,
                        y = this.y+2*pscale;

                Draw.rect(region, x + w/2, y + h/2, w, h);

                int amount = Math.min(inv.getAmountOf(stacks[i].item), stacks[i].amount);
                Resources.font2().setColor(amount < stacks[i].amount ? new Color(1f, 0.4f, 0.4f, 1f) : Color.white);

                Resources.font2().draw(amount + "/" + stacks[i].amount, x-2, y + 20, 0, Align.bottomLeft, false);

                Resources.font2().setColor(Color.white);
            }
        }

        public void set(ItemStack[] stack){
            this.stacks = stack;
        }
    }

    class Slot extends Element{
        public final int index;
        public final ClickListener click;
        public final BlockRecipe recipe;

        public Slot(BlockRecipe recipe, int index) {
            this.index = index;
            this.recipe = recipe;

            addListener(click = new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    selected = Slot.this;
                    req.set(recipe.requirements());

                    Koru.control.player.inventory.recipe = index;

                    RecipeSelectPacket packet = new RecipeSelectPacket();
                    packet.recipe = index;
                    Net.send(packet);
                }
            });

        }

        @Override
        public void draw(){
            float alpha = parentAlpha;
            Batch batch = Core.batch;
            Draw.color(color);
            Skin.instance.get(Koru.control.player.inventory.recipe == index ? "slotset" : (click.isOver() ? "slotselect2" : "slot2"), Drawable.class).draw(x, y, width, height);

            Material result = recipe.result();
            TextureRegion region = Draw.region(result.name());

            float w = region.width*pscale, h = region.height*pscale;

            if(result.layer() == MaterialLayer.floor){
                Draw.rect(region, x + width/2, y + height/2, w, h);
            }else{
                Draw.rect(region, x + width/2, 4*pscale+ y + height/2, w, h);
            }

            Draw.reset();
        }
    }
}


