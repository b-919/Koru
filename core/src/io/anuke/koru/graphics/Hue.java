package io.anuke.koru.graphics;

import arc.graphics.*;

public class Hue{
    public static Color lightness(float l){
        return new Color(l, l, l, 1f);
    }

    public static Color mix(Color c1, Color c2, float f){
        return c1.cpy().lerp(c2, f);
    }
}


