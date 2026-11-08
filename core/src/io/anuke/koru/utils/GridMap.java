package io.anuke.koru.utils;

import arc.struct.*;

public class GridMap<T>{
    private IntMap<IntMap<T>> map = new IntMap<>();

    public void put(int x, int y, T value){
        if(!map.containsKey(x)) map.put(x, new IntMap<>());
        map.get(x).put(y, value);
    }

    public T get(int x, int y){
        if(!map.containsKey(x)) return null;
        return map.get(x).get(y);
    }

    public void clear(){
        map.clear();
    }
}


