/*
 * Decompiled with CFR 0_118.
 */
package android.hardware.p6300.uhf.api;

public class Gen2 {
    public static char StaticQ = '\u0000';
    public static char DynamicQ = '\u0001';
    public int com_type;
    public int Q;
    public int startQ;
    public int MinQ;
    public int MaxQ;

    public Gen2() {
    }

    public Gen2(int com_type, int Q, int startQ, int MinQ, int MaxQ) {
        this.com_type = com_type;
        this.Q = Q;
        this.startQ = startQ;
        this.MinQ = MinQ;
        this.MaxQ = MaxQ;
    }
}
