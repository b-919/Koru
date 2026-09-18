package io.anuke.koru.entities.types;

import arc.graphics.Color;

import io.anuke.koru.entities.Entity;
import io.anuke.koru.world.materials.Material;

public class Particle extends Entity{
	public int colorstart, colorend;
	public Material material;
	public String particleName = "spark";
	public float gravity = 1f, velocity = 1f;

	public Particle set(String name, Color start, Color end){
		this.particleName = name;
		colorstart = start.rgba8888();
		colorend = end.rgba8888();
		return this;
	}

	public Particle set(Material material){
		this.material = material;
		return this;
	}

	public Particle set(String name, Color color){
		return set(name, color, color);
	}

	public Particle setSpeed(float gravity, float velocity){
		this.gravity = gravity;
		this.velocity = velocity;
		return this;
	}

	public Color getStartColor(){
		return new Color(colorstart);
	}

	public Color getEndColor(){
		return new Color(colorend);
	}
}
