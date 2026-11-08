package io.anuke.koru.entities.types;

import arc.*;
import arc.util.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.utils.*;

public class DamageIndicator extends Entity{

    {
        hasLifetime = true;
        lifetime = 20;
    }

    @Override
    public void draw(){
        Entity parent = Entities.get(this.parent);
        if(parent == null) return;

        Draw.z(parent.pos().y - 1);

        Draw.color(1f, 1f, 1f, ifract());
        Resources.font2().draw(text, parent.pos().x,
                parent.pos().y + parent.cheight * 1.5f + life / 6f, 0, arc.util.Align.center, false);
        Draw.color();
    }
}
