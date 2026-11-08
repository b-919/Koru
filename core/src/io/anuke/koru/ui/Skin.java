package io.anuke.koru.ui;

import arc.graphics.g2d.TextureAtlas;
import arc.struct.ObjectMap;

public class Skin{
	public static Skin instance;
	private ObjectMap<String, ObjectMap<String, Object>> resources = new ObjectMap<>();

	public void add(String name, Object resource){
		add(resource.getClass().getSimpleName(), name, resource);
	}

	public void add(String type, String name, Object resource){
		if(!resources.containsKey(type)) resources.put(type, new ObjectMap<>());
		resources.get(type).put(name, resource);
	}

	public <T> T get(String name, Class<T> type){
		if(resources.containsKey(type.getSimpleName())){
			Object o = resources.get(type.getSimpleName()).get(name);
			if(o != null) return (T)o;
		}
		if(arc.Core.scene != null && arc.Core.scene.hasStyle(type)){
			return arc.Core.scene.getStyle(type);
		}
		return null;
	}

	public arc.graphics.g2d.Font getFont(String name){
		return get(name, arc.graphics.g2d.Font.class);
	}

	public void addRegions(TextureAtlas atlas){
		for(TextureAtlas.AtlasRegion region : atlas.getRegions()){
			add("Drawable", region.name, atlas.drawable(region.name));
		}
	}
}


