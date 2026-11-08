package io.anuke.koru.network;

import arc.func.*;
import arc.net.*;
import arc.struct.*;
import io.anuke.koru.*;
import io.anuke.koru.entities.*;

public class Net{
    private static boolean server;
    private static NetProvider provider;
    private static ObjectMap<Class<?>, Cons<Object>> packetHandlers = new ObjectMap<>();

    public static void setProvider(boolean server, NetProvider provider){
        Net.server = server;
        Net.provider = provider;
    }

    public static boolean server(){
        return server;
    }

    public static void send(Object object){
        send(object, Mode.TCP);
    }

    public static void sendTo(int clientid, Object object){
        sendTo(clientid, object, Mode.TCP);
    }

    public static void sendExcept(int clientid, Object object){
        sendExcept(clientid, object, Mode.TCP);
    }

    public static void send(Object object, Mode mode){
        provider.send(object, mode);
    }

    public static void sendRange(Object object, float x, float y, float range, Mode mode){
        provider.sendRange(object, x, y, range, mode);
    }

    public static void sendTo(int clientid, Object object, Mode mode){
        provider.sendTo(clientid, object, mode);
    }

    public static void sendExcept(int clientid, Object object, Mode mode){
        provider.sendExcept(clientid, object, mode);
    }

    public static void sendEntity(Entity entity){
        provider.sendEntity(entity);
    }

    public static void removeEntity(Entity entity){
        provider.removeEntity(entity);
    }

    public static <T> void handle(Class<T> type, Cons<T> handler){
        packetHandlers.put(type, (Cons<Object>) handler);
    }

    public static void onRecieve(Object object){
        if(packetHandlers.containsKey(object.getClass())){
            packetHandlers.get(object.getClass()).get(object);
        }else if(!(object instanceof FrameworkMessage)){
            Koru.log("Unhandled packet type: " + object.getClass().getSimpleName());
        }
    }

    public static enum Mode{
        TCP, UDP;
    }

    public static interface NetProvider{

        public void send(Object object, Mode mode);

        public void sendRange(Object object, float x, float y, float range, Mode mode);

        public void sendTo(int clientid, Object object, Mode mode);

        public void sendExcept(int clientid, Object object, Mode mode);

        public void sendEntity(Entity entity);

        public void removeEntity(Entity entity);
    }
}
