package io.anuke.koru.ai;

import arc.math.geom.*;
import io.anuke.koru.world.*;

public class Raycaster implements RaycastCollisionDetector<Vec2>{

    @Override
    public boolean collides(Ray<Vec2> ray){
        return Physics.cast(ray.start.x, ray.start.y, ray.end.x, ray.end.y);
    }

    @Override
    public boolean findCollision(Collision<Vec2> collision, Ray<Vec2> ray){
        Vec2 v = Physics.vectorCast(ray.start.x, ray.start.y, ray.end.x, ray.end.y);
        if(v == null) return false;
        collision.point.set(v.x, v.y);
        collision.normal.set(v.x, v.y).nor();
        return true;
    }
}


