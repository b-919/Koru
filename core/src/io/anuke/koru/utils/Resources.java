package io.anuke.koru.utils;

import arc.graphics.g2d.*;
import arc.util.serialization.*;
import io.anuke.koru.items.impl.*;
import io.anuke.koru.world.materials.*;

import static arc.Core.*;

public class Resources{
    private static Json json;
    private static Font font;

    public static void loadData(){
        Materials.load();
        BlockRecipes.load();
        Recipes.load();
    }

    public static Font font(){
        if(font == null){
            font = new arc.graphics.g2d.Font(
                    arc.Core.files.internal("fonts/font.fnt"));
        }
        return font;
    }

    public static Font font2(){
        return io.anuke.koru.Koru.skin.getFont("pixel-font-noborder");
    }

    public static Font font3(){
        return io.anuke.koru.Koru.skin.getFont("pixel-font");
    }

    public static Font font(String name){
        return io.anuke.koru.Koru.skin.getFont(name);
    }

    public static Json getJson(){
        if(json == null) json = new Json();
        return json;
    }
}


