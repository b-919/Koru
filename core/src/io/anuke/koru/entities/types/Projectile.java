package io.anuke.koru.entities.types;

import io.anuke.koru.entities.Entity;
import io.anuke.koru.entities.ProjectileType;

public class Projectile extends Entity{

	{
		hasVelocity = true;
		hasLifetime = true;
		hasCollider = true;
		tileCollides = true;
		tcolWidth = 2;
		tcolHeight = 2;
	}

	public ProjectileType type;
	public Entity source;
	public boolean customDamage;

	@Override
	protected void onAdd(){
		if(type == null){
			throw new IllegalArgumentException("Projectile type is null! Set a value before adding the Projectile to anything.");
		}

		if(!customDamage){
			contactDamage = type.damage;
		}

		velocity.setLength(type.speed);
		cwidth = cheight = type.hitsize;
		lifetime = type.lifetime;
	}

	@Override
	public void update(){
		super.update();
		if(type != null) type.update(this);
	}

	@Override
	public void onTileCollision(int tileX, int tileY){
		type.removed(this);
		remove();
	}

	@Override
	public void draw(){
		if(type != null) type.draw(this);
	}

	/**Shortcut method.*/
	public static Projectile create(ProjectileType type, Entity source, float x, float y, float angle){
		return create(type, source, -1, x, y, angle);
	}

	public static Projectile create(ProjectileType type, Entity source, int damage, float x, float y, float angle){
		Projectile projectile = new Projectile();
		projectile.type = type;
		projectile.source = source;
		projectile.velocity.set(1, 1).setAngle(angle);
		if(damage != -1){
			projectile.contactDamage = damage;
			projectile.customDamage = true;
		}
		projectile.pos.set(x, y);
		projectile.add();
		return projectile;
	}
}


