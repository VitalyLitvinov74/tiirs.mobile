/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class EPC {
    public int pc_msb;
    public int pc_lsb;
    public int epc_len;
    public char[] epc;

    public EPC() {
    }

    public EPC(int pc_msb, int pc_lsb, int epc_len, char[] epc) {
        this.pc_msb = pc_msb;
        this.pc_lsb = pc_lsb;
        this.epc_len = epc_len;
        this.epc = epc;
    }
}
