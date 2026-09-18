package io.anuke.koru.graphics;

import arc.math.geom.Position;
import arc.util.pooling.Pool.Poolable;

public abstract class Facet implements Poolable, Comparable<Facet>, Position{
	public float x, y, layer;
	public Sorter provider = Sorter.tile;

	public abstract void draw();

	public Facet set(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}

	public Facet layer(float layer){
		this.layer = layer;
		return this;
	}

	public Facet sort(Sorter provider){
		this.provider = provider;
		return this;
	}

	@Override
	public float getX(){
		return x;
	}

	@Override
	public float getY(){
		return y;
	}

	@Override
	public int compareTo(Facet other){
		if(provider != other.provider)
			return Integer.compare(provider.ordinal(), other.provider.ordinal());
		return Float.compare(layer, other.layer);
	}

	@Override
	public void reset(){
		x = y = layer = 0;
		provider = Sorter.tile;
	}

	public void onFree(){}

	public Facet add(FacetList list){
		list.add(this);
		return this;
	}

	public Facet add(){
		Facets.instance().add(this);
		return this;
	}

	public float getLayer(){
		return layer;
	}
}


