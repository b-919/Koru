package io.anuke.koru.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import arc.Files;
import arc.backend.sdl.SdlApplication;
import arc.backend.sdl.SdlConfig;

import io.anuke.koru.Koru;

public class DesktopLauncher {

	public static void main (String[] arg){
		SdlConfig config = new SdlConfig();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		config.maximized = true;
		config.decorated = true;
		config.width = d.width;
		config.height = d.height-100;
		config.setWindowIcon(Files.FileType.internal, "sprites/logo.png");
		config.title = "Koru";

		new SdlApplication(new Koru(), config);
	}
}
