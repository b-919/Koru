package io.anuke.koru.ui;

import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;

public class TextDialog extends Dialog{
	private Label label;

	public TextDialog(String title, String text){
		super(title);
		label = cont.add(text).maxWidth(400).wrap().pad(20).get();
		buttons.button("OK", this::hide).size(100, 50);
	}

	public void set(String title, String... lines){
		this.title.setText(title);
		label.setText(String.join("\n", lines));
	}
}


