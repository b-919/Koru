package io.anuke.koru.modules;

import arc.*;
import arc.scene.*;
import arc.util.viewport.*;

public abstract class SceneModule extends KoruModule {
    public static final float mobileMinWidth = 1280f, mobileMinHeight = 720f;

    public Scene scene;

    public SceneModule(){
        if(Core.scene == null){
            Core.scene = Core.app != null && Core.app.isMobile() ? new Scene(new ExtendViewport(mobileMinWidth, mobileMinHeight)) : new Scene();
        }
        scene = Core.scene;
    }

    @Override
    public void update(){
        scene.act();
        scene.draw();
    }

    @Override
    public void resize(int width, int height){
        scene.resize(width, height);
    }
}


