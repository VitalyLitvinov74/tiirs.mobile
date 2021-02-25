/*
 * Decompiled with CFR 0_118.
 */
package ru.toir.mobile.multi.p6300.lib.src.main.java.android.hardware.p6300.uhf.api;

public class Power {
    public static char LOOP_OPEN = 0x00;
    public static char LOOP_CLOSE = 0x01;
    public int com_type;
    public int loop;
    public int read;
    public int write;

    public Power() {
    }

    public Power(int com_type, int loop, int read, int write) {
        this.com_type = com_type;
        this.loop = loop;
        this.read = read;
        this.write = write;
    }
}
