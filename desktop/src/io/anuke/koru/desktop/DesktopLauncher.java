package io.anuke.koru.desktop;

import arc.*;
import arc.backend.sdl.*;
import io.anuke.koru.*;

public class DesktopLauncher{
    public static void main(String[] args){
        SdlConfig config = new SdlConfig();
        config.maximized = true;
        config.decorated = true;
        config.width = 1280;
        config.height = 720;
        config.setWindowIcon(Files.FileType.internal, "sprites/logo.png");
        config.title = "Koru";

        new SdlApplication(new Koru(), config);
    }
}
