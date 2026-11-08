package io.anuke.koru.graphics;

import arc.struct.Seq;

public class Facets{
	private static Facets instance = new Facets();
	private Seq<Facet> facets = new Seq<>();
	private Seq<Facet> renderBuffer = new Seq<>();
	private FacetLayerHandler manager;
	private boolean updated = false;

	public static Facets instance(){
		return instance;
	}

	public void setLayerManager(FacetLayerHandler manager){
		this.manager = manager;
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
		sort();
		draw(facets);
	}

	/** Draws floors and tile-layer facets. Shadows are queued by this pass. */
	public void renderTiles(){
		sort();
		renderBuffer.clear();
		for(int i = 0; i < facets.size; i++){
			Facet facet = facets.get(i);
			if(facet.provider == Sorter.tile) renderBuffer.add(facet);
		}
		draw(renderBuffer);
	}

	/** Draws walls, props and other object-layer facets. */
	public void renderObjects(){
		sort();
		renderBuffer.clear();
		for(int i = 0; i < facets.size; i++){
			Facet facet = facets.get(i);
			if(facet.provider != Sorter.tile) renderBuffer.add(facet);
		}
		draw(renderBuffer);
	}

	private void sort(){
		if(updated){
			facets.sort();
			updated = false;
		}
	}

	private void draw(Seq<Facet> list){
		if(manager != null){
			manager.drawRenderables(list);
		}else{
			for(int i = 0; i < list.size; i++){
				list.get(i).draw();
			}
		}
	}

	public Seq<Facet> getFacetArray(){
		return facets;
	}
}
