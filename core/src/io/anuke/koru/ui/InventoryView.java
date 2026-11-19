package io.anuke.koru.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
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
    public static int slotsize = Core.app != null && Core.app.isMobile() ? 48 : 64;
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
            if(Core.app != null && Core.app.isMobile()){
                slot.addListener(new InputListener(){
                    private boolean dragging;
                    private float downX, downY;

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                        if(pointer != 0) return false;

                        dragging = false;
                        downX = x;
                        downY = y;
                        return true;
                    }

                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer){
                        if(pointer != 0 || dragging) return;

                        Slot target = slotAt(event.stageX, event.stageY);
                        if(Math.abs(x - downX) > 12f || Math.abs(y - downY) > 12f || (target != null && target != slot)){
                            dragging = true;
                            sendClick(slot.index);
                        }
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                        if(pointer != 0) return;

                        Slot target = slotAt(event.stageX, event.stageY);

                        if(dragging){
                            if(target != null && target != slot){
                                sendClick(target.index);
                            }
                        }else if(slot.index < 4){
                            Koru.control.selectHotbar(slot.index);
                        }
                    }
                });
            }else{
                slot.clicked(() -> sendClick(slot.index));
            }
            add(slot).size(slotsize);

        }
    }

    Slot slotAt(float stageX, float stageY){
        if(getScene() == null) return null;

        Element hit = getScene().hit(stageX, stageY, true);
        return hit instanceof Slot ? (Slot)hit : null;
    }

    void sendClick(int index){
        InventoryClickPacket packet = new InventoryClickPacket();
        packet.index = index;
        Net.send(packet);
    }

    @Override
    public void draw(){
        float pscale = slotsize / 16;
        Batch batch = Core.batch;
        float alpha = parentAlpha;
        arc.math.geom.Vec2 mouse = getScene() == null ? Tmp.v1.set(Core.input.mouseX(), Core.input.mouseY())
                : getScene().screenToStageCoordinates(Tmp.v1.set(Core.input.mouseX(), Core.input.mouseY()));

        Draw.color(new arc.graphics.Color(95 / 255f, 95 / 255f, 95 / 255f, 1f));
        Skin.instance.get("blank", Drawable.class).draw(x - pscale, y - pscale, width + pscale * 2, height + pscale * 2);
        Draw.color(Color.white);

        super.draw();

        if(inventory.selected != null){
            drawItem(batch, alpha, mouse.x - slotsize / 2, mouse.y - slotsize / 2, inventory.selected);
        }

        Draw.color();
        Element actor = getScene().hit(mouse.x, mouse.y, true);

        if(actor instanceof Slot){
            Slot slot = (Slot) actor;

            if(stacks[slot.index] != null){
                Resources.font3().getData().setScale(2f);
                Resources.font3().setColor(Color.yellow);
                Resources.font3().draw(stacks[slot.index].item.formalName(), mouse.x, mouse.y + 17, 0, Align.bottomRight, false);
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

        Resources.font2().setColor(Color.white);
        Draw.color();
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
            io.anuke.koru.graphics.Draw.color();
            io.anuke.koru.graphics.Draw.alpha(alpha);
            Skin.instance.get((index == inventory.hotbar) ? "slotselect" : "slot", Drawable.class).draw(x, y, width, height);

            if(stacks[index] != null){
                drawItem(batch, alpha, x, y, stacks[index]);
            }
            io.anuke.koru.graphics.Draw.reset();
        }
    }
}
