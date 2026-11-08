package io.anuke.koru.modules;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;

public abstract class RendererModule extends KoruModule {
    public Camera camera;
    public Batch batch;
    public TextureAtlas atlas;
    public Font font;
    public Vec2 screen = new Vec2();
    public boolean pixelate = false;

    public RendererModule(){
        camera = new Camera();
        batch = Core.batch;
        Core.camera = camera;
    }

    public void pixelate(){
        pixelate = true;
    }

    public void beginPixel(){}
    public void endPixel(){}
    public void clearScreen(){
        Core.graphics.clear(arc.graphics.Color.black);
    }

    public void smoothCamera(float targetX, float targetY, float lerp){
        camera.position.lerp(targetX, targetY, lerp);
    }

    public void record(){}

    @Override
    public void update(){
        screen.set(Core.graphics.getWidth(), Core.graphics.getHeight());
        draw();
    }

    public abstract void draw();

    @Override
    public void resize(int width, int height){
        camera.resize(width, height);
        screen.set(width, height);
    }
}


