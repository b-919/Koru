package io.anuke.koru.ai;

import arc.struct.*;

public class Graph implements IndexedGraph<Long>{
	LongSeq nodes = new LongSeq();
	private Seq<AiConnection<Long>> tempConnections = new Seq<AiConnection<Long>>();
	
	@Override
	public Seq<AiConnection<Long>> getConnections(Long fromNode){
		//TODO return the connections
		return tempConnections;
	}

	@Override
	public int getIndex(Long node){
		//TODO return the actual index
		return 0;
	}

	@Override
	public int getNodeCount(){
		return nodes.size;
	}
	
	public void addNode(Long node){
		nodes.add(node);
	}
}


