/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class Power {
    public static char LOOP_OPEN = '\u0000';
    public static char LOOP_CLOSE = '\u0001';
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
