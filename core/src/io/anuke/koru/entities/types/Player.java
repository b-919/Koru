package io.anuke.koru.entities.types;

import arc.*;
import arc.graphics.*;
import arc.util.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.input.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.syncing.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.traits.DirectionTrait.*;
import io.anuke.koru.utils.*;

public class Player extends Entity{

    {
        hasCollider = true;
        cwidth = 8;
        cheight = 6;

        chunkLoad = true;

        syncType = SyncType.player;
        interpolator = new Interpolator();

        inventory = new InventoryTrait(20);

        tileCollides = true;
        tcolWidth = 4;
        tcolHeight = 3;
        tcolOffsetY = 2;
    }

    @Override
    protected void onAdd(){
        //server players handle remote input; client players handle only the local player's own input
        if(input == null && (Net.server() || local)) input = new InputHandler(this);
    }

    @Override
    public void update(){
        super.update();

        if(Net.server() && input != null){
            input.update(Time.delta);
        }
    }

    @Override
    public boolean unload(){
        return false;
    }

    @Override
    public void drawShadow(){
        Drawf.shadow(this, 14, 1, true);
    }

    @Override
    public void draw(){
        Draw.z(pos().y + 1f);

        float x = pos().x;
        float y = pos().y;

        pos().x = (int) pos().x;
        pos().y = (int) pos().y;

        Direction dir = this.direction;

        Draw.grect("crab-" + (dir == Direction.left || dir == Direction.right ? "s" : dir == Direction.back ? "b" : "f")
                + (walktime > 0 ? "-" + ((int) (walktime / 7)) % 3 : ""),
                pos().x, pos().y, dir == Direction.left ? -12 : 12, 12);

        if(!local){
            Resources.font2().setColor(arc.graphics.Color.yellow);

            Resources.font2().getData().setScale(1f);
            Resources.font2().setUseIntegerPositions(false);
            Resources.font2().draw(name, pos().x + 0.5f, pos().y + 18, 0, arc.util.Align.center, false);

            Resources.font2().setColor(arc.graphics.Color.white);

            if(walking){
                walktime += Time.delta;
            }else{
                walktime = 0;
            }
        }

        pos().x = x;
        pos().y = y;
    }
}


