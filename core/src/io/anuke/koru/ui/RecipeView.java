package io.anuke.koru.ui;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import io.anuke.koru.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.graphics.Draw;
import io.anuke.koru.items.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.utils.*;

public class RecipeView extends Table{
    final int slotsize = Core.app != null && Core.app.isMobile() ? 48 : 64;
    float pscale = slotsize/16;
    Slot selected;
    Button craftButton;
    Label reqLabel;

    public RecipeView(){
        top().left();

        for(Recipe recipe : Recipe.getAll()){
            Slot slot = new Slot(recipe, recipe.id());
            add(slot).size(slotsize);
        }

        row();

        craftButton = button("Craft", this::craft).colspan(Recipe.getAll().size).growX().height(40).padTop(6).get();
        craftButton.setDisabled(() -> selected == null || !Koru.control.player.inventory.hasAll(selected.recipe.requirements()));

        if(Core.app != null && Core.app.isMobile()){
            row();
            reqLabel = new Label("Select an item");
            add(reqLabel).colspan(Math.max(1, Recipe.getAll().size)).width(slotsize * Math.max(1, Recipe.getAll().size)).wrap().padTop(6).left();
        }

        background(Skin.instance.get("button-window-bg", Drawable.class));
        margin(4);
    }

    void updateRequirements(){
        if(reqLabel != null){
            reqLabel.setText(selected == null ? "Select an item" : Recipe.formatRequirements(selected.recipe.requirements()));
        }
    }

    void craft(){
        if(selected == null){
            return;
        }

        CraftItemPacket packet = new CraftItemPacket();
        packet.recipe = selected.index;
        Net.send(packet);
    }

    class Slot extends Element{
        public final int index;
        public final ClickListener click;
        public final Recipe recipe;

        public Slot(Recipe recipe, int index) {
            this.index = index;
            this.recipe = recipe;

            addListener(click = new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    selected = Slot.this;
                    updateRequirements();
                }
            });

            Tooltip tooltip = new Tooltip(t -> {
                t.background(Skin.instance.get("button-window-bg", Drawable.class));
                t.margin(6);

                for(ItemStack stack : recipe.requirements()){
                    TextureRegion region = Draw.region(stack.item.name() + "item");
                    if(region != null){
                        t.image(new TextureRegionDrawable(region)).size(32f).padRight(4f);
                    }
                    t.label(() -> "x" + stack.amount);
                    t.row();
                }
            });
            tooltip.setInstant(true);
            tooltip.allowMobile = true;
            addListener(tooltip);
        }

        @Override
        public void draw(){
            float alpha = parentAlpha;
            Draw.color();

            Skin.instance.get(selected == Slot.this ? "slotset" : (click.isOver() ? "slotselect2" : "slot2"), Drawable.class).draw(x, y, width, height);

            TextureRegion region = Draw.region(recipe.result().name() + "item");

            float scale = 4f;

            Draw.color(0f, 0f, 0f, 0.1f * alpha);

            Draw.rect(region, x + width/2f, y + height/2f - scale,
                    region.width*scale, region.height*scale);

            Draw.color(color);

            Draw.rect(region, x + width/2f, y + height/2f,
                    region.width*scale, region.height*scale);

            if(recipe.resultAmount() > 1){
                Resources.font2().getData().setScale(2f);

                Resources.font2().setColor(0f, 0f, 0f, 0.18f * alpha);
                Resources.font2().draw("x" + recipe.resultAmount(), x + 10, y + 21);

                Resources.font2().setColor(1f, 1f, 1f, alpha);
                Resources.font2().draw("x" + recipe.resultAmount(), x + 8, y + 23);
            }

            Resources.font2().setColor(arc.graphics.Color.white);
            Draw.color();
        }
    }
}
