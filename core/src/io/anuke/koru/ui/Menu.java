package io.anuke.koru.ui;

import static io.anuke.koru.Koru.skin;

import arc.graphics.Color;
import arc.util.Align;

import io.anuke.koru.Koru;
import arc.scene.ui.Dialog;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;

public abstract class Menu extends Dialog{

	public Menu(String title) {
		super(title);
		titleTable.marginTop(-15);
		titleTable.marginLeft(3);
		// titleLabel is a field in Dialog
		this.title.setColor(arc.graphics.Color.yellow);
		cont.marginBottom(cont.getMarginBottom()-2);
		setMovable(false);
	}
	
	public void addCloseButton () {
		// Dialog has title and titleTable fields
		Label titleLabel = this.title;
		Table titleTable = this.titleTable;

		ImageButton closeButton = new ImageButton(skin.get("close-window", ImageButtonStyle.class));
		
		titleTable.add(closeButton).padRight(-titleTable.getMarginRight() + 3.7f).size(40).padTop(-titleTable.getMarginTop()-5);
		
		closeButton.clicked(() -> Koru.ui.closeMenu());

		if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
			titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
	}
	
	public abstract void onOpen();
	public abstract void onClose();
}
