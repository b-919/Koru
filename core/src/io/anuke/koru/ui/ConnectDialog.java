package io.anuke.koru.ui;

import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import io.anuke.koru.*;

import static io.anuke.koru.Koru.*;

public class ConnectDialog extends Dialog{
    TextDialog fail;

    public ConnectDialog(){
        super("Connect");
        setup();
    }

    private void setup(){
        cont.margin(16);
        addCloseButton();

        Koru.control.player.name = System.getProperty("user.name");

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
