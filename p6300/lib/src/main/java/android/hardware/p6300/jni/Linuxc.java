/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package android.hardware.p6300.jni;

import android.util.Log;

public class Linuxc {

    public static final int BAUD_RATE_1200 = 0;
    public static final int BAUD_RATE_2400 = 1;
    public static final int BAUD_RATE_4800 = 2;
    public static final int BAUD_RATE_9600 = 3;
    public static final int BAUD_RATE_19200 = 4;
    public static final int BAUD_RATE_38400 = 5;
    public static final int BAUD_RATE_57600 = 6;
    public static final int BAUD_RATE_115200 = 7;
    public static final int BAUD_RATE_230400 = 8;
    public static final int BAUD_RATE_921600 = 9;

    static {
        try {
            System.loadLibrary("p6300uart");
            Log.i("JIN", "Trying to load libp6300uart.so");
        }
        catch (UnsatisfiedLinkError ule) {
            Log.e("JIN", "WARNING:could not load libp6300uart.so");
        }
    }

    public static native int openUart(String path);
    public static native void closeUart(int descriptor);
    public static native int setUart(int descriptor, int baudRate, int timeOut, int minLen);
    public static native int sendMsgUartHex(int descriptor, String command, int commandSize);
    public static native String receiveMsgUartHex(int descriptor);
}
