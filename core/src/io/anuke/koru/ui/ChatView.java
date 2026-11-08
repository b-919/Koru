package io.anuke.koru.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Label.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import io.anuke.koru.network.*;
import io.anuke.koru.network.packets.*;

public class ChatView extends Table{
    private final static int messagesShown = 10;
    private Seq<ChatMessage> messages = new Seq<ChatMessage>();
    private float fadetime;
    private float lastfadetime;
    private boolean chatOpen = false;
    private TextField chatfield;
    private Label fieldlabel = new Label(">");
    private Font font;
    private GlyphLayout layout = new GlyphLayout();
    private float offsetx = 4, offsety = 4, fontoffsetx = 2, chatspace = 50;
    private float textWidth = 600;
    private Color shadowColor = new Color(0,0,0,0.4f);
    private float textspacing = 10;


    public ChatView(){
        super();

        setFillParent(true);
        font = Skin.instance.get("pixel-font", Font.class);

        setup();
    }

    private void setup(){
        fieldlabel.setStyle(new LabelStyle(fieldlabel.getStyle()));
        fieldlabel.getStyle().font = font;
        fieldlabel.setStyle(fieldlabel.getStyle());
        fieldlabel.setFontScale(2);
        chatfield = new TextField("", Skin.instance.get(null, TextField.TextFieldStyle.class));

        chatfield.getStyle().background = Skin.instance.get("blank", arc.scene.style.Drawable.class);
        chatfield.getStyle().fontColor = Color.white;
        chatfield.getStyle().font = Skin.instance.get("pixel-font-nomarkup", Font.class);

        bottom().left();
        add(fieldlabel).padBottom(4f).padLeft(offsetx*2).padBottom(offsety);

        add(chatfield).padBottom(offsety).padLeft(offsetx).growX().padRight(offsetx).height(28);
    }

    @Override
    public void draw(){
        float alpha = parentAlpha;

        Draw.color(shadowColor);
        if(chatOpen){
            Skin.instance.get("white", Drawable.class).draw(offsetx, chatfield.y, Core.graphics.getWidth()-offsetx*2, chatfield.getHeight()-1);

        }
        font.getData().setScale(2f);
        font.getData().down = -21.5f;
        font.getData().lineHeight = 22f;

        super.draw();


        float spacing = chatspace;

        chatfield.visible = chatOpen;
        fieldlabel.visible = chatOpen;

        Draw.color(shadowColor);

        float theight = offsety + spacing;
        for(int i = 0; i < messagesShown && i < messages.size && i < fadetime; i ++){

            layout.setText(font, messages.get(i).formattedMessage, Color.white, textWidth, Align.bottomLeft, true);
            theight += layout.height+textspacing;
            if(i == 0)theight -= textspacing+1;

            font.getCache().clear();
            font.getCache().addText(messages.get(i).formattedMessage, fontoffsetx + offsetx, offsety + theight, textWidth, Align.bottomLeft, true);

            if(fadetime-i < 1f && fadetime-i >= 0f){
                font.getCache().setAlphas(fadetime-i);
                Draw.color(0,0,0,shadowColor.a*(fadetime-i));

            }

            Skin.instance.get("white", Drawable.class).draw(offsetx, theight-layout.height+1-4, textWidth, layout.height+textspacing);
            Draw.color(shadowColor);

            font.getCache().draw();
        }

        Draw.color(1f, 1f, 1f, 1f);

        if(fadetime > 0 && !chatOpen){
            fadetime -= Core.graphics.getDeltaTime()*60f/120f;
        }
    }

    private void sendMessage(){
        String message = chatfield.getText();
        chatfield.clearText();

        if(message.replaceAll(" ", "").isEmpty()) return;

        ChatPacket packet = new ChatPacket();
        packet.message = message;
        Net.send(packet);
    }

    public void toggle(){
        Scene scene = getScene();

        if(!chatOpen && (scene.getKeyboardFocus() == null || !scene.getKeyboardFocus().parent.visible)){
            scene.setKeyboardFocus(chatfield);
            chatOpen = !chatOpen;
            lastfadetime = fadetime;
            fadetime = messagesShown+1;
        }else if(chatOpen){
            scene.setKeyboardFocus(null);
            chatOpen = !chatOpen;
            sendMessage();
            fadetime = lastfadetime;
        }
    }

    public boolean chatOpen(){
        return chatOpen;
    }

    public void addMessage(String message, String sender){
        messages.insert(0, new ChatMessage(message, sender));

        fadetime += 1f;
        fadetime = Math.min(fadetime, messagesShown)+2f;
    }

    private static class ChatMessage{
        public final String sender;
        public final String message;
        public final String formattedMessage;

        public ChatMessage(String message, String sender){
            this.message = message;
            this.sender = sender;
            if(sender == null){ //no sender, this is a server message
                formattedMessage = message;
            }else{
                formattedMessage = "[ROYAL]["+sender+"]: [YELLOW]"+message;
            }
        }
    }
}


