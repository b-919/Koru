package io.anuke.koru.modules;

import static io.anuke.koru.Koru.*;

import arc.Core;
import arc.struct.Seq;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.IntSet.IntSetIterator;
import arc.net.Client;
import arc.net.Connection;
import arc.net.NetListener;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.Entities;
import io.anuke.koru.entities.Entity;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.modules.Control.GameState;
import io.anuke.koru.network.Net;
import io.anuke.koru.network.Net.Mode;
import io.anuke.koru.network.Net.NetProvider;
import io.anuke.koru.network.Registrator;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.modules.KoruModule;
import arc.math.Angles;
import arc.util.Timekeeper;
import arc.net.DcReason;
import io.anuke.koru.network.KryoSerialization;
import io.anuke.koru.utils.Bits;

public class Network extends KoruModule implements NetProvider{
	public static final int port = 7575;
	public static final int ping = 0;
	public static final int pingInterval = 60;
	public static final int packetFrequency = 3;
	public static final float sparkUnloadRange = 600;

	private static final Timekeeper pingTimer = Timekeeper.ofTicks(pingInterval);
	private static final Timekeeper updateTimer = Timekeeper.ofTicks(packetFrequency);

	public String ip = "localhost";
	public boolean connecting;
	private Client client;

	private Player player;

	private boolean connected;
	private boolean chunksAdded = false;
	private IntSeq tempids = new IntSeq();
	private Seq<Entity> sparkQueue = new Seq<>();
	private IntSet sparksToRemove = new IntSet();
	private IntSet requestedEntities = new IntSet();

	@Override
	public void init(){
		registerPackets();

		Net.setProvider(false, this);

		int buffer = (int) Math.pow(2, 6) * 8192;
		KryoSerialization serialization = new KryoSerialization();
		client = new Client(buffer, buffer, serialization);
		Registrator.register(serialization.getKryo());
		client.addListener(new Listen());
		client.start();

		player = Koru.control.player;
	}

	private void registerPackets(){

		Net.handle(DataPacket.class, data -> {

			Core.app.post(() -> {
				Koru.log("Recieving a data packet... ");

				Entities.instance.clear();
				Koru.log("Recieved " + data.entities.size() + " entities.");

				for(Entity entity : data.entities){
					Entities.instance.addEntity(entity);
				}

				Koru.world.time = data.time;
				Koru.control.player.setID(data.playerid);

				Entities.instance.addEntity(Koru.control.player);
				Koru.log("Recieved data packet.");
			});
		});

		Net.handle(WorldUpdatePacket.class, p -> {
			for(Integer key : p.updates.keys()){
				Entity entity = Entities.instance.getEntity(key);
				if(entity == null){
					requestEntity(key);
					continue;
				}
				entity.syncType.read(p.updates.get(key), entity);
			}
		});

		Net.handle(ChunkPacket.class, p -> {
			world.loadChunks(p);
			chunksAdded = true;
		});

		Net.handle(TileUpdatePacket.class, p -> {
			if(world.inClientBounds(p.x, p.y)){
				world.setTile(p.x, p.y, p.tile);
			}

			chunksAdded = true;
		});

		Net.handle(MenuOpenPacket.class, p -> {
			Core.app.post(() -> {
				ui.openMenu(p.type);
			});
		});

		Net.handle(SlotChangePacket.class, p -> {
			Entity entity = Entities.instance.getEntity(p.id);
			if(entity == null || entity.inventory == null) return;

			entity.inventory.inventory[0] = p.stack;
		});

		Net.handle(InventoryUpdatePacket.class, p -> {
			player.inventory.set(p.stacks, p.selected);
		});

		Net.handle(ChatPacket.class, p -> {
			Core.app.post(() -> {
				ui.handleChatMessage(p.message, p.sender);
			});
		});

		Net.handle(BatchTileUpdatePacket.class, packet->{
			for(int i = 0; i < packet.coords.length; i ++){
				int x = (int)(packet.coords[i] >> 32);
				int y = (int)(packet.coords[i]);
				short floor = (short) Bits.getLeftShort(packet.ids[i]);
				short wall = (short) Bits.getRightShort(packet.ids[i]);

				Tile tile = Koru.world.getTile(x, y);
				tile.setWall(Material.getMaterial(wall));
				tile.addFloor(Material.getMaterial(floor));

				Koru.renderer.invalidateTiles();;
			}
		});

		handleEntity(Entity.class);
		handleEntity(ItemDrop.class);
		handleEntity(Particle.class);
		handleEntity(Projectile.class);
		handleEntity(TestEntity.class);
		handleEntity(DamageIndicator.class);
		handleEntity(BlockAnimation.class);
		handleEntity(Player.class);
		handleEntity(Effect.class);

		Net.handle(EntityRemovePacket.class, p -> {
			sparksToRemove.add(p.id);
		});

	}

