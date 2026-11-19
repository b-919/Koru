package io.anuke.koru.items.impl;

import io.anuke.koru.items.*;

import static io.anuke.koru.items.Recipe.*;

public class Recipes{

    public static void load(){
        addRecipe(Items.stick, 4, new ItemStack(Items.wood, 1));
        addRecipe(Items.hammer, new ItemStack(Items.wood, 5), new ItemStack(Items.stick, 5));
        addRecipe(Items.pickaxe, new ItemStack(Items.wood, 5), new ItemStack(Items.stick, 5));
        addRecipe(Items.axe, new ItemStack(Items.wood, 5), new ItemStack(Items.stick, 5));
        addRecipe(Items.mushroomStaff, new ItemStack(Items.stick, 10), new ItemStack(Items.mushroom, 5));
    }
}
