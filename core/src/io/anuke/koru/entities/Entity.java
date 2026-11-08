package io.anuke.koru.entities;

import arc.graphics.*;
import arc.math.geom.*;
import arc.util.*;
import io.anuke.koru.input.*;
import io.anuke.koru.items.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.syncing.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.traits.DirectionTrait.*;
import io.anuke.koru.world.materials.*;

public class Entity{
    public int id = -1;

    public final Vec2 pos = new Vec2();
    public final Vec2 velocity = new Vec2();
    public float drag;
    public boolean hasVelocity;

    public float lifetime;
    public float life;
    public boolean hasLifetime;

    public int health = 100, maxhealth = 100;
    public boolean dead;

    public boolean hasCollider;
    public float cwidth = 10f, cheight = 10f;
    public float coffsetx, coffsety;

    public int contactDamage;

    public boolean tileCollides;
    public boolean tcolTrigger;
    public float tcolWidth = 4f, tcolHeight = 4f;
    public float tcolOffsetX, tcolOffsetY;

    public boolean chunkLoad;

    public SyncType syncType;
    public Interpolator interpolator;

    public boolean local;
    public int connectionID = -1;
    public String name;

    public Direction direction = Direction.front;
    public float walktime;
    public boolean walking;

    public InventoryTrait inventory;

    public InputHandler input;

    public int parent;

    public int matid;

    public ItemStack stack;

    public String text;
    public Color textColor = Color.white;

    public Vec2 pos(){
        return pos;
    }

    public Vec2 velocity(){
        return velocity;
    }

    public float angle(){
        return velocity().angle();
    }

    public float fract(){
        return 1f - life / lifetime;
    }

    public float ifract(){
        return life / lifetime;
    }

    public float sfract(){
        return 2f * (0.5f - Math.abs(ifract() - 0.5f));
    }

    public int getID(){
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public Material material(){
        return Material.getMaterial(matid);
    }

    /** Whether this entity should be unloaded when far from the player. Overridden by the player. */
    public boolean unload(){
        return true;
    }

    /** Called when added to the {@link Entities} container. */
    protected void onAdd(){

    }

    /** Called when removed from the {@link Entities} container. */
    protected void onRemove(){

    }

    public void add(){
        Entities.add(this);
    }

    public void remove(){
        Entities.remove(this);
    }

    /** Updates lifetime, velocity, interpolation and tile collision. */
    public void update(){
        float delta = Math.min(Time.delta, 3f);

        if(hasLifetime){
            life += delta;
            if(life > lifetime) remove();
        }

        if(hasVelocity){
            pos.add(velocity.x * delta, velocity.y * delta);
            velocity.scl(1f - drag * delta);
        }

        if(syncType != null && interpolator != null && !Net.server() && !local){
            interpolator.update(this);
        }

        if(tileCollides){
            TileCollision.check(this);
        }
    }

    /** Called when the entity's tile-collide box overlaps a solid tile. Overridden by projectiles. */
    public void onTileCollision(int tileX, int tileY){

    }

    /** Shadow rendering hook, called before world objects and entity sprites are drawn. */
    public void drawShadow(){

    }

    /** Rendering hook, called by the renderer. */
    public void draw(){

    }
}


