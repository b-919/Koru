package io.anuke.koru.graphics;

import arc.Core;
import io.anuke.koru.graphics.Draw;

import io.anuke.koru.entities.Entity;

public class Drawf{

	public static boolean hasRegion(String name){
		return Core.atlas.has(name);
	}

	/** Draws the static shadow sprite used by the old facet shadow. */
	public static void shadow(Entity entity, int size, float offset){
		shadow(entity, size, offset, false);
	}

	/** Draws the static shadow sprite, optionally snapped to integer coordinates. */
	public static void shadow(Entity entity, int size, float offset, boolean snap){
		String region = "shadow" + (int) ((size * 0.8f / 2f) + Math.pow(size, 1.5) / 200.0) * 2;

		Draw.z(-999999);
		Draw.color();

		float x = snap ? (int) entity.pos.x : entity.pos.x;
		float y = snap ? (int) entity.pos.y : entity.pos.y;

		Draw.rect(region, x, y + offset);
	}
}


