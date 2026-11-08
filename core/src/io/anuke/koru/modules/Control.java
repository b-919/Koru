package io.anuke.koru.modules;

import arc.Core;
import arc.input.InputProcessor;
import arc.input.KeyCode;
import io.anuke.koru.graphics.Graphics;
import arc.math.Angles;

import io.anuke.koru.Koru;
import io.anuke.koru.entities.Entities;
import io.anuke.koru.entities.TileCollision;
import io.anuke.koru.entities.types.Player;
import io.anuke.koru.graphics.lsystems.LSystems;
import io.anuke.koru.input.InputHandler;
import io.anuke.koru.input.InputType;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.items.ItemType;
import io.anuke.koru.network.Net;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.traits.DirectionTrait;
import io.anuke.koru.traits.DirectionTrait.Direction;
import io.anuke.koru.traits.InventoryTrait;
import io.anuke.koru.world.Tile;
import io.anuke.koru.world.materials.Material;
import io.anuke.koru.world.materials.Materials;
import io.anuke.koru.Koru;
import arc.util.Log;
import arc.Core;
import arc.input.*;
import io.anuke.koru.modules.KoruModule;
import arc.math.Angles;
import arc.math.Mathf;

public class Control extends KoruModule implements InputProcessor{
	public final Player player;
	public boolean debug = false, consoleOpen = false;

	private float movespeed = 1.6f, dashspeed = 18f;
	private int blockx, blocky;
	private GameState state = GameState.title;

	public Control(){
		player = new Player();
		player.name = "you";
		player.local = true;
	}

	@Override
	public void init() {
		Core.input.addProcessor(this);
	}

	public boolean canMove(){
		return state == GameState.playing;
	}

	public boolean isPlaying(){
		return state != GameState.title;
	}

	public boolean isState(GameState state){
		return this.state == state;
	}

	protected void setState(GameState state){
		this.state = state;
	}

	@Override
	public void update() {
		if (Core.input.keyTap(KeyCode.escape)) {
			Core.app.exit();
		}

		if(Core.input.keyTap(KeyCode.enter) && isPlaying()){
			Koru.ui.toggleChat();
		}

		if(Core.input.keyTap(KeyCode.backtick)){
			consoleOpen = !consoleOpen;
		}

		if(Core.input.keyTap(KeyCode.f3)){
			debug = !debug;
		}

		if (!canMove() || !Entities.instance.contains(player)) return;

		arc.math.geom.Vec2 vec = io.anuke.koru.graphics.Graphics.mouseWorld();

		int nx = World.tile(vec.x), ny = World.tile(vec.y);

		if(Core.input.isTouched() && (nx != blockx || ny != blocky)){
			sendInput(InputType.block_moved, nx, ny);
		}

		blockx = nx;
		blocky = ny;

		vector.set(0, 0);

		float speed = (Core.input.keyDown(KeyCode.shiftLeft) ? dashspeed : movespeed);

		if (Core.input.keyDown(KeyCode.w)) {
			vector.y += speed;
			player.direction = Direction.back;
		}

		if (Core.input.keyDown(KeyCode.s)) {
			vector.y -= speed;
			player.direction = Direction.front;
		}

		if (Core.input.keyDown(KeyCode.a)) {
			vector.x -= speed;
			player.direction = Direction.left;
		}

		if (Core.input.keyDown(KeyCode.d)) {
			vector.x += speed;
			player.direction = Direction.right;
		}

		ItemStack stack = player.inventory.hotbarStack();

		if(stack != null && stack.item.isType(io.anuke.koru.items.ItemType.tool)){
			float angle = Angles.mouseAngle(Core.input.mouseWorld().x - player.pos().x, Core.input.mouseWorld().y - player.pos().y);

			player.direction = Direction.forOrdinal((int)((angle-45f)/90f));

			if(angle > 360-45){
				player.direction = Direction.right;
			}
		}

		vector.limit(speed);
		TileCollision.move(player, vector.x*delta(), vector.y*delta());

		if(vector.len() > 0.05){
			player.walktime += delta();
		}else{
			player.walktime = 0;
		}

		vector.set(0, 0);
	}

	@Override
	public boolean keyDown (KeyCode keycode) {

		if(keycode == KeyCode.p){
			sendBlock();
		}

		if(keycode == KeyCode.t){
			BlockQueryPacket p = new BlockQueryPacket();
			p.x = blockx;
			p.y = blocky;
			Net.send(p);
			Tile tile = Koru.world.getTile(blockx, blocky);
			if(tile != null){
				int variant = tile.rand(LSystems.variants);
				Koru.log("Tile variant: " + (variant-1));
				Log.info("Code: " + LSystems.test[variant-1].getData().rules);
			}
		}

		if(keycode == KeyCode.r){
			LSystems.generate();
		}


		if(keycode.ordinal() >= KeyCode.num1.ordinal() && keycode.ordinal() <= KeyCode.num4.ordinal()){
			player.inventory.hotbar = keycode.ordinal() - KeyCode.num1.ordinal();

			SlotChangePacket packet = new SlotChangePacket();
			packet.slot = player.inventory.hotbar;
			Net.send(packet);

		}else if(keycode == KeyCode.q){
			sendInput(InputType.interact, blockx, blocky);
		}

		return false;
	}

	void sendInput(InputType type, Object... params) {
		InputPacket packet = new InputPacket();
		packet.data = params;
		packet.type = type;
		Net.send(packet);
	}

	void sendBlock(){
		Material[] types = {Materials.ltest, Materials.birch, Materials.deadbush, Materials.droopy};

		BlockInputPacket p = new BlockInputPacket();
		p.x = blockx;
		p.y = blocky;
		p.material = types[player.inventory.hotbar].id();
		Net.send(p);
	}

	boolean playerReachesBlock(){
		return Mathf.dst(World.world(cursorX()), World.world(cursorY()), player.pos.x, player.pos.y) < InputHandler.reach;
	}

	int cursorX(){
		return World.tile(Graphics.mouseWorld().x);
	}

	int cursorY(){
		return World.tile(Graphics.mouseWorld().y);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, KeyCode button){

		if (button == KeyCode.mouseLeft){

			sendInput(InputType.leftclick_down, blockx, blocky);

			if(player.inventory.hotbarStack() == null || !(player.inventory.hotbarStack().item.isType(io.anuke.koru.items.ItemType.tool) || player.inventory.hotbarStack().item.isType(io.anuke.koru.items.ItemType.tool)))
				sendInput(InputType.interact, blockx, blocky);

		}else if (button == KeyCode.mouseRight){
			player.inventory.recipe = -1;
			sendInput(InputType.rightclick_down);
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, KeyCode button) {
		if (button == KeyCode.mouseLeft) {
			sendInput(InputType.leftclick_up);
		} else if (button == KeyCode.mouseRight) {
			sendInput(InputType.rightclick_up);
		}
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		int amount = (int)amountY;
		int i = ((player.inventory.hotbar+amount) % 4);
		player.inventory.hotbar = i < 0 ? i + 4 : i;

		SlotChangePacket packet = new SlotChangePacket();
		packet.slot = player.inventory.hotbar;
		Net.send(packet);

		return false;
	}

	public enum GameState{
		title, playing, menu, chatting
	}
}
