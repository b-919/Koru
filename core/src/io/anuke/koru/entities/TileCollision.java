package io.anuke.koru.entities;

import arc.math.Mathf;
import arc.math.geom.Rect;

/**
 * Simplified port of the uCore {@code TileCollisionProcessor} (jBump based).
 * Provides axis-separated sliding collision against solid world tiles.
 * Configured once at init with the world tilesize, collider and hitbox
 * provider, mirroring the original wiring.
 */
public class TileCollision{
	public static float tilesize;
	public static TileCollider collider;
	public static TileHitboxProvider hitbox;

	public interface TileCollider{
		boolean solid(int x, int y);
	}

	public interface TileHitboxProvider{
		void getHitbox(int x, int y, Rect out);
	}

	private static final int[] result = new int[2];

	/** Fires the tile-collision event for the entity if its box is overlapping a solid tile. */
	public static void check(Entity entity){
		if(collidesAt(entity, entity.pos.x, entity.pos.y, result)){
			entity.onTileCollision(result[0], result[1]);
		}
	}

	/** Axis-separated movement for the entity's tile-collide box. */
	public static void move(Entity entity, float mx, float my){
		if(entity.tcolTrigger){
			entity.pos.add(mx, my);
			return;
		}

		moveAxis(entity, mx, true);
		moveAxis(entity, my, false);
	}

	private static void moveAxis(Entity entity, float amount, boolean xAxis){
		if(Math.abs(amount) < 0.0001f) return;

		float nx = xAxis ? entity.pos.x + amount : entity.pos.x;
		float ny = xAxis ? entity.pos.y : entity.pos.y + amount;

		Rect moved = new Rect().setSize(entity.tcolWidth, entity.tcolHeight).setCenter(
				nx + entity.tcolOffsetX, ny + entity.tcolOffsetY);

		int minTx = Mathf.floor(moved.x / tilesize), maxTx = Mathf.floor((moved.x + moved.width) / tilesize);
		int minTy = Mathf.floor(moved.y / tilesize), maxTy = Mathf.floor((moved.y + moved.height) / tilesize);

		for(int tx = minTx; tx <= maxTx; tx++){
			for(int ty = minTy; ty <= maxTy; ty++){
				if(collider != null && !collider.solid(tx, ty)) continue;

				Rect block = new Rect();
				if(hitbox != null){
					hitbox.getHitbox(tx, ty, block);
				}else{
					block.set(tx * tilesize, ty * tilesize, tilesize, tilesize);
				}

				if(!moved.overlaps(block)) continue;

				if(xAxis){
					if(amount > 0){
						entity.pos.x = block.x - entity.tcolOffsetX - entity.tcolWidth / 2;
					}else{
						entity.pos.x = block.x + block.width - entity.tcolOffsetX + entity.tcolWidth / 2;
					}
					entity.velocity.x = 0;
				}else{
					if(amount > 0){
						entity.pos.y = block.y - entity.tcolOffsetY - entity.tcolHeight / 2;
					}else{
						entity.pos.y = block.y + block.height - entity.tcolOffsetY + entity.tcolHeight / 2;
					}
					entity.velocity.y = 0;
				}
				return;
			}
		}

		if(xAxis){
			entity.pos.x = nx;
		}else{
			entity.pos.y = ny;
		}
	}

	/** Whether the entity box at a position overlaps a solid tile. Writes the tile coords into {@code out}. */
	public static boolean collidesAt(Entity entity, float x, float y, int[] out){
		Rect rect = new Rect().setSize(entity.tcolWidth, entity.tcolHeight).setCenter(
				x + entity.tcolOffsetX, y + entity.tcolOffsetY);

		int minTx = Mathf.floor(rect.x / tilesize), maxTx = Mathf.floor((rect.x + rect.width) / tilesize);
		int minTy = Mathf.floor(rect.y / tilesize), maxTy = Mathf.floor((rect.y + rect.height) / tilesize);

		for(int tx = minTx; tx <= maxTx; tx++){
			for(int ty = minTy; ty <= maxTy; ty++){
				if(collider != null && !collider.solid(tx, ty)) continue;

				Rect block = new Rect();
				if(hitbox != null){
					hitbox.getHitbox(tx, ty, block);
				}else{
					block.set(tx * tilesize, ty * tilesize, tilesize, tilesize);
				}

				if(rect.overlaps(block)){
					out[0] = tx;
					out[1] = ty;
					return true;
				}
			}
		}
		return false;
	}
}


