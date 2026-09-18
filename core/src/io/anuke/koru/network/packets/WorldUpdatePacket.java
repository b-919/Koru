package io.anuke.koru.network.packets;

import arc.struct.ObjectMap;

import io.anuke.koru.network.syncing.SyncData;

public class WorldUpdatePacket{
	public ObjectMap<Integer, SyncData> updates = new ObjectMap<>();
}
