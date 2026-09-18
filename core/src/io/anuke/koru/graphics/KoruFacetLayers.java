package io.anuke.koru.graphics;

import arc.math.Mathf;

import io.anuke.koru.world.materials.MaterialTypes.Wall;

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
				
				boolean iswall = false;
				int pheight = 0;
				int py = 0;
				
				if(facet instanceof KoruFacet){
					KoruFacet k = (KoruFacet)facet;
					
					if(!k.reflect) continue;
					
					if(k.material instanceof Wall){
						iswall = true;
						Wall wall = (Wall)k.material;
						// pheight = k.sprite.getRegionHeight();
						// py = k.sprite.getRegionY();
						// k.sprite.setRegionY(k.sprite.getRegionY() + (k.sprite.getRegionHeight() - wall.height));
						// k.sprite.setRegionHeight(wall.height);
						// k.sprite.setSize(k.sprite.getWidth(), wall.height);
						
					}
				}
				
				Graphics.end();
				
				//TODO facet lags behind in Y axis while moving?
				
				// arc.Core.batch.getTransformMatrix().setToTranslation(0, facet.getLayer(), 0);
				// arc.Core.batch.getTransformMatrix().scale(1f, -1f, 0);
				// arc.Core.batch.getTransformMatrix().translate(0, -facet.getLayer(), 0);
				
				Graphics.begin();
				facet.draw();
				
				if(iswall){
					KoruFacet k = (KoruFacet)facet;
					// k.sprite.setRegionY(py);
					// k.sprite.setRegionHeight(pheight);
					// k.sprite.setSize(k.sprite.getWidth(), pheight);
				
				}
			}
			
			Graphics.end();
			Graphics.shader();
			
			// arc.Core.batch.getTransformMatrix().setToTranslation(0, 0, 0);
			Graphics.begin();
			
			Graphics.shader(Shaders.water);
			Graphics.flushSurface();
			Graphics.shader();
		}
	};
}
