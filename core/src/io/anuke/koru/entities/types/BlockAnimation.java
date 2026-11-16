package io.anuke.koru.entities.types;

import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.network.*;
import io.anuke.koru.world.*;
import io.anuke.koru.world.materials.*;
import io.anuke.koru.world.materials.MaterialTypes.Tree;

public class BlockAnimation extends Entity{
    private static final float rspeed = 0.7f;// used for tree falling animation, degrees per tick
    private float rotation;
    private float direction;

    {
        hasLifetime = true;
        lifetime = 50;
    }

    @Override
    public void update(){
        super.update();

        if(direction == 0f){
            direction = Koru.control.player.pos().x < pos.x ? -1f : 1f;
        }

        rotation += rspeed * direction * Time.delta;
    }

    @Override
    public void draw(){
        if(matid == 0){
            throw new RuntimeException("No material specified in the data! Did you set the renderer material?");
        }

        Material material = material();

        Tile tile = Koru.world.getWorldTile(pos().x, pos().y);

        Draw.z(pos().y);

        if(material instanceof Tree){
            ((Tree)material).drawFalling(tile, rotation, fract());
        }else{
            Draw.alpha(fract());
            material.draw(tile);
        }

        Draw.reset();
    }

    /** This sends the animation as well. */
    public static void create(Material material, float x, float y){
        BlockAnimation entity = new BlockAnimation();
        entity.pos().set(x, y);
        entity.matid = material.id();
        Net.sendEntity(entity);
    }
}


