package io.anuke.koru.ai;

import arc.struct.*;

public interface IndexedGraph<N> extends AiGraph<N>{
    int getIndex(N node);
    int getNodeCount();
}


