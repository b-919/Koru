package io.anuke.koru.items;

import arc.struct.Seq;

import io.anuke.koru.world.materials.Material;

public class BlockRecipe{
	private static Seq<BlockRecipe> recipes = new Seq<>();
	private static int lastid;
	
	private final int id;
	
	Material result;
	ItemStack[] requirements;
	
	public BlockRecipe(Material result){
		this(result, result.getDrops());
	}
	
	public BlockRecipe(Material result, ItemStack...req){
		this.result = result;
		this.requirements = req;
		id = lastid++;
		recipes.add(this);
	}
	
	public ItemStack[] requirements(){
		return requirements;
	}
	
	public Material result(){
		return result;
	}
	
	public int id(){
		return id;
	}
	
	public static BlockRecipe getRecipe(int id){
		return recipes.get(id);
	}
	
	public static Seq<BlockRecipe> getAll(){
		return recipes;
	}
	
	public static void addRecipe(Material result){
		new BlockRecipe(result);
	}
	
	public static void addRecipe(Material result, ItemStack...req){
		new BlockRecipe(result, req);
	}
}
