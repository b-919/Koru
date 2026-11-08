package io.anuke.koru.ai;

import arc.struct.LongSeq;
import arc.struct.ObjectMap;
import arc.struct.Seq;

public class Graph implements IndexedGraph<Long>{
	private final LongSeq nodes = new LongSeq();
	private final ObjectMap<Long, Seq<AiConnection<Long>>> connections = new ObjectMap<>();
	private final Seq<AiConnection<Long>> empty = new Seq<>();

	@Override
	public Seq<AiConnection<Long>> getConnections(Long fromNode){
		Seq<AiConnection<Long>> result = connections.get(fromNode);
		return result == null ? empty : result;
	}

	@Override
	public int getIndex(Long node){
		return nodes.indexOf(node);
	}

	@Override
	public int getNodeCount(){
		return nodes.size;
	}

	public void addNode(Long node){
		if(!nodes.contains(node)) nodes.add(node);
	}

	public void connect(Long from, Long to, float cost){
		addNode(from);
		addNode(to);

		Seq<AiConnection<Long>> seq = connections.get(from);
		if(seq == null) connections.put(from, seq = new Seq<>());

		seq.add(new Connection(from, to, cost));
	}

	private static class Connection implements AiConnection<Long>{
		final Long from, to;
		final float cost;

		Connection(Long from, Long to, float cost){
			this.from = from;
			this.to = to;
			this.cost = cost;
		}

		@Override
		public float getCost(){
			return cost;
		}

		@Override
		public Long getFromNode(){
			return from;
		}

		@Override
		public Long getToNode(){
			return to;
		}
	}
}
