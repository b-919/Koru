package io.anuke.koru.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.items.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.utils.*;

public class InventoryView extends Table{
    public static final int slotsize = 64;
    private ItemStack[] stacks;
    private InventoryTrait inventory;

    public InventoryView() {
        inventory = Koru.control.player.inventory;
        stacks = inventory.inventory;
        setup();
    }

    void setup(){
        marginRight(3f);
        marginBottom(3f);

        int width = 4;

        for(int i = 0; i < stacks.length; i++){
            if(i % width == 0){
                row();

            }
            Slot slot = new Slot(i);
            slot.clicked(() -> {
                InventoryClickPacket packet = new InventoryClickPacket();
                packet.index = slot.index;
                Net.send(packet);
            });
            add(slot).size(slotsize);

        }
    }

    @Override
    public void draw(){
        float pscale = slotsize / 16;
        Batch batch = Core.batch;
        float alpha = parentAlpha;

        Draw.color(new arc.graphics.Color(95 / 255f, 95 / 255f, 95 / 255f, 1f));
        Skin.instance.get("blank", Drawable.class).draw(x - pscale, y - pscale, width + pscale * 2, height + pscale * 2);
        Draw.color(Color.white);

        super.draw();

        if(inventory.selected != null){
            drawItem(batch, alpha, Core.input.mouseX() - slotsize / 2, Core.input.mouseY() - slotsize / 2, inventory.selected);
        }

        Element actor = getScene().hit(Core.input.mouseX(), Core.input.mouseY(), true);

        if(actor instanceof Slot){
            Slot slot = (Slot) actor;

            if(stacks[slot.index] != null){
                Resources.font3().getData().setScale(2f);
                Resources.font3().setColor(Color.yellow);
                Resources.font3().draw(stacks[slot.index].item.formalName(), Core.input.mouseX(), Core.input.mouseY() + 17, 0, Align.bottomRight, false);
                Resources.font3().setColor(Color.white);
            }
        }
    }

    public static void drawItem(Batch batch, float alpha, float x, float y, ItemStack stack){
        TextureRegion region = io.anuke.koru.graphics.Draw.region(stack.item.name() + "item");

        Draw.color(0, 0, 0, 0.1f * alpha);
        Draw.rect(region, x + slotsize / 2, y + slotsize / 2 - 4, region.width * slotsize / 16, region.height * slotsize / 16);

        Draw.color(1f, 1f, 1f, alpha);
        Draw.rect(region, x + slotsize / 2, y + slotsize / 2, region.width * slotsize / 16, region.height * slotsize / 16);

        Resources.font2().getData().setScale(2f);

        if(stack.amount > 1){

            Resources.font2().setColor(0, 0, 0, 0.18f * alpha);
            Resources.font2().draw(stack.amount + "", x + 8 + 2, y + 54 - 3);

            Resources.font2().setColor(1, 1, 1, alpha);
            Resources.font2().draw(stack.amount + "", x + 8, y + 54);
        }
    }

    class Slot extends Element{
        public final int index;
        public final ClickListener click;

        public Slot(int index) {
            this.index = index;

            addListener((click = new ClickListener()));
        }

        @Override
        public void draw(){
            float alpha = parentAlpha;
            Batch batch = Core.batch;
            io.anuke.koru.graphics.Draw.alpha(alpha);
            Skin.instance.get((index == inventory.hotbar) ? "slotselect" : "slot", Drawable.class).draw(x, y, width, height);

            if(stacks[index] != null){
                drawItem(batch, alpha, x, y, stacks[index]);
            }
            io.anuke.koru.graphics.Draw.reset();
        }
    }
}
