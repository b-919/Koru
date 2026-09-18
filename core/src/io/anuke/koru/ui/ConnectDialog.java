package io.anuke.koru.ui;

import io.anuke.koru.Koru;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.TextField;

public class ConnectDialog extends Dialog{
	TextDialog fail;
	
	public ConnectDialog(){
		super("Connect");
		setup();
	}
	
	private void setup(){
		cont.margin(16);
		addCloseButton();
		
		cont.add(new Label("Name:"));
		
		cont.field(System.getProperty("user.name"), s -> {
			Koru.control.player.name = s;
		});
		
		cont.row();
		
		cont.add(new Label("IP:"));
		
		cont.field("localhost", s -> {
			Koru.network.ip = s;
		});
		
		cont.row();
		
		cont.button("Connect", () -> {
			Koru.network.connect();
		}).colspan(2).padTop(10).size(300, 50);
	}
}
