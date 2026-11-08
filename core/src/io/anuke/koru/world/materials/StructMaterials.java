package io.anuke.koru.world.materials;

import arc.graphics.*;
import io.anuke.koru.items.impl.*;
import io.anuke.koru.world.materials.MaterialTypes.*;
import io.anuke.koru.world.materials.StructMaterialTypes.*;

/** Artifical materials built by the player. */
public class StructMaterials{
    public static final Material

    //tiles

    woodfloor = new Floor("woodfloor"){{
        addDrop(Items.wood, 1);
        color = new Color(0x744a28ff);
        breaktime = 20;
        variants = 0;
    }},

    stonefloor = new Floor("stonefloor"){{
        addDrop(Items.stone, 1);
        color = new Color(0x717171ff);
        variants = 0;
        breaktime = 20;
    }},


    //Objects, blocks

    stonepillar = new Wall("stonepillar"){{
        addDrop(Items.stone, 2);
        color = new Color(0x717171ff);
        breaktime = 60;
    }},

    woodblock = new Wall("woodblock"){{
        addDrop(Items.wood, 2);
        color = new Color(0x744a28ff);
        breaktime = 40;
    }},

    torch = new Torch("torch"){{
        addDrop(Items.wood, 1);
        breaktime = 20;
    }},

    box = new Prop("box"){{
        addDrop(Items.wood, 10);
    }},
    barrel = new Prop("barrel"){{
        addDrop(Items.wood, 10);
        breaktime = 20;
        variants = 0;
        offset = -3;
    }},
    table = new Prop("table"){{
        addDrop(Items.wood, 10);
        breaktime = 20;
        variants = 0;
        offset = -1;
    }};

    public static void load(){}
}


