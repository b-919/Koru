package io.anuke.koru.ai;

import arc.struct.Seq;

import java.util.Arrays;
import java.util.PriorityQueue;

public class IndexedAStarPathFinder<N>{
	private final IndexedGraph<N> graph;

	private float[] gScore = new float[0];
	private int[] parent = new int[0];
	private boolean[] closed = new boolean[0];
	private Object[] nodes = new Object[0];

	public IndexedAStarPathFinder(IndexedGraph<N> graph){
		this.graph = graph;
	}

	public boolean searchNodePath(N startNode, N endNode, Heuristic<N> heuristic, Seq<N> outPath){
		int count = graph.getNodeCount();
		if(count <= 0) return false;

		ensureCapacity(count);

		Arrays.fill(gScore, Float.MAX_VALUE);
		Arrays.fill(parent, -1);
		Arrays.fill(closed, false);
		Arrays.fill(nodes, null);

		int start = graph.getIndex(startNode);
		int end = graph.getIndex(endNode);
		if(start < 0 || end < 0 || start >= count || end >= count) return false;

		nodes[start] = startNode;
		nodes[end] = endNode;
		gScore[start] = 0f;

		PriorityQueue<Record<N>> open = new PriorityQueue<>((a, b) -> Float.compare(a.f, b.f));
		open.add(new Record<>(startNode, start, heuristic.estimate(startNode, endNode)));

		while(!open.isEmpty()){
			Record<N> current = open.poll();
			if(closed[current.index]) continue;
			if(current.index == end){
				reconstruct(start, end, outPath);
				return true;
			}

			closed[current.index] = true;

			Seq<AiConnection<N>> connections = graph.getConnections(current.node);
			if(connections == null) continue;

			for(AiConnection<N> connection : connections){
				N next = connection.getToNode();
				int nextIndex = graph.getIndex(next);
				if(nextIndex < 0 || nextIndex >= count || closed[nextIndex]) continue;

				float score = gScore[current.index] + connection.getCost();
				if(score < gScore[nextIndex]){
					gScore[nextIndex] = score;
					parent[nextIndex] = current.index;
					nodes[nextIndex] = next;
					open.add(new Record<>(next, nextIndex, score + heuristic.estimate(next, endNode)));
				}
			}
		}

		return false;
	}

	private void reconstruct(int start, int end, Seq<N> outPath){
		Seq<N> reversed = new Seq<>();
		int current = end;

		while(current != -1){
			@SuppressWarnings("unchecked")
			N node = (N)nodes[current];
			reversed.add(node);
			if(current == start) break;
			current = parent[current];
		}

		outPath.clear();
		for(int i = reversed.size - 1; i >= 0; i--){
			outPath.add(reversed.get(i));
		}
	}

	private void ensureCapacity(int size){
		if(gScore.length >= size) return;

		gScore = new float[size];
		parent = new int[size];
		closed = new boolean[size];
		nodes = new Object[size];
	}

	private static class Record<N>{
		final N node;
		final int index;
		final float f;

		Record(N node, int index, float f){
			this.node = node;
			this.index = index;
			this.f = f;
		}
	}
}
