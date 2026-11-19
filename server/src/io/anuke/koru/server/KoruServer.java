package io.anuke.koru.server;

import arc.backend.sdl.*;
import arc.math.*;
import arc.net.*;
import arc.struct.*;
import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.items.*;
import io.anuke.koru.items.impl.*;
import io.anuke.koru.modules.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.Net.*;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.server.world.*;
import io.anuke.koru.systems.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.utils.*;
import io.anuke.koru.world.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class KoruServer implements NetProvider{
    //maps connection IDs to entity IDs
    IntIntMap connections = new IntIntMap();
    IntMap<Connection> netConnections = new IntMap<>();

    Server server;
    KoruUpdater updater;
    CommandHandler commands;

    private Connection connection;

    private void setup(){
        Net.setProvider(true, this);
        registerHandlers();

        Resources.loadData();

        Effects.setEffectProvider((id, color, x, y, rotation) -> {
            Effect effect = Effect.create(id.id, new arc.graphics.Color(color.r, color.g, color.b, color.a), x, y);
            sendEntity(effect);
        });

        commands = new CommandHandler(this);

        createUpdater();

        try{
            KryoSerialization serialization = new KryoSerialization();
            server = new Server(16384 * 256, 16384 * 256, serialization);
            Registrator.register(serialization.getKryo());
            server.addListener(new Listen(this));
            server.start();
            server.bind(Network.port, Network.port);

            Koru.log("Server up.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createUpdater(){
        updater = new KoruUpdater(this);

        Thread thread = (new Thread(() -> {
            updater.run();
        }));

        thread.setDaemon(true);
        thread.start();
    }

    private void createMapGraphics(){
        SdlConfig config = new SdlConfig();
        config.disableAudio = true;
        config.title = "Map Preview";
        config.maximized = true;

        new SdlApplication((new MapPreview()), config);
    }

    private void registerHandlers(){

        Net.handle(PositionPacket.class, packet -> {
            Player player = getPlayer(connection.getID());

            player.pos.set(packet.x, packet.y);
            player.direction = packet.direction;
            player.input.mouseangle = packet.mouseangle;
        });

        Net.handle(EntityRequestPacket.class, packet -> {
            Entity entity = Entities.instance.getEntity(packet.id);
            if(entity != null){
                sendTo(connection.getID(), entity, Mode.TCP);
            }
        });

        Net.handle(ChatPacket.class, packet -> {
            packet.sender = getPlayer(connection.getID()).name;
            send(packet, Mode.TCP);
        });

        Net.handle(ChunkRequestPacket.class, packet -> {
            sendTo(connection.getID(), updater.world.createChunkPacket(packet), Mode.UDP);
        });

        Net.handle(InputPacket.class, packet -> {
            getPlayer(connection.getID()).input.inputEvent(packet.type, packet.data);
        });

        Net.handle(SlotChangePacket.class, packet -> {
            Player player = getPlayer(connection.getID());

            InventoryTrait inv = player.inventory;
            packet.slot = Mathf.clamp(packet.slot, 0, 3);
            inv.hotbar = packet.slot;
            packet.id = player.getID();
            packet.stack = inv.inventory[inv.hotbar];

            sendExcept(connection.getID(), packet, Mode.TCP);
        });

        Net.handle(RecipeSelectPacket.class, packet -> {
            getPlayer(connection.getID()).inventory.recipe = packet.recipe;
        });

        Net.handle(BlockInputPacket.class, packet -> {
            Koru.world.getTile(packet.x, packet.y).wallid = (short)packet.material;
            Koru.world.updateTile(Koru.world.getTile(packet.x, packet.y));
        });

        Net.handle(BlockQueryPacket.class, packet -> {
            Tile tile = Koru.world.getTile(packet.x, packet.y);
            Koru.log("Tile: " + tile);
        });

        Net.handle(InventoryClickPacket.class, packet -> {
            Player player = getPlayer(connection.getID());

            InventoryTrait inv = player.inventory;
            inv.clickSlot(packet.index);
            inv.sendUpdate(player);
        });

        Net.handle(CraftItemPacket.class, packet -> {
            Player player = getPlayer(connection.getID());
            if(player != null){
                craftItem(player, packet.recipe);
            }
        });
    }

    private void craftItem(Player player, int recipeId){
        Recipe recipe = Recipe.getRecipe(recipeId);
        if(recipe == null){
            return;
        }

        InventoryTrait inv = player.inventory;
        if(!inv.hasAll(recipe.requirements())){
            return;
        }

        inv.removeAll(recipe.requirements());

        ItemStack remaining = inv.addItem(new ItemStack(recipe.result(), recipe.resultAmount()));
        if(remaining != null && remaining.amount > 0){
            ItemDrop.create(player.pos.x, player.pos.y, new ItemStack[]{remaining});
        }

        inv.sendUpdate(player);
    }

    public void connectPacketRecieved(ConnectPacket packet, Connection connection){

        try{
            Koru.log("Connect packet recieved...");
            String name = uniqueName(packet.name);
            PlayerData saved = updater.file.getPlayerData(name);
            Player player = new Player();
            player.connectionID = connection.getID();
            player.name = name;
            if(saved != null){
                saved.apply(player);
                Koru.log("Restored player data for " + name + ".");
            }

            DataPacket data = new DataPacket();
            data.time = Koru.world.time;
            data.playerx = player.pos.x;
            data.playery = player.pos.y;
            data.direction = player.direction;
            data.name = name;

            ArrayList<Entity> entities = new ArrayList<Entity>();

            Entities.instance.getNearby(player.pos.x, player.pos.y, SyncSystem.syncrange, entity -> {
                entities.add(entity);
            });

            data.entities = entities;

            player.add();
            data.playerid = player.getID();

            sendTo(connection.getID(), data, Mode.TCP);
            sendExcept(connection.getID(), player, Mode.TCP);

            if(saved == null){
                initialItems(player);
            }
            player.inventory.sendUpdate(player);

            connections.put(connection.getID(), player.getID());

            sendChatMessage("[GREEN]" + name + " [CHARTREUSE]has connected.");
            Koru.log("spark ID is " + player.getID() + ", connection ID is " + player.connectionID);
            Koru.log(name + " has joined.");
        }catch(Exception e){
            e.printStackTrace();
            Koru.log("Critical error: failed sending player!");
            System.exit(1);
        }
    }

    public void initialItems(Player player){
        InventoryTrait inv = player.inventory;
        inv.addItem(new ItemStack(Items.axe));
        inv.addItem(new ItemStack(Items.hammer));
        inv.addItem(new ItemStack(Items.stick, 10));
        inv.addItem(new ItemStack(Items.stone, 10));
    }

    private String uniqueName(String requested){
        String base = requested == null ? "" : requested.trim();
        if(base.isEmpty()){
            base = "player";
        }

        if(!nameOnline(base)){
            return base;
        }

        int number = 1;
        while(nameOnline(base + number) || updater.file.hasPlayerData(base + number)){
            number++;
        }
        return base + number;
    }

    private boolean nameOnline(String name){
        for(Entity entity : Entities.all()){
            if(entity instanceof Player && name.equals(entity.name)){
                return true;
            }
        }
        return false;
    }

    public void disconnected(int id){
        try{
            if(!connections.containsKey(id)){
                Koru.log("An unknown player has disconnected.");
                return;
            }

            Player player = getPlayer(id);

            if(player != null){
                try{
                    updater.file.savePlayerData(PlayerData.create(player));
                }catch(Exception e){
                    Koru.log("Failed to save player data for " + player.name + "!");
                    e.printStackTrace();
                }
            }

            sendChatMessage("[GREEN]" + player.name + " [CORAL]has disconnected.");
            Koru.log(player.name + " has disconnected.");
            removeEntity(player);
            connections.remove(id, -1);
            netConnections.remove(id);
        }catch(Exception e){
            e.printStackTrace();
            Koru.log("Critical error: disconnect fail!");
        }
    }

    class Listen implements NetListener{
        KoruServer koru;

        public Listen(KoruServer n) {
            koru = n;
        }

        @Override
        public void disconnected(Connection con, DcReason reason){
            KoruServer.this.disconnected(con.getID());
        }

        @Override
        public void received(Connection con, Object object){

            if(object instanceof ConnectPacket){
                Koru.log("recieved a connect packet from " + con.getID());
                ConnectPacket packet = (ConnectPacket) object;
                if(!connections.containsKey(con.getID())){
                    netConnections.put(con.getID(), con);
                    connectPacketRecieved(packet, con);
                }
            }else if(connections.containsKey(con.getID())){
                KoruServer.this.connection = con;
                Net.onRecieve(object);
            }

        }
    }

    public void sendChatMessage(String message){
        ChatPacket packet = new ChatPacket();
        packet.message = message;
        send(packet, Mode.TCP);
    }

    @Override
    public void removeEntity(Entity entity){
        EntityRemovePacket remove = new EntityRemovePacket();
        remove.id = entity.getID();
        server.sendToAllTCP(remove);
        Entities.instance.removeEntity(entity);
    }

    @Override
    public void sendEntity(Entity entity){
        sendRange(entity, entity.pos.x, entity.pos.y, SyncSystem.syncrange, Mode.TCP);
    }

    @Override
    public void sendRange(Object object, float x, float y, float range, Mode mode){
        Entities.instance.getNearbyConnections(x, y, range, (entity) -> {
            sendTo(entity.connectionID, object, mode);
        });
    }

    @Override
    public void send(Object object, Mode mode){
        if(mode == Mode.TCP){
            server.sendToAllTCP(object);
        }else{
            server.sendToAllUDP(object);
        }
    }

    @Override
    public void sendExcept(int id, Object object, Mode mode){
        if(mode == Mode.TCP){
            server.sendToAllExceptTCP(id, object);
        }else{
            server.sendToAllExceptUDP(id, object);
        }
    }

    @Override
    public void sendTo(int id, Object object, Mode mode){
        Connection con = netConnections.get(id);
        if(con == null) return;
        if(mode == Mode.TCP){
            con.sendTCP(object);
        }else{
            con.sendUDP(object);
        }
    }

    public Player getPlayer(int connectionID){
        return (Player)Entities.instance.getEntity(connections.get(connectionID, -1));
    }

    public static void main(String[] args){
        System.out.println(ColorCodes.flush);
        System.out.flush();

        Seq<String> arglist = Seq.with(args);

        if(arglist.contains("-clearworld")){
            Koru.log("Clearing world.");
            Path world = Paths.get("world");
            if(Files.exists(world)){
                try(Stream<Path> stream = Files.walk(world)){
                    stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                        try{
                            Files.delete(path);
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    });
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

        KoruServer server = new KoruServer();
        server.setup();

        if(arglist.contains("-showmap")){
            server.createMapGraphics();
        }
    }
}
