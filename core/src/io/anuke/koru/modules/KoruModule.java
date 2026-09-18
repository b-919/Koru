package io.anuke.koru.modules;

import arc.math.geom.Vec2;
import arc.util.Time;

public abstract class KoruModule {
	protected Vec2 vector = new Vec2();

	public void init(){}
	public void update(){}
	public void render(){}
	public void resize(int width, int height){}
	public void dispose(){}

	public float delta(){
		return Time.delta;
	}
}


