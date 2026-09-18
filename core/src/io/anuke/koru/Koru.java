package io.anuke.koru;

import java.util.Calendar;

import arc.Core;

import io.anuke.koru.entities.Entities;
import io.anuke.koru.entities.TileCollision;
import io.anuke.koru.graphics.lsystems.LSystems;
import io.anuke.koru.modules.*;
import io.anuke.koru.network.Net;
import io.anuke.koru.utils.Resources;
import io.anuke.koru.world.Tile;
import io.anuke.koru.graphics.Effects;
import io.anuke.koru.modules.KoruModuleCore;
import arc.util.ColorCodes;
import arc.util.Time;

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
		//arc-backed chunks logic
		arc.Core.camera = new arc.graphics.Camera();
		arc.Core.batch = new arc.graphics.g2d.SpriteBatch();
		LSystems.generate();
		try{
			
			Resources.loadData();
			
			Entities.instance = new Entities();
			
			Effects.setEffectProvider((id, color, x, y, rotation)->{
				throw new IllegalArgumentException("Effects cannot be created clientside!");
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
			
		}catch (Exception e){
			handleException(e);
		}
		
		//engine.addSystem(new CollisionDebugSystem());
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

		if(Core.graphics.getFrameId() % 30 == 0){
			System.out.println("[DEBUG frame=" + Core.graphics.getFrameId() + " size=" + Core.graphics.getWidth() + "x" + Core.graphics.getHeight() + "]");
		}
	}
	
	static void handleException(Exception e){
		e.printStackTrace();
		
		//write log - useless for me, since I can just see the stack trace
		if(!System.getProperty("user.name").equals("anuke")){
			Core.files.local("korulog-" + Calendar.getInstance().getTime().toString().replace(":", "-") + ".log").writeString(e.toString() + "\n" + java.util.Arrays.toString(e.getStackTrace()), false);
		}
		//exit, nothing left to do here
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
