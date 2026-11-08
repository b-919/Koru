package io.anuke.koru.graphics;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;

public class Effects{
    private static final IntMap<Effect> effects = new IntMap<>();
    private static EffectProvider provider;

    public static void setEffectProvider(EffectProvider p){
        provider = p;
    }

    public static Effect getEffect(int id){
        return effects.get(id);
    }

    public static void effect(Effect effect, float x, float y){
        effect(effect, Color.white, x, y, 0);
    }

    public static void effect(Effect effect, Color color, float x, float y){
        effect(effect, color, x, y, 0);
    }

    public static void effect(Effect effect, Color color, float x, float y, float rotation){
        if(provider != null && effect != null) provider.create(effect, color, x, y, rotation);
    }

    public static void renderEffect(int id, Color color, float time, float rotation, float x, float y){
        Effect effect = effects.get(id);
        if(effect == null) return;

        EffectContainer container = new EffectContainer();
        container.id = id;
        container.color = color;
        container.time = time;
        container.lifetime = effect.lifetime;
        container.rotation = rotation;
        container.x = x;
        container.y = y;

        effect.drawer.get(container);
    }

    public interface EffectProvider{
        void create(Effect effect, Color color, float x, float y, float rotation);
    }

    public static class Effect{
        public final int id;
        private static int nextId;
        public final float lifetime;
        public final Cons<EffectContainer> drawer;

        public Effect(float lifetime, Cons<EffectContainer> drawer){
            this.id = nextId++;
            this.lifetime = lifetime;
            this.drawer = drawer;
            effects.put(id, this);
        }
    }

    public static class EffectContainer{
        public float x, y, time, lifetime, rotation;
        public Color color;
        public long id;

        public float fract(){ return time / lifetime; }
        public float ifract(){ return 1f - fract(); }
        public float powfract(){ return Mathf.pow(fract(), 2f); }
        public float sfract(){ return Mathf.sin(fract() * Mathf.PI); }
    }
}
