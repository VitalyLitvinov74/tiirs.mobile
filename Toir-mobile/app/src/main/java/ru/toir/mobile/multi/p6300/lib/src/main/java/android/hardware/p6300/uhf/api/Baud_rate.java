/*
 * Decompiled with CFR 0_118.
 */
package ru.toir.mobile.multi.p6300.lib.src.main.java.android.hardware.p6300.uhf.api;

public class Baud_rate {
    public static final int BAUD_RATE_9600 = 0;
    public static final int BAUD_RATE_19200 = 1;
    public static final int BAUD_RATE_38400 = 2;
    public static final int BAUD_RATE_57600 = 3;
    public static final int BAUD_RATE_115200 = 4;
    public int com_type;
    public int rate_type;

    public Baud_rate() {
    }

    public Baud_rate(int com_type, int rate_type) {
        this.com_type = com_type;
        this.rate_type = rate_type;
    }
}
