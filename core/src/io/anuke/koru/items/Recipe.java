package io.anuke.koru.items;

import arc.struct.*;

public class Recipe{
    private static Seq<Recipe> recipes = new Seq<>();
    private static int lastid;

    private final int id;

    Item result;
    int resultAmount = 1;
    ItemStack[] requirements;

    public Recipe(Item result, ItemStack[] requirements){
        this(result, 1, requirements);
    }

    public Recipe(Item result, int resultAmount, ItemStack[] requirements){
        id = lastid++;
        recipes.add(this);

        this.result = result;
        this.resultAmount = Math.max(1, resultAmount);
        this.requirements = requirements;
    }

    public ItemStack[] requirements(){
        return requirements;
    }

    public Item result(){
        return result;
    }

    public int resultAmount(){
        return resultAmount;
    }

    public int id(){
        return id;
    }

    public static Recipe getRecipe(int id){
        return id < 0 || id >= recipes.size ? null : recipes.get(id);
    }

    public static Seq<Recipe> getAll(){
        return recipes;
    }

    public static void addRecipe(Item result, ItemStack...req){
        new Recipe(result, req);
    }

    public static void addRecipe(Item result, int amount, ItemStack...req){
        new Recipe(result, amount, req);
    }
}
