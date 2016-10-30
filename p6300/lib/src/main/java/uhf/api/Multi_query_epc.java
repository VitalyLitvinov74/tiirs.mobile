/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  uhf.api.Query_epc
 */
package uhf.api;

import uhf.api.Query_epc;

public class Multi_query_epc {
    public int com_type;
    public int query_total;
    public int query_total_msb;
    public int query_total_lsb;
    public int packet_num;
    public Query_epc[] tags_epc;

    public Multi_query_epc() {
    }

    public Multi_query_epc(int com_type, int query_total, int packet_num, Query_epc[] tags_epc) {
        this.com_type = com_type;
        this.query_total = query_total;
        this.packet_num = packet_num;
        this.tags_epc = tags_epc;
    }
}
