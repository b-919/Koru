package io.anuke.koru.entities;

public abstract class ProjectileType{
	public float lifetime = 100;
	public float speed = 1;
	public int damage = 1;
	public float hitsize = 4;

	public Integer hiteffect;
	public Integer despawneffect;

	public abstract void draw(Entity entity);

	public void update(Entity entity){

	}

	/** Called when the projectile hits an entity. */
	public void removed(Entity entity){
		// TODO spawn hiteffect once Effects subsystem is ported
	}

	/** Called when the projectile is despawned (lifetime expired). */
	public void despawned(Entity entity){
		// TODO spawn despawneffect once Effects subsystem is ported
	}
}


