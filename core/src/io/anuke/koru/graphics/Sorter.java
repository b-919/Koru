package io.anuke.koru.graphics;

public enum Sorter{
    tile{
        @Override
        public int compare(Facet a, Facet b){
            if(b.provider != tile) return -1;
            if(a.getLayer() == b.getLayer()) return 0;
            return a.getLayer() < b.getLayer() ? 1 : -1;
        }
    },
    object{
        @Override
        public int compare(Facet a, Facet b){
            if(b.provider != object) return 1;
            if(a.getLayer() == b.getLayer()) return 0;
            return a.getLayer() < b.getLayer() ? 1 : -1;
        }
    };

    public static final float shadow = -999999;
    public static final float light = 999999;
    public static final float dark = 9999999;

    public abstract int compare(Facet a, Facet b);
}
