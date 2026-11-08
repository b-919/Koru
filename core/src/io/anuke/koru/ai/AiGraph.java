package io.anuke.koru.ai;

import arc.struct.*;

public interface AiGraph<N>{
    Seq<AiConnection<N>> getConnections(N fromNode);
}


