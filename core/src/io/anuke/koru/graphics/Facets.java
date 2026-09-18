package io.anuke.koru.graphics;

import arc.struct.Seq;

public class Facets{
	private static Facets instance = new Facets();
	private Seq<Facet> facets = new Seq<>();
	private boolean updated = false;

	public static Facets instance(){
		return instance;
	}

	public void add(Facet facet){
		updated = true;
		facets.add(facet);
	}

	public void remove(Facet facet){
		if(facets.remove(facet)){
			facet.onFree();
		}
	}

	public void renderAll(){
		if(updated){
			facets.sort();
			updated = false;
		}
		for(int i = 0; i < facets.size; i++){
			facets.get(i).draw();
		}
	}

	public Seq<Facet> getFacetArray(){
		return facets;
	}

	public void setLayerManager(Object manager){}
}
