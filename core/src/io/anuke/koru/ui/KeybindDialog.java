package io.anuke.koru.ui;

import arc.scene.ui.*;

public class KeybindDialog extends Dialog{

    public KeybindDialog(){
        super("Controls");
        buttons.button("Back", this::hide).size(100, 50);
        closeOnBack();
    }
}


