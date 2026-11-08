package io.anuke.koru.ui;

import arc.Core;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Dialog;

public class SettingsDialog extends Dialog{
	public SettingsDialog(){
		super("Settings");
		buttons.button("Back", this::hide).size(100, 50);
	}

	public void checkPref(String name, String title, boolean def){
		CheckBox box = new CheckBox(title);
		box.setChecked(Core.settings.getBool(name, def));
		box.changed(() -> Core.settings.put(name, box.isChecked()));
		add(box).left().pad(4).row();
	}
}
