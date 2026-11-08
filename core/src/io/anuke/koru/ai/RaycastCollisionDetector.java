package io.anuke.koru.ai;

import arc.math.geom.*;

public interface RaycastCollisionDetector<V extends Vector<V>>{
    boolean collides(Ray<V> ray);
    boolean findCollision(Collision<V> collision, Ray<V> ray);
}
