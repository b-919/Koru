package io.anuke.koru.modules;

import arc.*;
import arc.func.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.graphics.*;
import io.anuke.koru.items.*;
import io.anuke.koru.modules.Control.*;
import io.anuke.koru.traits.*;
import io.anuke.koru.ui.*;

import static arc.Core.*;

public class UI extends SceneModule{
    private ObjectMap<Class<?>, Menu> menus = new ObjectMap<>();
    private Menu currentMenu;

    private ChatView chat;
    private TextDialog error;
    private ConnectDialog connect;
    private io.anuke.koru.ui.KeybindDialog keybinds;
    private SettingsDialog settings;
    private PauseDialog pause;

    private InventoryView inventory;
    private BlockView blockrecipes;
    private RecipeView recipes;

    private Boolp titlevis = () -> Koru.control.isState(GameState.title);
    private Boolp playvis = () -> Koru.control.isPlaying();

    public UI(){
        Koru.skin = StyleLoader.loadStyles();

        Core.input.addProcessor(scene);
    }

    @Override
    public void init(){
        error = new TextDialog("Error", "N/A");
        connect = new ConnectDialog();
        settings = new SettingsDialog();
        keybinds = new io.anuke.koru.ui.KeybindDialog();
        pause = new PauseDialog();
        chat = new ChatView();

        settings.checkPref("smoothcam", "Smooth Camera", true);

        setup();

        Time.run(6, () -> {
            connect.show();
        });
    }

    void setup(){
        //title menu
        Table titleTable = new Table(Skin.instance.get("window-noborder", Drawable.class));
        titleTable.setFillParent(true);
        titleTable.defaults().size(180, 45);

        titleTable.button("Connect", connect::show);
        titleTable.row();
        titleTable.button("Settings", settings::show);
        titleTable.row();
        titleTable.button("Controls", keybinds::show);
        titleTable.row();
        titleTable.button("Quit", () -> Core.app.exit());

        titleTable.visible(titlevis);
        scene.add(titleTable);

        //title text
        Table titleTextTable = new Table();
        titleTextTable.setFillParent(true);
        titleTextTable.top();

        Label titleShadow = new Label("Koru");
        titleShadow.setColor(arc.graphics.Color.gray);
        titleShadow.setFontScale(4f);
        titleTextTable.add(titleShadow).padBottom(-124);
        titleTextTable.row();

        Label titleLabel = new Label("Koru");
        titleLabel.setFontScale(4f);
        titleTextTable.add(titleLabel);

        titleTextTable.visible(titlevis);
        this.scene.add(titleTextTable);

        //inventory
        Table invTable = new Table();
        invTable.setFillParent(true);
        invTable.bottom().right();

        inventory = new InventoryView();
        invTable.add(inventory);

        invTable.visible(playvis);
        this.scene.add(invTable);

        //recipes
        Table recipeTable = new Table();
        recipeTable.setFillParent(true);
        recipeTable.left();

        recipeTable.visible(() -> {
            ItemStack stack = Koru.control.player == null ? null : Koru.control.player.inventory.hotbarStack();
            return playvis.get() && stack != null && stack.isType(ItemType.placer);
        });
        this.scene.add(recipeTable);

        //debug
        Table debugTable = new Table();
        debugTable.setFillParent(true);
        debugTable.top().right();
        debugTable.label(() -> Core.graphics.getFramesPerSecond() + " FPS");

        debugTable.visible(playvis);
        this.scene.add(debugTable);

        //crafting
        Table craftTable = new Table();
        craftTable.setFillParent(true);
        craftTable.top().left();

        blockrecipes = new BlockView();
        recipes = new RecipeView();

        Table craftSubTable = new Table(Skin.instance.get("button-window-bg", Drawable.class));
        craftSubTable.margin(4);

        ButtonGroup<Button> group = new ButtonGroup<>();
        Stack stack = new Stack();

        Button blocksBtn = craftSubTable.button("Blocks", () -> {
            stack.invalidateHierarchy();
        }).fillX().uniformX().get();
        group.add(blocksBtn);

        Button recipesBtn = craftSubTable.button("Recipes", () -> {
            stack.invalidateHierarchy();
        }).fillX().uniformX().get();
        group.add(recipesBtn);

        stack.add(blockrecipes);
        blockrecipes.visible(() -> group.getCheckedIndex() == 0);

        stack.add(recipes);
        recipes.visible(() -> group.getCheckedIndex() == 1);

        craftSubTable.row();
        craftSubTable.add(stack).colspan(group.getButtons().size);

        craftTable.add(craftSubTable);
        craftTable.visible(playvis);
        this.scene.add(craftTable);

        //chat
        chat = new ChatView();
        chat.visible(playvis);
        scene.add(chat);
    }

    public void toggleChat(){
        chat.toggle();

        if(chat.chatOpen()){
            Koru.control.setState(GameState.chatting);
        }else{
            Koru.control.setState(GameState.playing);
        }
    }

    public void hideConnect(){
        connect.hide();
    }

    public void openPauseMenu(){
        if(pause == null){
            pause = new PauseDialog();
        }

        pause.show();
        Koru.control.setState(GameState.menu);
    }

    public void resumeGame(){
        if(pause != null){
            pause.hide(null);
        }
        Koru.control.setState(GameState.playing);
    }

    public void onPauseClosed(){
        Koru.control.setState(GameState.playing);
    }

    public boolean subMenuOpen(){
        return (settings != null && settings.isShown()) || (keybinds != null && keybinds.isShown());
    }

    public void showSettings(){
        settings.show();
    }

    public void showControls(){
        keybinds.show();
    }

    public void onDisconnected(){
        if(pause != null){
            pause.hide(null);
        }
        settings.hide(null);
        keybinds.hide(null);

        if(Koru.control != null && Koru.control.player != null && Koru.control.player.inventory != null){
            Koru.control.player.inventory.recipe = -1;
        }
    }

    public void leaveToTitle(){
        onDisconnected();
        closeMenu();

        Koru.network.leaveServer();
        Koru.control.setState(GameState.title);
    }

    public void handleChatMessage(String message, String sender){
        chat.addMessage(message, sender);
    }

    public boolean menuOpen(){
        return scene.hasMouse() || currentMenu != null || chat.chatOpen() || (pause != null && pause.isShown());
    }

    public void showError(String... lines){
        error.set("Error", lines);
        error.show();
    }

    public void openMenu(Class<? extends Menu> c){
        Menu m = null;
        if(menus.containsKey(c)){
            m = menus.get(c);
        }else{
            try{
                m = c.newInstance();
            }catch(Exception e){
                throw new RuntimeException(e);
            }
            menus.put(c, m);
        }

        m.show(scene);
        currentMenu = m;
        m.onOpen();

        KoruCursors.setCursor("cursor");
    }

    public void closeMenu(){
        if(currentMenu != null){
            currentMenu.hide();
            currentMenu.onClose();
            currentMenu = null;
        }
    }
}


