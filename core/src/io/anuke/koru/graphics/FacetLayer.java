package io.anuke.koru.graphics;

public abstract class FacetLayer {
    public String name;
    public float layer;
    public int offset;

    public FacetLayer(String name, float layer, int offset) {
        this.name = name;
        this.layer = layer;
        this.offset = offset;
    }

    public abstract boolean acceptFacet(Facet facet);
    public abstract boolean layerEquals(float f);

    /** Starts drawing this layer into its off-screen surface. */
    public void begin(){
        Graphics.surface(name);
    }

    /** Ends the current surface. */
    public void end(){
        Graphics.surface();
    }
}
