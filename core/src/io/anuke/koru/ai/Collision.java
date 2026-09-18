package io.anuke.koru.ai;

import arc.math.geom.Vector;

public class Collision<V extends Vector<V>>{
	public V point;
	public V normal;

	public Collision(V point, V normal){
		this.point = point;
		this.normal = normal;
	}
}
