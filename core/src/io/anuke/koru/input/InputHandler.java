package io.anuke.koru.input;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.util.pooling.Pools;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.Entity;
import io.anuke.koru.entities.types.BlockAnimation;
import io.anuke.koru.entities.types.ItemDrop;
import io.anuke.koru.graphics.Fx;
import io.anuke.koru.items.*;
import io.anuke.koru.modules.World;
import io.anuke.koru.traits.InventoryTrait;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.world.materials.MaterialLayer;
import io.anuke.koru.world.materials.MaterialTypes.Tree;
import io.anuke.koru.world.materials.MaterialTypes.Wall;
import io.anuke.koru.world.materials.Materials;
import io.anuke.koru.graphics.Effects;

//TODO make this less messy
public class InputHandler{
	public final static float reach = 75;
	public float mouseangle;
	private ObjectMap<InputType, Boolean> keys = new ObjectMap<InputType, Boolean>();
	Entity spark;
	int blockx, blocky;
	float blockhold;
	float cooldown = 0;

	public InputHandler(Entity spark) {
		this.spark = spark;
	}

	public void update(float delta){
		
		
		//weapon updating
		ItemStack stack = spark.inventory.hotbarStack();
		
		// block breaking
		if(key(InputType.leftclick_down)){
			Tile tile = Koru.world.getTile(blockx, blocky);
			Material block = tile.wall();
			Material floor = tile.topFloor();
			
			Material select = null;
			
			if(Mathf.dst(World.world(blockx), World.world(blocky), spark.pos.x, spark.pos.y) < reach
					&& stack != null && stack.item.isType(ItemType.tool)){
				
				if(stack.breaks(block.breakType()) && block.isBreakable()){
					select = block;
				}else if (stack.breaks(floor.breakType()) && floor.isBreakable() && tile.canRemoveTile()){
					select = floor;
				}else{
					blockhold = 0;
					return;
				}
				
				
				blockhold += delta * stack.item.getBreakSpeed(select.breakType());

				if((int) (blockhold) % 20 == 1)
					Effects.effect(Fx.blockparticle, gdxColor(select.getColor()), World.world(blockx), 
							select instanceof Wall ? World.world(blocky) - 6.5f : World.world(blocky) - 1f);

				if(blockhold >= select.breaktime()){
					Effects.effect(Fx.blockbreak, gdxColor(select.getColor()), World.world(blockx), World.world(blocky) - 1);

					if(select instanceof Tree){
						BlockAnimation.create(select, World.world(blockx), World.world(blocky) - 1);
					}

					spark.inventory.addItems(tile.wall().getDrops());
					spark.inventory.sendUpdate(spark);

					ItemDrop.create(World.world(blockx), World.world(blocky), select.getDrops());
					
					if(select == floor)
						tile.removeTile();
					else
						tile.setWall(Materials.air);

					// schedule this later.

					Koru.world.updateLater(blockx, blocky);
					blockhold = 0;
				}

			}else{
				blockhold = 0;
			}

		}else{
			blockhold = 0;
		}
		
	}

	public void inputEvent(InputType type, Object... data){
		inputKey(type, data);
		if(type.name().contains("up")){
			keys.put(InputType.values()[type.ordinal() - 1], false);
		}else if(type.name().contains("down")){
			keys.put(type, true);
		}
	}

	private boolean key(InputType type){
		return keys.get(type, false);
	}

	private void tileDownEvent(){
		// block place check
		ItemStack stack = spark.inventory.hotbarStack();

		if(stack == null)
			return;

		Item item = stack.item;
		
		
		if(item.isType(ItemType.placer)){

			Tile tile = Koru.world.getTile(blockx, blocky);

			InventoryTrait inv = spark.inventory;

			BlockRecipe recipe = null;
			
			if(inv.recipe != -1) recipe = BlockRecipe.getRecipe(inv.recipe);
			
			if(spark.pos.dst(World.world(blockx), World.world(blocky)) < reach
					&& inv.recipe != -1 && inv.hasAll(recipe.requirements())
					&& World.isPlaceable(recipe.result(), tile)){
				
				Effects.effect(Fx.blockplace, World.world(blockx), World.world(blocky));
				
				if(recipe.result().isLayer(MaterialLayer.floor)){
					tile.addFloor(recipe.result());
				}else{
					tile.setWall(recipe.result());
				}
				
				Koru.world.updateTile(tile);

				inv.removeAll(recipe.requirements());
				inv.sendUpdate(spark);
			}
		}
	}
	
	private void onInteract(){
		Tile tile = Koru.world.getTile(blockx, blocky);
		
		if(tile.wall().interactable()){
			tile.wall().onInteract(tile, spark);
			
			Koru.world.updateTile(tile);
		}
	}

	private void rawClick(boolean left){
		ItemStack stack = spark.inventory.hotbarStack();

		if(stack == null)
			return;
	}

	private void inputKey(InputType type, Object... data){
		if(type == InputType.leftclick_down){
			blockx = (int) data[0];
			blocky = (int) data[1];
			click(true);
			rawClick(true);
		}else if(type == InputType.rightclick_down){
			rawClick(false);
		}else if(type == InputType.block_moved){
			click(false);

			blockhold = 0;
			blockx = (int) data[0];
			blocky = (int) data[1];

			click(true);
		}else if(type == InputType.interact){
			blockx = (int) data[0];
			blocky = (int) data[1];
			onInteract();
		}
	}
	


	private void click(boolean down){

		if(down)
			tileDownEvent();

		// fire block click event
		InventoryTrait inv = spark.inventory;
		int slot = inv.hotbar;
		ItemStack stack = inv.inventory[slot];
		if(stack == null)
			return;
		Tile tile = Koru.world.getTile(blockx, blocky);

		ClickEvent event = Pools.obtain(ClickEvent.class, ClickEvent::new).set(down, blockx, blocky, tile, inv);
		stack.item.onClickEvent(event);
		event.free();
	}

	static arc.graphics.Color gdxColor(arc.graphics.Color color){
		return new arc.graphics.Color(color.r, color.g, color.b, color.a);
	}

	public static class ClickEvent{
		public int x, y;
		public Tile tile;
		public InventoryTrait component;
		public ItemStack stack;
		public boolean down;

		public ClickEvent set(boolean click, int x, int y, Tile tile, InventoryTrait component){
			this.down = click;
			this.x = x;
			this.y = y;
			this.tile = tile;
			this.component = component;
			return this;
		}

		public void free(){
			Pools.free(this);
		}
	}

	public static enum ClickType{
		up, down;
	}
}
