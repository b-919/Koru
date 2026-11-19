package io.anuke.koru.modules;

import arc.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.graphics.lsystems.*;
import io.anuke.koru.input.*;
import io.anuke.koru.items.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.packets.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.traits.DirectionTrait.*;
import io.anuke.koru.ui.mobile.*;
import io.anuke.koru.world.*;
import io.anuke.koru.world.materials.*;

public class Control extends KoruModule implements InputProcessor{
    public final Player player;
    public boolean debug = false, consoleOpen = false;

    private float movespeed = 1.6f, dashspeed = 18f;
    private int blockx, blocky;
    private GameState state = GameState.title;

    private final Vec2 cursor = new Vec2(), mobileAimOffset = new Vec2();
    private boolean mobileAimSet = false, mobileUseHeld = false, mobileDragging = false;

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
        boolean back = Core.input.keyTap(KeyCode.back);

        if(Core.input.keyTap(KeyCode.escape) || back){
            if(state == GameState.playing){
                Koru.ui.openPauseMenu();
            }else if(state == GameState.chatting){
                Koru.ui.toggleChat();
            }else if(state == GameState.menu && !Koru.ui.subMenuOpen()){
                Koru.ui.resumeGame();
            }else if(back && state == GameState.title){
                Core.app.exit();
            }
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

        if(!canMove() || !Entities.instance.contains(player)) return;

        if(Core.app.isMobile()){
            updateMobileCursor();
        }else{
            cursor.set(io.anuke.koru.graphics.Graphics.mouseWorld());

            int nx = World.tile(cursor.x), ny = World.tile(cursor.y);

            if(Core.input.isTouched() && (nx != blockx || ny != blocky)){
                sendInput(InputType.blockMoved, nx, ny);
            }

            blockx = nx;
            blocky = ny;
        }

        vector.set(0, 0);

        float speed = (Core.input.keyDown(KeyCode.shiftLeft) ? dashspeed : movespeed);

        if(Core.app.isMobile()){
            MobileControls controls = Koru.ui.mobile();
            if(controls != null){
                vector.set(controls.joystick.vector);
            }

            if(vector.len() > 0.1f){
                player.direction = directionForAngle(Angles.angle(vector.x, vector.y));
            }

            vector.scl(speed);
        }else{
            if(Core.input.keyDown(KeyCode.w)) {
                vector.y += speed;
                player.direction = Direction.back;
            }

            if(Core.input.keyDown(KeyCode.s)) {
                vector.y -= speed;
                player.direction = Direction.front;
            }

            if(Core.input.keyDown(KeyCode.a)) {
                vector.x -= speed;
                player.direction = Direction.left;
            }

            if(Core.input.keyDown(KeyCode.d)) {
                vector.x += speed;
                player.direction = Direction.right;
            }
        }

        ItemStack stack = player.inventory.hotbarStack();

        if(stack != null && stack.item.isType(io.anuke.koru.items.ItemType.tool) && (!Core.app.isMobile() || mobileAimSet)){
            float angle = Angles.angle(cursor.x - player.pos().x, cursor.y - player.pos().y);

            player.direction = directionForAngle(angle);
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

    public static Direction directionForAngle(float angle){
        angle = Mathf.mod(angle, 360f);

        if(angle < 45f || angle >= 315f) return Direction.right;
        if(angle < 135f) return Direction.back;
        if(angle < 225f) return Direction.left;
        return Direction.front;
    }

    private void updateMobileCursor(){
        if(!mobileAimSet){
            setDefaultMobileAim();
        }

        cursor.set(player.pos.x + mobileAimOffset.x, player.pos.y + mobileAimOffset.y);

        int nx = World.tile(cursor.x), ny = World.tile(cursor.y);

        if(nx != blockx || ny != blocky){
            blockx = nx;
            blocky = ny;

            if(mobileUseHeld){
                sendInput(InputType.blockMoved, blockx, blocky);
            }
        }
    }

    private void setDefaultMobileAim(){
        float distance = 24f;

        if(player.direction == Direction.right){
            mobileAimOffset.set(distance, 0);
        }else if(player.direction == Direction.left){
            mobileAimOffset.set(-distance, 0);
        }else if(player.direction == Direction.back){
            mobileAimOffset.set(0, distance);
        }else{
            mobileAimOffset.set(0, -distance);
        }
    }

    public void mobileAim(int screenX, int screenY){
        Vec2 world = Core.camera.unproject(screenX, screenY);

        mobileAimSet = true;
        mobileAimOffset.set(world.x - player.pos.x, world.y - player.pos.y);
        cursor.set(world);
        blockx = World.tile(cursor.x);
        blocky = World.tile(cursor.y);
    }

    public void mobileUseDown(){
        if(!canMove()) return;

        updateMobileCursor();
        mobileUseHeld = true;

        sendInput(InputType.leftclickDown, blockx, blocky);
    }

    public void mobileUseUp(){
        if(mobileUseHeld){
            mobileUseHeld = false;
            sendInput(InputType.leftclickUp);
        }
    }

    public void resetMobileInput(){
        mobileDragging = false;
    }

    public void selectHotbar(int slot){
        player.inventory.hotbar = Mathf.clamp(slot, 0, 3);
        SlotChangePacket packet = new SlotChangePacket();
        packet.slot = player.inventory.hotbar;
        Net.send(packet);
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
            selectHotbar(keycode.ordinal() - KeyCode.num1.ordinal());
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

    public int cursorX(){
        return blockx;
    }

    public int cursorY(){
        return blocky;
    }

    public float cursorWorldX(){
        return cursor.x;
    }

    public float cursorWorldY(){
        return cursor.y;
    }

    public boolean isMobileActing(){
        return mobileDragging || mobileUseHeld;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, KeyCode button){

        if(button == KeyCode.mouseLeft){
            if(Core.app.isMobile()){
                mobileAim(screenX, screenY);
                mobileDragging = true;
            }else{
                cursor.set(Core.camera.unproject(screenX, screenY));
                blockx = World.tile(cursor.x);
                blocky = World.tile(cursor.y);
            }

            sendInput(InputType.leftclickDown, blockx, blocky);

            if(Core.app.isMobile() || player.inventory.hotbarStack() == null || !player.inventory.hotbarStack().item.isType(io.anuke.koru.items.ItemType.tool)){
                sendInput(InputType.interact, blockx, blocky);
            }
        }else if(button == KeyCode.mouseRight){
            player.inventory.recipe = -1;
            sendInput(InputType.rightclickDown);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, KeyCode button) {
        mobileDragging = false;

        if(button == KeyCode.mouseLeft) {
            sendInput(InputType.leftclickUp);
        } else if(button == KeyCode.mouseRight) {
            sendInput(InputType.rightclickUp);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        if(!Core.app.isMobile() || !mobileDragging || !canMove()) return false;

        int lastx = blockx, lasty = blocky;
        mobileAim(screenX, screenY);

        if(blockx != lastx || blocky != lasty){
            sendInput(InputType.blockMoved, blockx, blocky);
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