	private void handleEntity(Class<? extends Entity> type){
		Net.handle(type, entity -> sparkQueue.add(entity));
	}

	public void connect(){
		try{
			connecting = true;
			client.connect(1200, ip, port, port);
			Koru.log("Connecting to server..");
		}catch(Exception e){
			connecting = false;
			connected = false;
			e.printStackTrace();
			Koru.ui.showError("[crimson]Failed to connect to server:", (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
			Koru.log("Connection failed!");
		}

		connecting = false;
	}

	void requestEntity(int id){
		if(!requestedEntities.contains(id) && !sparksToRemove.contains(id)){
			requestedEntities.add(id);

			EntityRequestPacket request = new EntityRequestPacket();
			request.id = id;
			client.sendTCP(request);
		}
	}

	private void disconnect(){
		connected = false;
		connecting = false;
		Koru.ui.showError("Connection error: Timed out.");

		for(int x = 0; x < world.chunks.length; x++){
			for(int y = 0; y < world.chunks[x].length; y++){
				world.chunks[x][y] = null;
			}
		}

		Entities.instance.clear();
	}

	@Override
	public void update(){
		while(sparkQueue.size != 0){

			Entity entity = sparkQueue.pop();
			if(entity == null){
				continue;
			}

			requestedEntities.remove(entity.getID());

			if(sparksToRemove.contains(entity.getID())){
				continue;
			}

			if(Entities.instance.getEntity(entity.getID()) == null){
				Entities.instance.addEntity(entity);
			}
		}

		if(connected && pingTimer.poll()){
			client.updateReturnTripTime();
		}

		//unloads entities that are very far away
		for(Entity e : Entities.all()){
			if(e.unload() && e.pos.dst2(player.pos.x, player.pos.y) > sparkUnloadRange * sparkUnloadRange){
				Entities.instance.removeEntity(e);
			}
		}

		tempids.clear();

		int id = -1;
		for(IntSetIterator rit = sparksToRemove.iterator(); rit.hasNext;){
			id = rit.next();
			if(Entities.instance.removeEntity(id)){
				tempids.add(id);
			}
		}

		for(int i = 0; i < tempids.size; i++){
			sparksToRemove.remove(tempids.get(i));
		}

		if(chunksAdded){
			renderer.updateTiles();
			chunksAdded = false;
		}

		if(connected && updateTimer.poll()){
			sendUpdate();
		}
	}

	private void sendUpdate(){
		if(player.input == null)
			return;

		PositionPacket pos = new PositionPacket();
		pos.x = player.pos.x;
		pos.y = player.pos.y;
		pos.mouseangle = Angles.mouseAngle(Core.input.mouseWorld().x - player.pos.x, Core.input.mouseWorld().y - player.pos.y);
		player.input.mouseangle = pos.mouseangle;
		pos.direction = player.direction;

		client.sendUDP(pos);
	}

	@Override
	public void send(Object object, Mode mode){
		client.sendTCP(object);
	}

	@Override
	public void sendRange(Object object, float x, float y, float range, Mode mode){
		throw new RuntimeException("Not supported on client!");
	}

	@Override
	public void sendTo(int clientid, Object object, Mode mode){
		throw new RuntimeException("Not supported on client!");
	}

	@Override
	public void sendExcept(int clientid, Object object, Mode mode){
		throw new RuntimeException("Not supported on client!");
	}

	@Override
	public void sendEntity(Entity entity){
		throw new RuntimeException("Not supported on client!");
	}

	@Override
	public void removeEntity(Entity entity){
		Entities.instance.removeEntity(entity);
	}

	class Listen implements NetListener{
		@Override
		public void connected(Connection connection){
			ConnectPacket packet = new ConnectPacket();
			packet.name = player.name;
			client.sendTCP(packet);
			Koru.log("Sent packet.");

			connected = true;
			Koru.ui.hideConnect();
			Koru.control.setState(GameState.playing);
		}

		@Override
		public void received(Connection connection, Object object){
			//network callbacks fire on arc-net's thread; run handlers on the main thread
			Core.app.post(() -> {
				try{
					Net.onRecieve(object);
				}catch(Exception e){
					e.printStackTrace();
					Koru.log("Packet recieve error!");
				}
			});
		}

		@Override
		public void disconnected(Connection connection, DcReason reason){
			Core.app.post(() -> {
				disconnect();
				Koru.ui.showError("Disconnected from server: " + reason);
				Koru.control.setState(GameState.title);
			});
		}
	}
}
