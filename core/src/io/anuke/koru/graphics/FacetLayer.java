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
    public void begin() {}
    public void end() {}
}
