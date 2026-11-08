package io.anuke.koru.network.packets;

import arc.struct.*;
import io.anuke.koru.network.syncing.*;

public class WorldUpdatePacket{
    public ObjectMap<Integer, SyncData> updates = new ObjectMap<>();
}
