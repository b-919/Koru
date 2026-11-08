package io.anuke.koru.world.materials;

import io.anuke.koru.graphics.*;
import io.anuke.koru.world.*;

public class StructMaterialTypes{

    public static class Torch extends Material{
        private final arc.graphics.Color lightc = arc.graphics.Color.valueOf("ffd675");

        public Torch(String name){
            super(name, MaterialLayer.wall);
            color = new arc.graphics.Color(0x744a28ff);
            lightc.a = 0.7f;
        }

        public void draw(Tile tile, FacetList group){
            LightRenderer.addLight(tile.worldx(), tile.worldy(), 230f, lightc);

            new KoruFacet("torchflame1"){
                public void draw(){
                    sprite.set(Draw.region("torchflame" + frame(tile)));
                    super.draw();
                }
            }.light().tile(tile).centerX().add(group);

            //radius light
            new KoruFacet("light"){
                public void draw(){
                    float scale = 1f + (float)Math.sin(arc.util.Time.time/7f + tile.rand(100)/30f)/25f + (float)Math.random()/20f;
                    Draw.color(color);
                    Draw.rect(sprite, x + sprite.width/2f, y + sprite.height/2f, sprite.width * scale, sprite.height * scale);
                }
            }.light().set(tile.worldx(), tile.worldy()+6).center().color(0.5f, 0.4f, 0.2f).add(group);

            //actual torch
            new KoruFacet(name){
                public void draw(){
                    Draw.rect(sprite, x + sprite.width/2f, y + sprite.height/2f);
                    Draw.color(1,1,1,color.a);
                    Draw.crect("torchflame" + frame(tile), x, y);
                }
            }.set(tile.worldx(), tile.worldy() + offset()).layer(tile.worldy()).centerX()
            .sort(Sorter.object)
            .addShadow(group, -offset()).add(group);
        }

        int frame(Tile tile){
            return (int)(1+(tile.rand(50)+arc.util.Time.time/4)%4);
        }
    };
}


