package io.anuke.koru.ai;

import arc.math.geom.Vector;

public class Ray<V extends Vector<V>>{
	public V start;
	public V end;

	public Ray(V start, V end){
		this.start = start;
		this.end = end;
	}
}
