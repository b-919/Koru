package io.anuke.koru.ui;

import arc.*;
import arc.files.*;
import arc.freetype.*;
import arc.freetype.FreeTypeFontGenerator.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.CheckBox.*;
import arc.scene.ui.Dialog.*;
import arc.scene.ui.ImageButton.*;
import arc.scene.ui.Label.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.TextField.*;

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

        font.getData().markupEnabled = true;
        largefont.getData().markupEnabled = true;
        borderfont.getData().markupEnabled = true;

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

        Font uiFont = new Font(Core.files.internal("fonts/font2.fnt"));
        uiFont.getData().markupEnabled = false;
        styles.add("default-font", uiFont);

        Font markupFont = new Font(Core.files.internal("fonts/font2.fnt"));
        markupFont.getData().markupEnabled = true;
        styles.add("markup-font", markupFont);

        Skin.instance = styles;
        registerDefaults(styles);

        return styles;
    }

    private static void registerDefaults(Skin skin){
        if(Core.scene == null) return;

        Font font = skin.getFont("default-font");
        Color grey = new Color(0.32f, 0.32f, 0.32f, 1f);
        Color orange = Color.valueOf("ffa500");

        LabelStyle label = new LabelStyle(font, Color.white);

        TextButtonStyle button = new TextButtonStyle();
        button.up = skin.get("button", Drawable.class);
        button.down = skin.get("button-down", Drawable.class);
        button.over = skin.get("button-over", Drawable.class);
        button.font = font;
        button.fontColor = Color.white;
        button.disabledFontColor = grey;

        DialogStyle dialog = new DialogStyle();
        dialog.background = skin.get("window", Drawable.class);
        dialog.titleFont = font;
        dialog.titleFontColor = orange;

        TextFieldStyle field = new TextFieldStyle();
        field.font = font;
        field.fontColor = Color.white;
        field.disabledFontColor = grey;
        field.background = skin.get("textfield", Drawable.class);
        field.focusedBackground = skin.get("textfield-over", Drawable.class);
        field.cursor = skin.get("cursor", Drawable.class);
        field.selection = skin.get("selection", Drawable.class);
        field.messageFont = font;
        field.messageFontColor = grey;

        ImageButtonStyle imageButton = new ImageButtonStyle();
        imageButton.up = skin.get("button", Drawable.class);
        imageButton.down = skin.get("button-down", Drawable.class);
        imageButton.over = skin.get("button-over", Drawable.class);
        imageButton.imageUp = skin.get("icon-close", Drawable.class);
        imageButton.imageOver = skin.get("icon-close-over", Drawable.class);
        imageButton.imageDown = skin.get("icon-close-down", Drawable.class);

        CheckBoxStyle checkBox = new CheckBoxStyle();
        checkBox.font = font;
        checkBox.fontColor = Color.white;
        checkBox.disabledFontColor = grey;
        checkBox.checkboxOn = skin.get("check-on", Drawable.class);
        checkBox.checkboxOff = skin.get("check-off", Drawable.class);
        checkBox.checkboxOver = skin.get("vis-check-over", Drawable.class);

        Core.scene.addStyle(LabelStyle.class, label);
        Core.scene.addStyle(ButtonStyle.class, button);
        Core.scene.addStyle(TextButtonStyle.class, button);
        Core.scene.addStyle(DialogStyle.class, dialog);
        Core.scene.addStyle(TextFieldStyle.class, field);
        Core.scene.addStyle(ImageButtonStyle.class, imageButton);
        Core.scene.addStyle(CheckBoxStyle.class, checkBox);

        skin.add("close-window", imageButton);
    }
}


