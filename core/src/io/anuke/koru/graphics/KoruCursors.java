package io.anuke.koru.graphics;

import arc.*;
import arc.Graphics.*;
import arc.graphics.*;
import arc.struct.*;
import io.anuke.koru.utils.*;
import io.anuke.koru.utils.Pixmaps;

public class KoruCursors{
    private static ObjectMap<String, Cursor> cursors = new ObjectMap<>();
    private static String cursor;

    public static void setCursor(String name){
        cursor = name;
    }

    public static Cursor loadCursor(String name){
        Cursor cursor = cursors.get(name);

        if(cursor == null){
            Pixmap pixmap = new Pixmap(Core.files.internal("cursors/"+name+".png"));
            Pixmap out = Pixmaps.outline(pixmap, new Color(0,0,0,0.1f));
            Pixmap out2 = Pixmaps.scale(out, 4);

            out.dispose();
            pixmap.dispose();

            cursor = Core.graphics.newCursor(out2, out2.width/2, out2.height/2);
            cursors.put(name, cursor);
        }

        return cursor;
    }

    public static void updateCursor(){
        if(cursor != null){
            Core.graphics.cursor(loadCursor(cursor));
        }
    }
}
