package io.anuke.koru.items.impl;

import io.anuke.koru.world.materials.*;

import static io.anuke.koru.items.BlockRecipe.*;

public class BlockRecipes{

    public static void load(){
        addRecipe(StructMaterials.woodblock);
        addRecipe(StructMaterials.stonepillar);
        addRecipe(StructMaterials.woodfloor);
        addRecipe(StructMaterials.stonefloor);
        addRecipe(StructMaterials.torch);
    }
}
