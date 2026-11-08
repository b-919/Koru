package io.anuke.koru.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.Draw;

public class SpriteFacet extends Facet{
    public TextureRegion sprite;
    public Color color = new Color(Color.white);
    public float w = -1, h = -1;
    public float skewAmount;
    public boolean layerSet;

    public SpriteFacet(String region){
        this.sprite = io.anuke.koru.graphics.Draw.region(region);
    }

    public SpriteFacet(TextureRegion region){
        this.sprite = region;
    }

    public SpriteFacet(){
    }

    public float getDrawWidth(){
        return w >= 0 ? w : sprite.width;
    }

    public float getDrawHeight(){
        return h >= 0 ? h : sprite.height;
    }

    public SpriteFacet setSize(float w, float h){
        this.w = w;
        this.h = h;
        return this;
    }

    @Override
    public SpriteFacet layer(float layer){
        this.layer = layer;
        layerSet = true;
        return this;
    }

    @Override
    public float getLayer(){
        return layerSet ? layer : y;
    }

    public SpriteFacet set(float x, float y, boolean center, boolean block){
        if(sprite == null) return this;
        float dw = getDrawWidth(), dh = getDrawHeight();
        this.x = x;
        this.y = y;
        if(center){
            this.x -= dw/2f;
            this.y -= dh/2f;
        }
        if(block){
            this.layer = y;
            layerSet = true;
        }
        return this;
    }

    public SpriteFacet region(TextureRegion region){
        this.sprite = region;
        return this;
    }

    public SpriteFacet add(float x, float y){
        this.x += x;
        this.y += y;
        return this;
    }

    public SpriteFacet centerX(){
        if(sprite == null) return this;
        this.x -= getDrawWidth()/2f;
        return this;
    }

    public SpriteFacet centerY(){
        if(sprite == null) return this;
        this.y -= getDrawHeight()/2f;
        return this;
    }

    public SpriteFacet center(){
        centerX();
        centerY();
        return this;
    }

    public SpriteFacet scale(float x, float y){
        return setSize(getDrawWidth() + x, getDrawHeight() + y);
    }

    public SpriteFacet skewX(float amount){
        skewAmount = amount;
        return this;
    }

    @Override
    public void draw(){
        if(sprite == null) return;

        float dw = getDrawWidth(), dh = getDrawHeight();
        float cx = x + dw/2f, cy = y + dh/2f;

        if(layerSet && layer == Sorter.shadow){
            ShadowRenderer.add(sprite, cx, cy, dw, dh, color);
            return;
        }

        Draw.color(color);

        if(skewAmount == 0f){
            Draw.rect(sprite, cx, cy, dw, dh);
        }else{
            final int[] corner = {0};
            Draw.rectv(sprite, cx, cy, dw, dh, v -> {
                //rectv order: bottom-left, top-left, top-right, bottom-right
                if(corner[0] == 1 || corner[0] == 2) v.x += skewAmount;
                corner[0]++;
            });
        }
    }

    @Override
    public void reset(){
        super.reset();
        sprite = null;
        w = h = -1;
        skewAmount = 0f;
        layerSet = false;
        layer = 0f;
        color.set(Color.white);
    }
}


