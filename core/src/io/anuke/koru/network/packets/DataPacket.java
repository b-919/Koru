package io.anuke.koru.network.packets;

import io.anuke.koru.entities.*;
import io.anuke.koru.traits.DirectionTrait.*;
import java.util.*;

public class DataPacket{
    public ArrayList<Entity> entities;
    public int playerid;
    public float time;
    public float playerx, playery;
    public String name;
    public Direction direction;
}
