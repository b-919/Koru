package io.anuke.koru.modules;

import arc.*;
import arc.struct.*;

public abstract class KoruModuleCore implements ApplicationListener{
    public Seq<KoruModule> modulearray = new Seq<>();

    protected <T extends KoruModule> T module(T module){
        modulearray.add(module);
        return module;
    }

    @Override
    public void init(){
        for(KoruModule module : modulearray){
            module.init();
        }
    }

    @Override
    public void update(){
        for(KoruModule module : modulearray){
            module.update();
        }
    }

    public void render(){
        for(KoruModule module : modulearray){
            module.update();
        }
    }

    @Override
    public void resize(int width, int height){
        for(KoruModule module : modulearray){
            module.resize(width, height);
        }
    }

    @Override
    public void pause(){}

    @Override
    public void resume(){}

    @Override
    public void dispose(){
        for(KoruModule module : modulearray){
            module.dispose();
        }
    }
}



