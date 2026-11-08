package io.anuke.koru.ai;

import io.anuke.koru.modules.*;
import io.anuke.koru.utils.*;

public class ManhattanDistanceHueristic implements Heuristic<Long>{

    @Override
    public float estimate(Long node, Long other){
        float x = World.world(Bits.getLeftInt(node)), y = World.world(Bits.getRightInt(node));
        float x2 = World.world(Bits.getLeftInt(other)), y2 = World.world(Bits.getRightInt(other));
        return Math.abs(x2 - x) + Math.abs(y2 - y);
    }
}
