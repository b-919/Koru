package io.anuke.koru.graphics;

import arc.func.*;

public class BaseFacet extends Facet{
    public Cons<BaseFacet> drawer;

    public BaseFacet(Cons<BaseFacet> drawer){
        this.drawer = drawer;
    }

    public BaseFacet(float layer, Sorter provider, Cons<BaseFacet> drawer){
        this.layer = layer;
        this.provider = provider;
        this.drawer = drawer;
    }

    public BaseFacet add(){
        Facets.instance().add(this);
        return this;
    }

    @Override
    public void draw(){
        drawer.get(this);
    }

    @Override
    public void reset(){
        super.reset();
        drawer = null;
    }
}


