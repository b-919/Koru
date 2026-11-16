package io.anuke.koru.graphics;

import arc.math.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.entities.types.*;

public class KoruFacetLayers{
    public static final float waterLayer = 2;

    private static final Mat mirror = new Mat();

    public static final FacetLayer

    water = new FacetLayer("reflection", waterLayer, 0){

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

                mirror.idt().translate(0f, facet.getLayer()).scale(1f, -1f).translate(0f, -facet.getLayer());
                arc.graphics.g2d.Draw.trans(mirror);

                Draw.color();
                Graphics.begin();
                facet.draw();
            }

            Graphics.shader(Shaders.mix);
            if(Entities.instance != null){
                for(Entity entity : Entities.all()){
                    if(entity instanceof Effect || entity instanceof DamageIndicator) continue;

                    Graphics.end();

                    mirror.idt().translate(0f, entity.pos().y).scale(1f, -1f).translate(0f, -entity.pos().y);
                    arc.graphics.g2d.Draw.trans(mirror);

                    Draw.color();
                    Graphics.begin();
                    entity.draw();
                }
            }

            Graphics.end();
            Graphics.shader();

            mirror.idt();
            arc.graphics.g2d.Draw.trans(mirror);
            Draw.color();

            Graphics.begin();

            Graphics.shader(Shaders.water);
            Graphics.flushSurface();
            Graphics.shader();
        }
    };
}
