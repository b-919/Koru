package io.anuke.koru.utils;

public class Bits{
    public static int packShort(short left, short right){
        return (left << 16) | (right & 0xFFFF);
    }

    public static short getLeftShort(int packed){
        return (short) (packed >> 16);
    }

    public static short getRightShort(int packed){
        return (short) (packed & 0xFFFF);
    }

    public static long packInt(int left, int right){
        return ((long) left << 32) | (right & 0xFFFFFFFFL);
    }

    public static int getLeftInt(long packed){
        return (int) (packed >> 32);
    }

    public static int getRightInt(long packed){
        return (int) (packed & 0xFFFFFFFFL);
    }
}


