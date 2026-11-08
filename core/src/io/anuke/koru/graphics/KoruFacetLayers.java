package io.anuke.koru.graphics;

import arc.math.Mathf;

public class KoruFacetLayers{
	public static final float waterLayer = 2;

	public static final FacetLayer

	water = new FacetLayer("water", waterLayer, 0){

		{
			Graphics.addSurface("reflection", 4, 3);
		}

		@Override
		public boolean acceptFacet(Facet facet){
			return layerEquals(facet.getLayer()) && facet.provider == Sorter.tile;
		}

		@Override
		public boolean layerEquals(float f){
			return Mathf.equal(f, layer, 2f);
		}

		@Override
		public void end(){
			Draw.color();

			Shaders.mix.color.set(0x29619bff);
			Shaders.mix.amount = 0.42f;
			Graphics.shader(Shaders.mix);

			arc.struct.Seq<Facet> facets = Facets.instance().getFacetArray();
			for(int i = 0; i < facets.size; i ++){
				Facet facet = facets.get(i);

				if(facet.provider != Sorter.object || facet instanceof ReflectionlessFacet){
					continue;
				}

				if(facet instanceof KoruFacet && !((KoruFacet)facet).reflect){
					continue;
				}

				Graphics.end();
				Graphics.begin();
				facet.draw();
			}

			Graphics.end();
			Graphics.shader();

			Graphics.begin();

			Graphics.shader(Shaders.water);
			Graphics.flushSurface();
			Graphics.shader();
		}
	};
}
