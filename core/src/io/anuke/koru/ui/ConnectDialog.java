package io.anuke.koru.ui;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import io.anuke.koru.*;
import io.anuke.koru.modules.*;

import static io.anuke.koru.Koru.*;

public class ConnectDialog extends Dialog{
    TextDialog fail;

    public ConnectDialog(){
        super("Connect");
        setup();
        closeOnBack();
    }

    private void setup(){
        cont.margin(16);
        addCloseButton();

        String defaultName = System.getProperty("user.name");
        if(defaultName == null || defaultName.isEmpty()) defaultName = "player";

        String name = Core.settings.getString("koru-name", defaultName);
        String ip = Core.settings.getString("koru-ip", "localhost");
        String port = String.valueOf(Core.settings.getInt("koru-port", Network.port));

        Koru.control.player.name = name;
        Koru.network.ip = ip;
        Koru.network.connectPort = Strings.parseInt(port, Network.port);

        cont.add(new Label("Name:"));
        cont.field(name, s -> {
            Koru.control.player.name = s;
            Core.settings.put("koru-name", s);
        }).width(360).height(50);

        cont.row();

        cont.add(new Label("Server IP:"));
        cont.field(ip, s -> {
            Koru.network.ip = s;
            Core.settings.put("koru-ip", s);
        }).width(360).height(50);

        cont.row();

        cont.add(new Label("Port:"));
        cont.field(port, TextField.TextFieldFilter.digitsOnly, s -> {
            int value = Strings.parseInt(s, Network.port);
            Koru.network.connectPort = value;
            Core.settings.put("koru-port", value);
        }).width(160).height(50);

        cont.row();

        cont.add("Enter your server's LAN or public IP. The default port is 7575.")
                .colspan(2).width(420).wrap().padTop(8).row();

        cont.button("Connect", () -> {
            Core.settings.put("koru-name", Koru.control.player.name);
            Core.settings.put("koru-ip", Koru.network.ip);
            Core.settings.put("koru-port", Koru.network.connectPort);
            Core.settings.forceSave();
            Koru.network.connect();
        }).colspan(2).padTop(10).size(300, 50);
    }

    @Override
    public void addCloseButton(){
        Label titleLabel = title;
        Table titleTable = this.titleTable;

        ImageButton closeButton = new ImageButton(skin.get("close-window", ImageButtonStyle.class));

        titleTable.add(closeButton).padRight(-titleTable.getMarginRight() + 3.7f).size(40).padTop(-titleTable.getMarginTop()-5);
        closeButton.clicked(this::hide);

        if(titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2){
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
        }
    }
}
