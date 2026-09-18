package io.anuke.koru.ui;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureAtlas;
import io.anuke.koru.ui.Skin;
import arc.freetype.FreeTypeFontGenerator;
import arc.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import arc.scene.ui.Dialog.DialogStyle;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.Label.LabelStyle;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.TextField.TextFieldStyle;
import arc.scene.style.Drawable;

public class StyleLoader{
	
	public static Skin loadStyles(){
		float s = 1f;
		Fi skinFile = Core.files.internal("ui/uiskin.json");
		Skin styles = new Skin();

		Fi atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
		if(atlasFile.exists()){
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			styles.addRegions(atlas);
		}
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Core.files.internal("fonts/smooth.ttf"));
		
		FreeTypeFontParameter normalparameter = new FreeTypeFontParameter();
		normalparameter.size = (int)(22 * s);

		FreeTypeFontParameter largeparameter = new FreeTypeFontParameter();
		largeparameter.size = (int)(26 * s);

		FreeTypeFontParameter borderparameter = new FreeTypeFontParameter();
		borderparameter.size = (int)(26 * s);
		borderparameter.borderWidth = 2*s;
		borderparameter.borderColor = new Color(0,0,0,1f);
		borderparameter.spaceX = -2;

		Font font = generator.generateFont(normalparameter);
		Font largefont = generator.generateFont(largeparameter);
		Font borderfont = generator.generateFont(borderparameter);

		styles.add("default-font", font);
		styles.add("smooth-font", font);
		styles.add("large-font", largefont);
		styles.add("border-font", borderfont);
		
		FreeTypeFontGenerator pgenerator = new FreeTypeFontGenerator(Core.files.internal("fonts/prose.ttf"));
		
		FreeTypeFontParameter pixelparameter = new FreeTypeFontParameter();
		pixelparameter.borderStraight = true;
		pixelparameter.size = 16;
		pixelparameter.borderWidth = 1;
		pixelparameter.borderColor = new Color(0,0,0,1f);
		pixelparameter.spaceX = -1;
		
		FreeTypeFontParameter pixelparameter2 = new FreeTypeFontParameter();
		pixelparameter2.borderStraight = true;
		pixelparameter2.size = 16;
		
		Font pixelfont = pgenerator.generateFont(pixelparameter);
		pixelfont.getData().setScale(2f);
		pixelfont.getData().markupEnabled = true;
		styles.add("pixel-font", pixelfont);
		
		Font pixelfontm = pgenerator.generateFont(pixelparameter);
		pixelfontm.getData().setScale(2f);
		pixelfontm.getData().markupEnabled = false;
		styles.add("pixel-font-nomarkup", pixelfontm);
		
		Font pixelfont2 = pgenerator.generateFont(pixelparameter2);
		pixelfont2.getData().setScale(2f);
		pixelfont2.setUseIntegerPositions(false);
		styles.add("pixel-font-noborder", pixelfont2);
		
		Font pixelfontsm = new Font(Core.files.internal("fonts/pixel-small.fnt"));
		styles.add("pixel-font-small", pixelfontsm);

		styles.load(skinFile);
		Skin.instance = styles;
		registerDefaults(styles);

		return styles;
	}

	private static void registerDefaults(Skin skin){
		if(Core.scene == null) return;

		Font font = skin.getFont("default-font");
		Font border = skin.getFont("border-font");
		Font pixel = skin.getFont("pixel-font");

		LabelStyle label = new LabelStyle(font, Color.white);

		TextButtonStyle button = new TextButtonStyle();
		button.up = skin.get("button", Drawable.class);
		button.down = skin.get("button-down", Drawable.class);
		button.over = skin.get("button-over", Drawable.class);
		button.font = font;
		button.fontColor = Color.white;
		button.disabledFontColor = Color.gray;

		DialogStyle dialog = new DialogStyle();
		dialog.background = skin.get("window", Drawable.class);
		dialog.titleFont = border;
		dialog.titleFontColor = Color.white;

		TextFieldStyle field = new TextFieldStyle();
		field.font = pixel;
		field.fontColor = Color.white;
		field.background = skin.get("textfield", Drawable.class);
		field.focusedBackground = skin.get("textfield-over", Drawable.class);
		field.cursor = skin.get("cursor", Drawable.class);
		field.selection = skin.get("selection", Drawable.class);
		field.messageFont = font;
		field.messageFontColor = Color.gray;

		ImageButtonStyle imageButton = new ImageButtonStyle();
		imageButton.up = skin.get("button", Drawable.class);
		imageButton.down = skin.get("button-down", Drawable.class);
		imageButton.over = skin.get("button-over", Drawable.class);
		imageButton.imageUp = skin.get("icon-close", Drawable.class);

		Core.scene.addStyle(LabelStyle.class, label);
		Core.scene.addStyle(ButtonStyle.class, button);
		Core.scene.addStyle(TextButtonStyle.class, button);
		Core.scene.addStyle(DialogStyle.class, dialog);
		Core.scene.addStyle(TextFieldStyle.class, field);
		Core.scene.addStyle(ImageButtonStyle.class, imageButton);

		skin.add("close-window", imageButton);
	}
}


