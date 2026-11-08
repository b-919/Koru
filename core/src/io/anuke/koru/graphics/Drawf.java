package io.anuke.koru.graphics;

import arc.*;
import io.anuke.koru.entities.*;

public class Drawf{

    public static boolean hasRegion(String name){
        return Core.atlas.has(name);
    }

    /** Draws a shadow under the entity. */
    public static void shadow(Entity entity, int size, float offset){
        shadow(entity, size, offset, false);
    }

    /** Draws the static shadow sprite, optionally snapped to integer coordinates. */
    public static void shadow(Entity entity, int size, float offset, boolean snap){
        String region = "shadow" + (int)(size * 0.8f / 2f + Math.pow(size, 1.5f) / 200f) * 2;

        Draw.z(-999999);

        float x = snap ? (int) entity.pos.x : entity.pos.x;
        float y = snap ? (int) entity.pos.y : entity.pos.y;

        arc.graphics.g2d.TextureRegion sprite = Draw.region(region);
        if(sprite != null){
            ShadowRenderer.add(sprite, x, y + offset, sprite.width, sprite.height, new arc.graphics.Color(0, 0, 0, 0.3f));
        }
    }
}


