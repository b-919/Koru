package io.anuke.koru.ui;

import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.Label.*;
import arc.scene.ui.layout.*;
import arc.util.*;

import static io.anuke.koru.Koru.*;

public class TextDialog extends Dialog{
    private Label label;

    public TextDialog(String title, String text){
        super(title);
        addCloseButton();
        label = cont.add(text).width(400).wrap().pad(20).get();
        label.setStyle(new LabelStyle(Skin.instance.getFont("markup-font"), Color.white));
        buttons.button("OK", this::hide).size(100, 50);
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

    public void set(String title, String... lines){
        this.title.setText(title);
        label.setText(String.join("\n", lines));
    }
}


