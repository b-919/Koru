package io.anuke.koru.systems;

import arc.util.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.modules.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.Net.*;
import io.anuke.koru.network.packets.*;

public class SyncSystem{
    static public float syncrange = 150;
    private static final Timekeeper syncTimer = Timekeeper.ofTicks(Network.packetFrequency);

    public void update(){
        if(!syncTimer.poll()){
            return;

        }
        for(Entity player : Entities.all()){
            if(player.connectionID == -1 || player.syncType == null) continue;

            WorldUpdatePacket packet = new WorldUpdatePacket();

            Entities.instance.getNearbySyncables(player.pos.x, player.pos.y, syncrange, (entity) -> {
                if(entity == player) return;
                packet.updates.put(entity.getID(), entity.syncType.write(entity));
            });

            if(packet.updates.size != 0){
                Net.sendTo(player.connectionID, packet, Mode.UDP);
            }
        }
    }

}


