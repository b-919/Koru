package io.anuke.koru.entities;

import arc.func.Boolf;
import arc.func.Cons;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;

import io.anuke.koru.Koru;
import io.anuke.koru.modules.World;

public class Entities{
	public static Entities instance;

	public static final float cellsize = World.tilesize * World.chunksize / 2;
	public static final int maxCells = 3000;

	private final Seq<Entity> entities = new Seq<>();
	private final IntMap<Entity> byId = new IntMap<>();
	private final ObjectMap<Long, Seq<Entity>> cells = new ObjectMap<>();
	private int lastID = 1;

	public static void add(Entity entity){
		instance.addEntity(entity);
	}

	public static void remove(Entity entity){
		instance.removeEntity(entity);
	}

	public static Entity get(int id){
		return instance.getEntity(id);
	}

	public static Seq<Entity> all(){
		return instance.entities;
	}

	public synchronized void addEntity(Entity entity){
		if(entity.id == -1){
			entity.id = lastID++;
		}

		byId.put(entity.id, entity);
		entities.add(entity);
		entity.onAdd();
	}

	public synchronized void removeEntity(Entity entity){
		if(byId.remove(entity.id) == null) return;
		entities.remove(entity, true);
		entity.onRemove();
	}

	public boolean removeEntity(int id){
		Entity entity = byId.get(id);
		if(entity == null) return false;
		removeEntity(entity);
		return true;
	}

	public boolean contains(Entity entity){
		return byId.get(entity.id) == entity && entities.contains(entity, true);
	}

	public int getCellAmount(){
		return cells.size;
	}

	public Entity getEntity(int id){
		return byId.get(id);
	}

	public void clear(){
		for(Entity entity : entities){
			entity.onRemove();
		}
		entities.clear();
		byId.clear();
		cells.clear();
	}

	public void update(){
		rebuildCells();

		for(Entity entity : entities){
			entity.update();
		}
	}

	private void rebuildCells(){
		if(cells.size > maxCells){
			Koru.log("Too many mapper cells (" + cells.size + "). Clearing cells.");
			cells.clear();
			System.gc();
		}

		for(Seq<Entity> set : cells.values()){
			set.clear();
		}

		for(Entity entity : entities){
			processEntity(entity);
		}
	}

	private void processEntity(Entity entity){
		int x = (int) (entity.pos.x / cellsize), y = (int) (entity.pos.y / cellsize);

		long key = cellKey(x, y);

		Seq<Entity> set = cells.get(key);

		if(set == null){
			cells.put(key, (set = new Seq<>()));
		}

		set.add(entity);
	}

	public synchronized void getNearby(float cx, float cy, float range, Boolf<Entity> pred, Cons<Entity> con){
		if(range < 1)
			throw new IllegalArgumentException("range cannot be negative.");

		int maxx = (int) ((cx + range) / cellsize), maxy = (int) ((cy + range) / cellsize),
				minx = (int) ((cx - range) / cellsize), miny = (int) ((cy - range) / cellsize);

		for(int x = minx; x < maxx + 1; x++){
			for(int y = miny; y < maxy + 1; y++){
				Seq<Entity> set = cells.get(cellKey(x, y));
				if(set != null){
					for(Entity e : set){
						if(pred.get(e) && Math.abs(e.pos.x - cx) < range && Math.abs(e.pos.y - cy) < range){
							con.get(e);
						}
					}
				}
			}
		}
	}

	public void getNearby(float cx, float cy, float range, Cons<Entity> con){
		getNearby(cx, cy, range, e -> true, con);
	}

	public void getNearbyConnections(float cx, float cy, float range, Cons<Entity> con){
		getNearby(cx, cy, range, e -> e.connectionID != -1, con);
	}

	public void getNearbySyncables(float cx, float cy, float range, Cons<Entity> con){
		getNearby(cx, cy, range, e -> e.syncType != null, con);
	}

	public static long cellKey(int x, int y){
		return ((long) x << 32) ^ (y & 0xffffffffL);
	}
}


