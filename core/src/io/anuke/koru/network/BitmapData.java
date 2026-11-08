package io.anuke.koru.network;

import arc.graphics.*;
import arc.struct.*;
import java.nio.*;

public class BitmapData{
    final public byte[] data;
    final public int width, height;
    final public int[] colors;
    transient int byteIndex = 0;

    public BitmapData(Pixmap pixmap){
        data = new  byte[pixmap.getWidth()*pixmap.getHeight()];
        IntSet colorset = new IntSet();
        this.width = pixmap.getWidth();
        this.height = pixmap.getHeight();

        for(int x = 0; x < pixmap.getWidth(); x ++){
            for(int y = 0; y < pixmap.getHeight(); y ++){
                int color = getPixel(pixmap, x, y);
                colorset.add(color);
            }
        }

        colors = new int[colorset.size];

        byte index = 0;
        for(int i : colorset.iterator().toArray().items){
            colors[index ++] = i;
        }

        ObjectMap<Integer, Byte> rmap = new ObjectMap<Integer, Byte>();
        for(byte i = 0; i < colors.length; i ++){
            rmap.put(colors[i], i);
        }

        for(int x = 0; x < pixmap.getWidth(); x ++){
            for(int y = 0; y < pixmap.getHeight(); y ++){
                Byte b = rmap.get(getPixel(pixmap, x, y));
                data[x + y*pixmap.getWidth()] = (b == null ? 5 : b);
            }
        }
    }

    public BitmapData(int width, int height, int[] colors){
        this.width = width;
        this.height = height;
        this.colors = colors;
        this.data = new byte[width*height];
    }

    public void pushBytes(byte[] bytes){
        System.arraycopy(bytes, 0, data, byteIndex, bytes.length);
        byteIndex += bytes.length;
    }

    public boolean isDone(){
        return byteIndex >= data.length;
    }

    public Pixmap toPixmap(){
        Pixmap pixmap = new Pixmap(width, height);
        for(int x = 0; x < pixmap.getWidth(); x ++){
            for(int y = 0; y < pixmap.getHeight(); y ++){
                int color = colors[data[x + y*width]];
                setPixel(pixmap, x, y, color);
            }
        }
        return pixmap;
    }

    static int getPixel(Pixmap pixmap, int x, int y){
        ByteBuffer buf = pixmap.getPixels();
        int i = (y * pixmap.getWidth() + x) * 4;
        return (buf.get(i) & 0xff) << 24 | (buf.get(i + 1) & 0xff) << 16 | (buf.get(i + 2) & 0xff) << 8 | (buf.get(i + 3) & 0xff);
    }

    static void setPixel(Pixmap pixmap, int x, int y, int color){
        ByteBuffer buf = pixmap.getPixels();
        int i = (y * pixmap.getWidth() + x) * 4;
        buf.put(i, (byte)(color >>> 24));
        buf.put(i + 1, (byte)(color >>> 16));
        buf.put(i + 2, (byte)(color >>> 8));
        buf.put(i + 3, (byte)color);
    }
}


