package io.anuke.koru.entities;

import arc.math.geom.*;
import io.anuke.koru.modules.*;

/** Axis-separated sliding collision against solid world tiles. Tiles are centered on their world coordinates. */
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
    private static final Rect blockTmp = new Rect();

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

        float ox = entity.pos.x, oy = entity.pos.y;
        float hx = entity.tcolWidth / 2f, hy = entity.tcolHeight / 2f;
        float offX = entity.tcolOffsetX, offY = entity.tcolOffsetY;

        float left = ox + offX - hx, bottom = oy + offY - hy;
        float right = left + entity.tcolWidth, top = bottom + entity.tcolHeight;

        if(xAxis){
            int minTx = World.tile(Math.min(left, left + amount)) - 1;
            int maxTx = World.tile(Math.max(right, right + amount)) + 1;
            int minTy = World.tile(bottom) - 1;
            int maxTy = World.tile(top) + 1;

            float nearest = amount > 0 ? Float.MAX_VALUE : -Float.MAX_VALUE;
            boolean hit = false;

            for(int tx = minTx; tx <= maxTx; tx++){
                for(int ty = minTy; ty <= maxTy; ty++){
                    if(!solid(tx, ty)) continue;

                    Rect block = getHitbox(tx, ty);
                    if(block.y >= top - 0.0001f || block.y + block.height <= bottom + 0.0001f) continue;

                    if(amount > 0){
                        if(block.x >= right - 0.0001f || block.x + block.width > left + 0.0001f){
                            nearest = Math.min(nearest, block.x);
                            hit = true;
                        }
                    }else{
                        if(block.x + block.width <= left + 0.0001f || block.x < right - 0.0001f){
                            nearest = Math.max(nearest, block.x + block.width);
                            hit = true;
                        }
                    }
                }
            }

            if(hit){
                entity.pos.x = amount > 0 ? nearest - offX - hx : nearest - offX + hx;
                entity.velocity.x = 0;
            }else{
                entity.pos.x = ox + amount;
            }
        }else{
            int minTx = World.tile(left) - 1;
            int maxTx = World.tile(right) + 1;
            int minTy = World.tile(Math.min(bottom, bottom + amount)) - 1;
            int maxTy = World.tile(Math.max(top, top + amount)) + 1;

            float nearest = amount > 0 ? Float.MAX_VALUE : -Float.MAX_VALUE;
            boolean hit = false;

            for(int tx = minTx; tx <= maxTx; tx++){
                for(int ty = minTy; ty <= maxTy; ty++){
                    if(!solid(tx, ty)) continue;

                    Rect block = getHitbox(tx, ty);
                    if(block.x >= right - 0.0001f || block.x + block.width <= left + 0.0001f) continue;

                    if(amount > 0){
                        if(block.y >= top - 0.0001f || block.y + block.height > bottom + 0.0001f){
                            nearest = Math.min(nearest, block.y);
                            hit = true;
                        }
                    }else{
                        if(block.y + block.height <= bottom + 0.0001f || block.y < top - 0.0001f){
                            nearest = Math.max(nearest, block.y + block.height);
                            hit = true;
                        }
                    }
                }
            }

            if(hit){
                entity.pos.y = amount > 0 ? nearest - offY - hy : nearest - offY + hy;
                entity.velocity.y = 0;
            }else{
                entity.pos.y = oy + amount;
            }
        }
    }

    /** Whether the entity box at a position overlaps a solid tile. Writes the tile coords into {@code out}. */
    public static boolean collidesAt(Entity entity, float x, float y, int[] out){
        Rect rect = new Rect().setSize(entity.tcolWidth, entity.tcolHeight).setCenter(
                x + entity.tcolOffsetX, y + entity.tcolOffsetY);

        int minTx = World.tile(rect.x) - 1, maxTx = World.tile(rect.x + rect.width) + 1;
        int minTy = World.tile(rect.y) - 1, maxTy = World.tile(rect.y + rect.height) + 1;

        for(int tx = minTx; tx <= maxTx; tx++){
            for(int ty = minTy; ty <= maxTy; ty++){
                if(!solid(tx, ty)) continue;

                if(rect.overlaps(getHitbox(tx, ty))){
                    out[0] = tx;
                    out[1] = ty;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean solid(int x, int y){
        return collider != null && collider.solid(x, y);
    }

    private static Rect getHitbox(int x, int y){
        blockTmp.set(x * tilesize - tilesize / 2f, y * tilesize - tilesize / 2f, tilesize, tilesize);
        if(hitbox != null){
            hitbox.getHitbox(x, y, blockTmp);
        }
        return blockTmp;
    }
}
