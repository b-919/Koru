package io.anuke.koru.entities;

import io.anuke.koru.graphics.*;

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
        if(hiteffect != null) Effects.effect(Effects.getEffect(hiteffect), entity.pos.x, entity.pos.y);
    }

    /** Called when the projectile is despawned (lifetime expired). */
    public void despawned(Entity entity){
        if(despawneffect != null) Effects.effect(Effects.getEffect(despawneffect), entity.pos.x, entity.pos.y);
    }
}


