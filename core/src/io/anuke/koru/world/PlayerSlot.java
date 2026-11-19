package io.anuke.koru.world;

import io.anuke.koru.items.*;

/** A saved inventory slot. Items are stored by name so item, this to prevent corruption by mismatched ids. */
public class PlayerSlot{
    public String item;
    public int amount;

    public static PlayerSlot of(ItemStack stack){
        if(stack == null || stack.item == null || stack.amount <= 0){
            return null;
        }

        PlayerSlot slot = new PlayerSlot();
        slot.item = stack.item.name();
        slot.amount = stack.amount;
        return slot;
    }

    public static ItemStack toStack(PlayerSlot slot){
        if(slot == null || slot.item == null || slot.amount <= 0){
            return null;
        }

        Item item = Item.getByName(slot.item);
        return item == null ? null : new ItemStack(item, slot.amount);
    }
}
