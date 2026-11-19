package io.anuke.koru.ui.mobile;

import arc.*;
import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import io.anuke.koru.ui.*;

/** Mindustry-style native text input dialog used on Android, with a scene fallback on desktop. */
public class MobileTextDialog{

    public static void show(String title, String text, String message, int maxLength, Cons<String> accepted, Runnable canceled){
        if(Core.app != null && Core.app.isMobile()){
            Input.TextInput input = new Input.TextInput();
            input.title = title;
            input.text = text;
            input.message = message;
            input.maxLength = maxLength;
            input.accepted = accepted;
            input.canceled = canceled;
            Core.input.getTextInput(input);
        }else{
            showScene(title, text, message, maxLength, accepted, canceled);
        }
    }

    private static void showScene(String title, String text, String message, int maxLength, Cons<String> accepted, Runnable canceled){
        Dialog dialog = new Dialog(title);
        TextField field = new TextField(text);

        field.setMaxLength(maxLength);
        dialog.cont.margin(20);

        if(message != null){
            dialog.cont.add(message).width(520).wrap().padBottom(10).row();
        }

        dialog.cont.add(field).width(520).height(56);
        dialog.buttons.button("Send", () -> {
            accepted.get(field.getText());
            dialog.hide();
        }).size(160, 50);

        dialog.buttons.button("Cancel", () -> {
            if(canceled != null) canceled.run();
            dialog.hide();
        }).size(160, 50);

        dialog.closeOnBack(canceled);
        dialog.show();

        Core.app.post(() -> {
            if(dialog.getScene() != null){
                dialog.getScene().setKeyboardFocus(field);
                field.setCursorPosition(field.getText().length());
            }
        });
    }
}
