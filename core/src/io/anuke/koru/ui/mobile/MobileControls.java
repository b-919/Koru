package io.anuke.koru.ui.mobile;

import arc.input.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import io.anuke.koru.*;
import io.anuke.koru.items.*;
import io.anuke.koru.modules.Control.*;

public class MobileControls extends WidgetGroup{
    private static final float joystickSize = 230f, panelWidth = 200f;
    private static final float inventoryFallbackWidth = 196f, inventoryFallbackTop = 248f;

    public final Joystick joystick;
    private final Table actions = new Table();
    private final TextButton useButton;

    private boolean using;
    private int usePointer = -1;

    public MobileControls(){
        setFillParent(true);
        touchable = Touchable.childrenOnly;

        joystick = new Joystick();
        addChild(joystick);
        addChild(actions);

        actions.defaults().size(92f, 44f).pad(2f);
        actions.bottom().left();

        useButton = new TextButton("USE");
        useButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                if(using) return false;

                using = true;
                usePointer = pointer;
                Koru.control.mobileUseDown();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                if(using && pointer == usePointer){
                    using = false;
                    usePointer = -1;
                    Koru.control.mobileUseUp();
                }
            }
        });

        actions.add(actionButton("CHAT", Koru.ui::openChatInput));
        actions.add(actionButton("BUILD", Koru.ui::toggleCraft));
        actions.row();

        actions.add(useButton).colspan(2).size(panelWidth - 8f, 76f);

        useButton.visible(() -> {
            ItemStack stack = Koru.control.player.inventory.hotbarStack();
            return stack != null && stack.item.isType(ItemType.tool);
        });

        visible(() -> Koru.control.isState(GameState.playing));

        update(this::refresh);
    }

    @Override
    public void layout(){
        joystick.setBounds(14f, 14f, joystickSize, joystickSize);

        float invWidth = Koru.ui.inventoryWidth();
        if(invWidth <= 0f) invWidth = inventoryFallbackWidth;

        float invLeft = Koru.ui.inventoryLeft();
        if(invLeft <= 0f) invLeft = getWidth() - invWidth - 8f;

        float invTop = Koru.ui.inventoryTop();
        if(invTop <= 0f) invTop = inventoryFallbackTop;

        actions.setBounds(invLeft, invTop + 8f, panelWidth, 128f);
    }

    private TextButton actionButton(String text, Runnable clicked){
        TextButton button = new TextButton(text);
        button.clicked(clicked);
        return button;
    }

    private void refresh(){
        if(!Koru.control.canMove()){
            reset();
        }
    }

    public void reset(){
        joystick.reset();
        Koru.control.resetMobileInput();

        if(using){
            using = false;
            usePointer = -1;
            Koru.control.mobileUseUp();
        }
    }
}
