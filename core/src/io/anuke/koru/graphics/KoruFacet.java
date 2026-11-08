package io.anuke.koru.graphics;

import io.anuke.koru.utils.Listenable;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;

public class KoruFacet extends SpriteFacet{
	public boolean reflect = true;
	public Material material = null;
	public Listenable updater;

	public KoruFacet(){

	}

	public KoruFacet(String region){
		super(region);
	}

	public KoruFacet update(Listenable update){
		this.updater = update;
		return this;
	}

	public KoruFacet disableReflect(){
		reflect = false;
		return this;
	}

	public KoruFacet material(Material material){
		this.material = material;
		return this;
	}

	public KoruFacet region(String name){
		return (KoruFacet) super.region(Draw.region(name));
	}

	public KoruFacet generateShadow(){
		float sw = sprite != null ? sprite.width : 12f;
		String region = "shadow" + (int)(sw * 0.8f / 2f + Math.pow(sw, 1.5f) / 200f) * 2;

		return (KoruFacet)FacetPool.get(region)
						.set(x + sw/2f, y, true, true)
						.color(0, 0, 0, 0.3f).layer(Sorter.shadow);
	}

	public KoruFacet shadow(){
		return (KoruFacet) layer(Sorter.shadow);
	}

	public KoruFacet light(){
		return (KoruFacet) layer(Sorter.light);
	}

	public KoruFacet dark(){
		return (KoruFacet) layer(Sorter.dark);
	}

	public KoruFacet color(float r, float g, float b, float a){
		color.set(r, g, b, a);
		return this;
	}

	public KoruFacet setSize(float w, float h){
		super.setSize(w, h);
		return this;
	}

	public KoruFacet color(float r, float g, float b){
		color.set(r, g, b, 1f);
		return this;
	}

	public KoruFacet skewX(float skew){
		super.skewX(skew);
		return this;
	}

	public KoruFacet addShadow(FacetMap group){
		group.add("shadow", generateShadow());
		return this;
	}

	public KoruFacet addShadow(FacetList list){
		list.add(generateShadow());
		return this;
	}

	public KoruFacet addShadow(FacetList list, float offset){
		list.add(generateShadow().add(0, offset));
		return this;
	}

	public KoruFacet addShadow(FacetMap list, float offset){
		list.add("shadow", generateShadow().add(0, offset));
		return this;
	}

	public KoruFacet tile(Tile tile){
		super.set(tile.worldx(), tile.worldy());
		return this;
	}

	public KoruFacet set(float x, float y){
		super.set(x, y);
		return this;
	}

	public KoruFacet set(float x, float y, boolean center){
		return (KoruFacet)super.set(x, y, center, false);
	}

	public KoruFacet set(float x, float y, boolean center, boolean block){
		return (KoruFacet)super.set(x, y, center, block);
	}

	public KoruFacet centerX(){
		super.centerX();
		return this;
	}

	public KoruFacet sort(Sorter provider){
		super.sort(provider);
		return this;
	}

	public KoruFacet layer(float layer){
		super.layer(layer);
		return this;
	}

	public KoruFacet centerY(){
		super.centerY();
		return this;
	}

	public KoruFacet center(){
		super.center();
		return this;
	}

	@Override
	public void draw(){
		if(updater != null){
			updater.listen();
		}
		super.draw();
	}


	@Override
	public void onFree(){
		reflect = true;
		material = null;
		updater = null;
		FacetPool.free(this);
	}

}
