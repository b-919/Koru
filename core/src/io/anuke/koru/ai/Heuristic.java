package io.anuke.koru.ai;

public interface Heuristic<N>{
    float estimate(N node, N endNode);
}


