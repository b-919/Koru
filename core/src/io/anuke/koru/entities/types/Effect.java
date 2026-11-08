package io.anuke.koru.entities.types;

import arc.graphics.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.network.*;

public class Effect extends Entity{

    {
        hasLifetime = true;
    }

    public int effectId;
    public float effectRotation;
    public Color effectColor = Color.white;

    @Override
    protected void onAdd(){
        if(Net.server()){
            throw new IllegalArgumentException("Effect entities should not be added serverside!");
        }

        Effects.Effect effect = Effects.getEffect(effectId);
        if(effect != null) lifetime = effect.lifetime;
    }

    @Override
    public void draw(){
        Effects.renderEffect(effectId, effectColor, life, effectRotation, pos.x, pos.y);
    }

    /** Creates an effect entity without adding it to the container.
      *  Used by the server to send effect packets without owning the entity. */
    public static Effect create(int effectId, Color color, float x, float y){
        return create(effectId, color, x, y, 0);
    }

    public static Effect create(int effectId, Color color, float x, float y, float rotation){
        Effect effect = new Effect();
        effect.effectId = effectId;
        effect.effectColor = color;
        effect.effectRotation = rotation;
        effect.pos.set(x, y);
        return effect;
    }
}


