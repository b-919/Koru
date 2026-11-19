package io.anuke.koru.world;

import io.anuke.koru.entities.types.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.traits.DirectionTrait.*;

import java.util.Arrays;

/** A snapshot of a player that is stored with the chunk they logged out in. */
public class PlayerData{
    public String name;
    public float x, y;
    public int direction;
    public PlayerSlot[] inventory = {};
    public PlayerSlot selected;
    public int hotbar;
    public int recipe = -1;

    public static PlayerData create(Player player){
        PlayerData data = new PlayerData();
        data.name = player.name;
        data.x = player.pos.x;
        data.y = player.pos.y;
        data.direction = player.direction == null ? 0 : player.direction.ordinal();
        data.recipe = -1;

        InventoryTrait inv = player.inventory;
        if(inv != null && inv.inventory != null){
            data.inventory = new PlayerSlot[inv.inventory.length];

            for(int i = 0; i < inv.inventory.length; i++){
                data.inventory[i] = PlayerSlot.of(inv.inventory[i]);
            }

            data.selected = PlayerSlot.of(inv.selected);
            data.hotbar = inv.hotbar;
            data.recipe = inv.recipe;
        }

        return data;
    }

    public void apply(Player player){
        player.pos.set(x, y);
        player.direction = Direction.forOrdinal(direction);

        InventoryTrait inv = player.inventory;
        if(inv == null || inv.inventory == null){
            return;
        }

        Arrays.fill(inv.inventory, null);

        for(int i = 0; i < inv.inventory.length && i < inventory.length; i++){
            inv.inventory[i] = PlayerSlot.toStack(inventory[i]);
        }

        inv.selected = PlayerSlot.toStack(selected);
        inv.hotbar = Math.max(0, Math.min(hotbar, inv.inventory.length - 1));
        inv.recipe = recipe;
    }
}
