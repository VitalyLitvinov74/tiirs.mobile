/*
 * Decompiled with CFR 0_118.
 */
package android.hardware.p6300.uhf.api;

public class Fastid {
    public static final int FASTID_ON = 1;
    public static final int FASTID_OFF = 0;
    public int com_type;
    public int fastid_switch;

    public Fastid() {
    }

    public Fastid(int com_type, int fastid_switch) {
        this.com_type = com_type;
        this.fastid_switch = fastid_switch;
    }
}
