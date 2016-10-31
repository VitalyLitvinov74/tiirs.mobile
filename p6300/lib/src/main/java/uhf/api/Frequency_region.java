/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class Frequency_region {
    public static final char Region_China1 = '\u0001';
    public static final char Region_China2 = '\u0002';
    public static final char Region_Europe = '\u0003';
    public static final char Region_USA = '\u0004';
    public static final char Region_Korea = '\u0005';
    public static final char Region_Japan = '\u0006';
    public int com_type;
    public int save_setting;
    public int region;

    public Frequency_region() {
    }

    public Frequency_region(int com_type, int save_setting, int region) {
        this.com_type = com_type;
        this.save_setting = save_setting;
        this.region = region;
    }
}
