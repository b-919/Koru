package io.anuke.koru.world.materials;

import arc.graphics.Color;

import io.anuke.koru.*;
import io.anuke.koru.graphics.Draw;
import io.anuke.koru.graphics.KoruFacet;
import io.anuke.koru.world.Tile;
import arc.*;
import io.anuke.koru.graphics.FacetList;
import io.anuke.koru.graphics.Sorter;

public class StructMaterialTypes{
	
	public static class Torch extends Material{
	    Color lightc = Color.valueOf("ffd675");
		
		public Torch(String name){
			super(name, MaterialLayer.wall);
			color = new arc.graphics.Color(0x744a28ff);
			lightc.a = 0.7f;
		}
		
		public void draw(Tile tile, FacetList group){
			
			//light overlay
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
/*
            Light add = new PointLight(Koru.renderer.rays, 50, lightc, 230, tile.worldx(), tile.worldy());
            add.setNoise(3, 2, 1.5f);
*/
			//actual torch
			new KoruFacet(name){
				public void draw(){
					Draw.rect(sprite, x + sprite.width/2f, y + sprite.height/2f);
					Draw.color(1,1,1,color.a);
					Draw.crect("torchflame" + frame(tile), x, y);
				}

                @Override
                public void onFree(){
                    super.onFree();
                    // add.remove();
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


