package io.anuke.koru.network.syncing;

import io.anuke.koru.entities.Entity;

public class SyncData{
	public long id;
	public Object[] objects;

	public SyncData(Entity entity, Object... objects){
		this.id = entity.getID();
		this.objects = objects;
	}

	private SyncData(){}

	public <T> T get(int index){
		return (T)objects[index];
	}

	public float x(){
		return get(0);
	}

	public float y(){
		return get(1);
	}
}


