package io.anuke.koru.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.struct.*;

public class ShadowRenderer{
    private static final Seq<Shadow> shadows = new Seq<>();
    private static FrameBuffer buffer;
    private static TextureRegion region;

    public static void add(TextureRegion sprite, float x, float y, float width, float height, Color color){
        if(sprite == null) return;
        shadows.add(new Shadow(sprite, x, y, width, height, new Color(color)));
    }

    public static void draw(){
        if(shadows.size == 0) return;

        int width = Core.graphics.getWidth(), height = Core.graphics.getHeight();
        if(buffer == null) resize(width, height);

        arc.graphics.g2d.Draw.flush();
        buffer.begin();
        Core.graphics.clear(Color.clear);

        Gl.blendEquation(Gl.max);
        arc.graphics.g2d.Draw.blend(new Blending(Gl.one, Gl.one));

        for(Shadow shadow : shadows){
            arc.graphics.g2d.Draw.color(shadow.color);
            arc.graphics.g2d.Draw.rect(shadow.sprite, shadow.x, shadow.y, shadow.width, shadow.height);
        }

        arc.graphics.g2d.Draw.flush();
        Gl.blendEquation(Gl.funcAdd);
        arc.graphics.g2d.Draw.blend();
        arc.graphics.g2d.Draw.color();
        buffer.end();

        arc.graphics.g2d.Draw.proj(0, 0, width, height);
        arc.graphics.g2d.Draw.blend();
        arc.graphics.g2d.Draw.color();
        arc.graphics.g2d.Draw.rect(region, width / 2f, height / 2f, width, height);
        arc.graphics.g2d.Draw.proj(Core.camera);

        shadows.clear();
    }

    public static void resize(int width, int height){
        if(buffer != null) buffer.dispose();
        width = Math.max(1, width);
        height = Math.max(1, height);
        buffer = new FrameBuffer(Pixmap.Format.rgba8888, width, height, false);
        buffer.getTexture().setFilter(TextureFilter.linear);
        region = new TextureRegion(buffer.getTexture());
        region.flip(false, true);
    }

    private static class Shadow{
        final TextureRegion sprite;
        final float x, y, width, height;
        final Color color;

        Shadow(TextureRegion sprite, float x, float y, float width, float height, Color color){
            this.sprite = sprite;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }
    }
}
