package io.anuke.koru.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.net.NetSerializer;
import io.anuke.koru.utils.Bits;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.anuke.koru.entities.Entity;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.input.InputType;
import io.anuke.koru.items.Item;
import io.anuke.koru.items.ItemData;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.items.impl.Items;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.network.syncing.SyncData;
import io.anuke.koru.traits.DirectionTrait.Direction;
import io.anuke.koru.ui.Menu;
import io.anuke.koru.world.Chunk;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;

public class Registrator{
	public static void register(Kryo k){
		register(k,
			ConnectPacket.class,
			PositionPacket.class,
			DataPacket.class,
			WorldUpdatePacket.class,
			EntityRemovePacket.class,
			ChunkRequestPacket.class,
			ChunkPacket.class,
			InputPacket.class,
			TileUpdatePacket.class,
			BlockInputPacket.class,
			StoreItemPacket.class,
			ChatPacket.class,
			BitmapDataPacket.class,
			BitmapDataPacket.Header.class,
			MaterialRequestPacket.class,
			InventoryUpdatePacket.class,
			InventoryClickPacket.class,
			SlotChangePacket.class,
			RecipeSelectPacket.class,
			EntityRequestPacket.class,
			BlockQueryPacket.class,
			BatchTileUpdatePacket.class,

			SyncData.class,

			Direction.class,

			Chunk.class,
			Tile.class,
			Tile[].class,
			Tile[][].class,
			Tile[][][].class,
			ItemStack[].class,
			ItemStack[][].class,
			long[].class,
			Items.class,
			ItemData.class,

			InputType.class,
			SyncType.class,
			Object[].class,
			Bits.class,
			Color.class,
			Vec2.class,
			ArrayList.class,
			ObjectMap.class,
			ConcurrentHashMap.class,
			ObjectMap.Keys.class,
			HashMap.class,
			byte[].class,
			int[].class,
			Class.class
		);

		EntitySerializer serializer = new EntitySerializer();
		k.register(Entity.class, serializer);
		k.register(ItemDrop.class, serializer);
		k.register(Particle.class, serializer);
		k.register(Projectile.class, serializer);
		k.register(TestEntity.class, serializer);
		k.register(DamageIndicator.class, serializer);
		k.register(BlockAnimation.class, serializer);
		k.register(Player.class, serializer);
		k.register(Effect.class, serializer);

		k.register(MenuOpenPacket.class, new MenuPacketSerializer());

		k.register(Material.class, new MaterialsSerializer());
		k.register(ItemStack.class, new ItemStackSerializer());
		k.register(Tile.class, new TileSerializer());
	}

	private static void register(Kryo k, Class<?>... classes){
		for(Class<?> c : classes){
			k.register(c);
		}
	}

	public static class ItemStackSerializer extends Serializer<ItemStack>{
		@Override
		public ItemStack read(Kryo k, Input i, Class<ItemStack> c){
			ItemStack stack = new ItemStack();
			stack.amount = k.readObject(i, Integer.class);
			stack.item = Item.getItem(k.readObject(i, Integer.class));
			return stack;
		}

		@Override
		public void write(Kryo k, Output o, ItemStack t){
			k.writeObject(o, t.amount);
			k.writeObject(o, t.item.id());
		}
	}

	public static class TileSerializer extends Serializer<Tile>{
		@Override
		public Tile read(Kryo k, Input i, Class<Tile> c){
			Tile tile = Tile.unloadedTile();

			tile.floors = new short[]{k.readObject(i, short.class)};
			tile.wallid = k.readObject(i, short.class);
			tile.light = k.readObject(i, byte.class);
			tile.top = k.readObject(i, byte.class);

			return tile;
		}

		@Override
		public void write(Kryo k, Output o, Tile t){
			k.writeObject(o, t.tileid());
			k.writeObject(o, t.wallid);
			k.writeObject(o, t.light);
			k.writeObject(o, t.top);
		}
	}

	public static class MenuPacketSerializer extends Serializer<MenuOpenPacket>{

		@Override
		public void write(Kryo kryo, Output output, MenuOpenPacket object){
			kryo.writeObject(output, object.type.getName());
		}

		@Override
		public MenuOpenPacket read(Kryo kryo, Input input, Class<MenuOpenPacket> type){
			MenuOpenPacket packet = new MenuOpenPacket();
			try{
				packet.type = (Class<? extends Menu>) Class.forName(kryo.readObject(input, String.class));
				return packet;
			}catch (Exception e){
				throw new RuntimeException(e);
			}
		}
	}

	public static class MaterialsSerializer extends Serializer<Material>{
		@Override
		public Material read(Kryo k, Input i, Class<Material> c){
			return (Material)Material.getMaterial(k.readObject(i, int.class));
		}

		@Override
		public void write(Kryo k, Output o, Material m){
			k.writeObject(o, m.id());
		}
	}

	/**
	 * Format:
	 * 1) entity ID
	 * 2) pos.x
	 * 3) pos.y
	 */
	static class EntitySerializer extends Serializer<Entity>{

		@Override
		public Entity read(Kryo k, Input input, Class<Entity> c){
			int id = k.readObject(input, int.class);
			float x = k.readObject(input, float.class);
			float y = k.readObject(input, float.class);

			try{
				Entity entity = c.newInstance();
				entity.id = id;
				entity.pos.set(x, y);
				return entity;
			}catch(Exception e){
				throw new RuntimeException("Failed to create entity of type " + c.getName(), e);
			}
		}

		@Override
		public void write(Kryo k, Output output, Entity entity){
			k.writeObject(output, entity.getID());
			k.writeObject(output, entity.pos.x);
			k.writeObject(output, entity.pos.y);
		}
	}
}


