package io.anuke.koru.ai;

import arc.math.geom.*;

public interface SmoothableGraphPath<N, V extends Vector<V>> extends Iterable<N>{
    int getCount();
    N get(int index);
    void swap(int i, int j);
    V getNodePosition(int index);
}
