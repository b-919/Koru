package io.anuke.koru.ui;

import arc.Core;
import arc.scene.ui.Dialog;

public class SettingsDialog extends Dialog{
	public SettingsDialog(){
		super("Settings");
		buttons.button("Back", this::hide).size(100, 50);
	}

	public void checkPref(String name, String title, boolean def){
		//TODO
	}
}


