package io.anuke.koru.graphics;

import arc.struct.ObjectMap;

public class FacetMap{
	public ObjectMap<String, Facet> facets = new ObjectMap<>();

	public void add(String name, Facet facet){
		facets.put(name, facet);
	}

	public Facet get(String name){
		return facets.get(name);
	}
}


