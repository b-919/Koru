package io.anuke.koru;

import arc.*;
import arc.util.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.graphics.lsystems.*;
import io.anuke.koru.modules.*;
import io.anuke.koru.network.*;
import io.anuke.koru.utils.*;
import io.anuke.koru.world.*;

public class Koru extends KoruModuleCore implements arc.ApplicationListener{
    public static Renderer renderer;
    public static Control control;
    public static UI ui;
    public static World world;
    public static Network network;
    public static io.anuke.koru.ui.Skin skin;

    private static StringBuffer log = new StringBuffer();

    @Override
    public void init(){
        Core.camera = new arc.graphics.Camera();
        Core.batch = new arc.graphics.g2d.SpriteBatch();
        LSystems.generate();
        try{

            Resources.loadData();

            Entities.instance = new Entities();

            Effects.setEffectProvider((id, color, x, y, rotation)->{
                io.anuke.koru.entities.types.Effect effect = io.anuke.koru.entities.types.Effect.create(
                        id.id, color.cpy(), x, y, rotation);
                effect.add();
            });

            module(network = new Network());
            module(control = new Control());
            module(renderer = new Renderer());

            module(world = new World());
            module(ui = new UI());

            TileCollision.tilesize = World.tilesize;
            TileCollision.collider = (x, y)->{
                Tile tile = world.getTile(x, y);
                return tile != null && tile.solid();
            };
            TileCollision.hitbox = (x, y, out)->{
                Tile tile = world.getTile(x, y);
                tile.solidMaterial().getHitbox(x, y, out);
            };

            resize(arc.Core.graphics.getWidth(), arc.Core.graphics.getHeight());

            for(KoruModule module : modulearray){
                module.init();
            }

        }catch(Exception e){
            handleException(e);
        }
    }

    @Override
    public void update(){
        render();
    }

    @Override
    public void render(){

        Time.update();

        try{
            super.render();
            Entities.instance.update();
        }catch(Exception e){
            handleException(e);
        }
    }

    static void handleException(Exception e){
        e.printStackTrace();

        if(!System.getProperty("user.name").equals("anuke")){
            Core.files.local("korulog-" + System.currentTimeMillis() + ".log").writeString(e.toString() + "\n" + java.util.Arrays.toString(e.getStackTrace()), false);
        }
        Core.app.exit();
    }

    public static void log(Object o){
        StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        String name = e.getFileName().replace(".java", "");

        if(Net.server() || Core.app == null){
            if(Core.app == null){
                System.out.println(ColorCodes.backDefault + ColorCodes.bold + ColorCodes.lightBlue + "[" + name + "]: "
                        + ColorCodes.lightGreen + o + ColorCodes.red);
            }else{
                System.out.println(ColorCodes.backDefault + ColorCodes.lightBlue + "[" + ColorCodes.blue + name + ColorCodes.backDefault + "::"
                        + ColorCodes.lightYellow + e.getMethodName() + ColorCodes.lightBlue + "]"
                        + ColorCodes.lightGreen + o + ColorCodes.red);
            }
        }else{
            System.out.println("[" + name + "::" + e.getMethodName() + "] " + o);

            String message = "[" + name + "::" + e.getMethodName() + "]" + "" + o;
            log.append(message + "\n");
            int l = 500;


            log = new StringBuffer(log.substring(Math.max(0, log.length()-l), log.length()));
        }

        if(o instanceof Exception){
            ((Exception) o).printStackTrace();
        }
    }

    public static CharSequence getLog(){
        return log;
    }
}
