/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package jni;

import android.util.Log;

public class Linuxc {
    public static int BAUD_RATE_1200;
    public static int BAUD_RATE_2400;
    public static int BAUD_RATE_4800;
    public static int BAUD_RATE_9600;
    public static int BAUD_RATE_19200;
    public static int BAUD_RATE_38400;
    public static int BAUD_RATE_57600;
    public static int BAUD_RATE_115200;
    public static int BAUD_RATE_230400;
    public static int BAUD_RATE_921600;

    static {
        try {
            System.loadLibrary("uart");
            Log.i((String)"JIN", (String)"Trying to load libuart.so");
        }
        catch (UnsatisfiedLinkError ule) {
            Log.e((String)"JIN", (String)"WARNING:could not load libuart.so");
        }
        BAUD_RATE_1200 = 0;
        BAUD_RATE_2400 = 1;
        BAUD_RATE_4800 = 2;
        BAUD_RATE_9600 = 3;
        BAUD_RATE_19200 = 4;
        BAUD_RATE_38400 = 5;
        BAUD_RATE_57600 = 6;
        BAUD_RATE_115200 = 7;
        BAUD_RATE_230400 = 8;
        BAUD_RATE_921600 = 9;
    }

    public static native int openUart(String var0);

    public static native void closeUart(int var0);

    public static native int setUart(int var0, int var1, int var2, int var3);

    public static native int sendMsgUartHex(int var0, String var1, int var2);

    public static native String receiveMsgUartHex(int var0);

    public static native int sendMsgUartByte(int var0, byte[] var1, int var2);
}
