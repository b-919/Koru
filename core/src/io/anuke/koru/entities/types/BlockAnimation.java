package io.anuke.koru.entities.types;

import io.anuke.koru.graphics.Draw;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.Entity;
import io.anuke.koru.network.Net;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;

public class BlockAnimation extends Entity{
	{
		hasLifetime = true;
		lifetime = 50;
	}

	@Override
	public void draw(){
		if(matid == 0){
			throw new RuntimeException("No material specified in the data! Did you set the renderer material?");
		}

		Material material = material();

		Tile tile = Koru.world.getWorldTile(pos().x, pos().y);

		Draw.z(pos().y);
		Draw.alpha(fract());

		material.draw(tile);

		Draw.reset();
	}

	/**This sends the animation as well.*/
	public static void create(Material material, float x, float y){
		BlockAnimation entity = new BlockAnimation();
		entity.pos().set(x, y);
		entity.matid = material.id();
		Net.sendEntity(entity);
	}
}


