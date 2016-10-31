/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class Temperature {
    public int com_type;
    public int temp_msb;
    public int temp_lsb;

    public Temperature() {
    }

    public Temperature(int com_type, int temp_msb, int temp_lsb) {
        this.com_type = com_type;
        this.temp_msb = temp_msb;
        this.temp_lsb = temp_lsb;
    }
}
