/*
 * Decompiled with CFR 0_118.
 */
package ru.toir.mobile.multi.p6300.lib.src.main.java.android.hardware.p6300.uhf.api;

public class Tags_epc {
    public int com_type;
    public int[] epc;
    public int epc_len;

    public Tags_epc() {
    }

    public Tags_epc(int com_type, int[] epc, int epc_len) {
        this.com_type = com_type;
        this.epc = epc;
        this.epc_len = epc_len;
    }
}
