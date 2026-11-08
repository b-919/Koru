package io.anuke.koru.graphics;

import arc.Core;
import arc.graphics.Color;

import io.anuke.koru.graphics.Shader;

public class Shaders{
	public static final Outline outline = new Outline();
	public static final Inline inline = new Inline();
	public static final Water water = new Water();
	public static final Mix mix = new Mix();
	public static final Round round = new Round();
	public static final Light light = new Light();

	public static class Water extends Shader{

		public Water(){
			super("water", "default");
		}

		@Override
		public void apply(){
			setUniformf("camerapos", Core.camera.position.x, Core.camera.position.y);
			setUniformf("screensize", Core.graphics.getWidth() / 4f, Core.graphics.getHeight() / 4f);
			setUniformf("time", arc.util.Time.time);
		}
	}

	public static class Light extends Shader{

		public Light(){
			super("light", "default");
		}

		@Override
		public void apply(){

		}
	}

	public static class Round extends Shader{

		public Round(){
			super("round", "default");
		}

		@Override
		public void apply(){

		}
	}

	public static class Mix extends Shader{
		public Color color = new Color();
		public float amount = 0.5f;

		public Mix(){
			super("mix", "default");
		}

		@Override
		public void apply(){
			setUniformf("tint", color.r, color.g, color.b, color.a);
			setUniformf("amount", amount);
		}
	}

	public static class Outline extends Shader{
		public Color color = Color.white;

		public Outline(){
			super("outline", "outline");
		}

		@Override
		public void apply(){
			setUniformf("u_texsize", region.texture.width, region.texture.height);
			setUniformf("u_color", color.r, color.g, color.b, color.a);
		}
	}

	public static class Inline extends Shader{
		public Color color = Color.white;

		public Inline(){
			super("inline", "outline");
		}

		@Override
		public void apply(){
			setUniformf("u_texsize", region.texture.width, region.texture.height);
			setUniformf("u_size", region.width, region.height);
			setUniformf("u_pos", region.u * region.texture.width, region.v * region.texture.height);
			setUniformf("u_color", color.r, color.g, color.b, color.a);
		}
	}
}


