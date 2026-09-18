package io.anuke.koru.graphics;

import arc.util.pooling.Pools;

import io.anuke.koru.graphics.Facet;

public class FacetPool{

	public static KoruFacet get(String region){
		return Pools.obtain(KoruFacet.class, KoruFacet::new).region(region);
	}

	public static void free(Facet r){
		if(r.getClass().isAnonymousClass()) return;

		Pools.free(r);
	}
}
