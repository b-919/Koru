package io.anuke.koru.network.packets;

import io.anuke.koru.items.*;

public class InventoryUpdatePacket{
    public ItemStack[] stacks;
    public ItemStack selected;
    public int hotbar;
    public int recipe = -1;
}
