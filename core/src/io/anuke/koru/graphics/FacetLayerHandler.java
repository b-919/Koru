package io.anuke.koru.graphics;

import arc.struct.*;

public class FacetLayerHandler{
    public Seq<FacetLayer> allDrawLayers = new Seq<>();

    public void drawRenderables(Iterable<Facet> iterable){
        Seq<FacetLayer> array = new Seq<>(allDrawLayers);
        FacetLayer layer = null;

        for(Facet facet : iterable){
            boolean switched = false;

            if(layer != null && !layer.acceptFacet(facet)){
                endBufferLayer(layer, array);
                layer = null;
                switched = true;
            }

            if(layer == null){
                for(FacetLayer l : array){
                    if(l.acceptFacet(facet)){
                        if(switched) facet.draw();
                        layer = l;
                        beginBufferLayer(layer);
                        break;
                    }
                }
            }

            facet.draw();
        }

        if(layer != null){
            endBufferLayer(layer, array);
        }
    }

    private void beginBufferLayer(FacetLayer layer){
        layer.begin();
    }

    private void endBufferLayer(FacetLayer layer, Seq<FacetLayer> array){
        layer.end();
        if(array != null) array.remove(layer, true);
    }
}