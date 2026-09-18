package io.anuke.koru.graphics;

import arc.struct.Seq;

public class FacetList{
	public Seq<Facet> facets = new Seq<>();

	public void add(Facet facet){
		Facets.instance().add(facet);
		facets.add(facet);
	}

	public void free(){
		for(int i = 0; i < facets.size; i++){
			Facets.instance().remove(facets.get(i));
		}
		facets.clear();
	}
}
