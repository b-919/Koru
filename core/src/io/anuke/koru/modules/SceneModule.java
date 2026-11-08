package io.anuke.koru.modules;

import arc.*;
import arc.scene.*;

public abstract class SceneModule extends KoruModule {
    public Scene scene;

    public SceneModule(){
        if(Core.scene == null){
            Core.scene = new Scene();
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


