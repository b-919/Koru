package io.anuke.koru.entities.types;

import io.anuke.koru.graphics.Draw;

import io.anuke.koru.entities.Entity;
import io.anuke.koru.graphics.Drawf;
import io.anuke.koru.network.SyncType;
import io.anuke.koru.network.syncing.Interpolator;

public class TestEntity extends Entity{

	{
		hasCollider = true;
		cwidth = 7;
		cheight = 7;

		syncType = SyncType.physics;
		interpolator = new Interpolator();
	}

	@Override
	public void draw(){
		Draw.z(pos().y);

		Draw.rect("player", pos().x, pos().y);

		Drawf.shadow(this, 8, 0);
	}
}


