package io.anuke.koru.ui;

import arc.scene.ui.*;
import io.anuke.koru.*;

public class PauseDialog extends Dialog{

    public PauseDialog(){
        super("Paused");

        cont.defaults().size(220, 45).pad(4);

        cont.button("Resume", () -> Koru.ui.resumeGame()).row();
        cont.button("Settings", () -> Koru.ui.showSettings()).row();
        cont.button("Controls", () -> Koru.ui.showControls()).row();
        cont.button("Back to Main Menu", () -> Koru.ui.leaveToTitle());

        closeOnBack();
        hidden(() -> Koru.ui.onPauseClosed());
    }
}
