/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

import uhf.api.EPC;

public class Query_epc {
    public int com_type;
    public EPC epc;
    public int rssi_msb;
    public int rssi_lsb;
    public int ant_id;
    public int tid_len;
    public int[] tid;

    public Query_epc() {
    }

    public Query_epc(int com_type, EPC epc, int rssi_msb, int rssi_lsb, int ant_id, int tid_len, int[] tid) {
        this.com_type = com_type;
        this.epc = epc;
        this.rssi_msb = rssi_msb;
        this.rssi_lsb = rssi_lsb;
        this.ant_id = ant_id;
        this.tid_len = tid_len;
        this.tid = tid;
    }
}
