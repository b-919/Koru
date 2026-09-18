package io.anuke.koru.graphics;

import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;

public class Effects{
	private static EffectProvider provider;

	public static void setEffectProvider(EffectProvider p){
		provider = p;
	}

	public static void effect(Effect effect, float x, float y){
		effect(effect, Color.white, x, y, 0);
	}

	public static void effect(Effect effect, Color color, float x, float y){
		effect(effect, color, x, y, 0);
	}

	public static void effect(Effect effect, Color color, float x, float y, float rotation){
		if(provider != null) provider.create(effect, color, x, y, rotation);
	}

	public interface EffectProvider{
		void create(Effect effect, Color color, float x, float y, float rotation);
	}

	public static class Effect{
		public final int id;
		private static int nextId = 0;
		public final float lifetime;
		public final Cons<EffectContainer> drawer;

		public Effect(float lifetime, Cons<EffectContainer> drawer){
			this.id = nextId++;
			this.lifetime = lifetime;
			this.drawer = drawer;
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


