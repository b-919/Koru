package io.anuke.koru.ai;

import arc.math.geom.*;
import io.anuke.koru.modules.*;
import io.anuke.koru.utils.*;

public class TiledGraphPath extends GraphPath<Long> implements SmoothableGraphPath<Long, Vec2>{

    @Override
    public Vec2 getNodePosition(int index){
        long node = get(index);
        return new Vec2(World.world(Bits.getLeftInt(node)), World.world(Bits.getRightInt(node)));
    }

    @Override
    public int getCount(){
        return size;
    }

    @Override
    public void swap(int i, int j){
        Long temp = get(i);
        set(i, get(j));
        set(j, temp);
    }

    @Override
    public void truncate(int newLength){
        size = newLength;
    }
}


