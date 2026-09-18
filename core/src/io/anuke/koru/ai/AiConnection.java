package io.anuke.koru.ai;

public interface AiConnection<N>{
	float getCost();
	N getFromNode();
	N getToNode();
}


