package io.anuke.koru.ai;

import arc.struct.LongMap;

import io.anuke.koru.Koru;
import io.anuke.koru.modules.World;
import io.anuke.koru.utils.Bits;
import io.anuke.koru.world.Tile;

public class Pathfinder{
	private static final ManhattanDistanceHueristic heuristic = new ManhattanDistanceHueristic();
	private static final int margin = 16;

	public static TiledGraphPath findPath(float x, float y, float targetx, float targety){
		TiledGraphPath path = new TiledGraphPath();
		if(Koru.world == null) return path;

		int startX = World.tile(x), startY = World.tile(y);
		int endX = World.tile(targetx), endY = World.tile(targety);

		int minX = Math.min(startX, endX) - margin, maxX = Math.max(startX, endX) + margin;
		int minY = Math.min(startY, endY) - margin, maxY = Math.max(startY, endY) + margin;

		Graph graph = new Graph();
		LongMap<Boolean> walkable = new LongMap<>();

		for(int tx = minX; tx <= maxX; tx++){
			for(int ty = minY; ty <= maxY; ty++){
				if(solid(tx, ty)) continue;

				long node = Bits.packInt(tx, ty);
				graph.addNode(node);
				walkable.put(node, true);
			}
		}

		long start = Bits.packInt(startX, startY);
		long end = Bits.packInt(endX, endY);

		if(walkable.get(start) == null || walkable.get(end) == null) return path;

		for(int tx = minX; tx <= maxX; tx++){
			for(int ty = minY; ty <= maxY; ty++){
				long from = Bits.packInt(tx, ty);
				if(walkable.get(from) == null) continue;

				connect(graph, walkable, from, tx + 1, ty);
				connect(graph, walkable, from, tx - 1, ty);
				connect(graph, walkable, from, tx, ty + 1);
				connect(graph, walkable, from, tx, ty - 1);
			}
		}

		new IndexedAStarPathFinder<Long>(graph).searchNodePath(start, end, heuristic, path);
		return path;
	}

	private static void connect(Graph graph, LongMap<Boolean> walkable, long from, int x, int y){
		long to = Bits.packInt(x, y);
		if(walkable.get(to) != null) graph.connect(from, to, 1f);
	}

	private static boolean solid(int x, int y){
		Tile tile = Koru.world.getTile(x, y);
		return tile == null || tile.solid();
	}
}
