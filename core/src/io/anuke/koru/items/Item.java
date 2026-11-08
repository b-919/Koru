package io.anuke.koru.items;

import arc.struct.*;
import arc.util.*;
import io.anuke.koru.input.InputHandler.*;
import io.anuke.koru.world.*;

public class Item{
    private static int lastid;
    private static Seq<Item> items = new Seq<Item>();

    private final String name;
    private final ObjectSet<ItemType> types;
    private final int id;

    protected ObjectIntMap<BreakType> breakSpeeds = new ObjectIntMap<>();
    protected String formalName;
    protected int stackSize = 100;

    public static Item getItem(int id){
        return items.get(id);
    }

    public Item(String name, ItemType... types){
        this.name = name;
        this.types = ObjectSet.with(types);
        this.types.add(ItemType.any);
        id = lastid ++;
        items.add(this);
        formalName = Strings.capitalize(name);
    }

    public int getMaxStackSize(){
        return stackSize;
    }

    public boolean isType(ItemType type){
        return types.contains(type);
    }

    public String name(){
        return name;
    }

    public int getBreakSpeed(BreakType type){
        return breakSpeeds.get(type, 0);
    }

    public String formalName(){
        return formalName;
    }

    public int id(){
        return id;
    }

    public void onClickEvent(ClickEvent event){}
}
