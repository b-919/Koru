package io.anuke.koru.ui;

import arc.graphics.g2d.Batch;
import arc.graphics.g2d.TextureRegion;

import io.anuke.koru.Koru;
import io.anuke.koru.items.Recipe;
import io.anuke.koru.network.Net;
import io.anuke.koru.network.packets.RecipeSelectPacket;
import io.anuke.koru.traits.InventoryTrait;
import io.anuke.koru.graphics.Draw;
import arc.scene.Element;
import arc.scene.ui.layout.Table;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.style.Drawable;

public class RecipeView extends Table{
	final int slotsize = 64;
	float pscale = slotsize/16;
	Slot selected;
	
	public RecipeView(){
		top().left();
		
		for(Recipe recipe : Recipe.getAll()){
			Slot slot = new Slot(recipe, recipe.id());
			add(slot).size(slotsize);
		}
		
		background(Skin.instance.get("button-window-bg", Drawable.class));
		margin(4);
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
						
					Koru.control.player.inventory.recipe = index;
						
					RecipeSelectPacket packet = new RecipeSelectPacket();
					packet.recipe = index;
					Net.send(packet);
				}
			});

		}

		@Override
		public void draw(){
			float alpha = parentAlpha;
			
			Skin.instance.get(Koru.control.player.inventory.recipe == index ? "slotset" : (click.isOver() ? "slotselect2" : "slot2"), Drawable.class).draw(x, y, width, height);
			
			TextureRegion region = Draw.region(recipe.result().name() + "item");
			
			float scale = 4f;
			
			Draw.color(0f, 0f, 0f, 0.1f * alpha);
			
			Draw.rect(region, x + width/2f, y + height/2f - scale, 
					region.width*scale, region.height*scale);
			
			Draw.color(color);
			
			Draw.rect(region, x + width/2f, y + height/2f, 
					region.width*scale, region.height*scale);
		}
	}
}


