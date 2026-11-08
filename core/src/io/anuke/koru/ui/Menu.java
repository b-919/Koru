package io.anuke.koru.ui;

import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import io.anuke.koru.*;

import static io.anuke.koru.Koru.*;

public abstract class Menu extends Dialog{

    public Menu(String title) {
        super(title);
        titleTable.marginTop(-15);
        titleTable.marginLeft(3);
        this.title.setColor(Color.yellow);
        cont.marginBottom(cont.getMarginBottom()-2);
        setMovable(false);
    }

    public void addCloseButton () {
        Label titleLabel = this.title;
        Table titleTable = this.titleTable;

        ImageButton closeButton = new ImageButton(skin.get("close-window", ImageButtonStyle.class));

        titleTable.add(closeButton).padRight(-titleTable.getMarginRight() + 3.7f).size(40).padTop(-titleTable.getMarginTop()-5);

        closeButton.clicked(() -> Koru.ui.closeMenu());

        if(titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2){
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
        }
    }

    public abstract void onOpen();
    public abstract void onClose();
}
