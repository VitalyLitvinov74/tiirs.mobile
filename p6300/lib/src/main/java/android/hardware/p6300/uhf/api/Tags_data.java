/*
 * Decompiled with CFR 0_118.
 */
package android.hardware.p6300.uhf.api;

public class Tags_data {
    public int com_type;
    public String password;
    public int FMB;
    public int filterData_len;
    public int filterData_len_msb;
    public int filterData_len_lsb;
    public char[] filterData;
    public int mem_bank;
    public int start_addr;
    public int start_addr_msb;
    public int start_addr_lsb;
    public int data_len;
    public int data_len_msb;
    public int data_len_lsb;
    public int ant_id;
    public char[] data;

    public Tags_data() {
    }

    public Tags_data(int com_type, String password, int FMB, int filterData_len, char[] filterData, int mem_bank, int start_addr, int data_len, int ant_id, char[] data) {
        this.com_type = com_type;
        this.password = password;
        this.FMB = FMB;
        this.filterData_len = filterData_len;
        this.filterData = filterData;
        this.mem_bank = mem_bank;
        this.start_addr = start_addr;
        this.data_len = data_len;
        this.ant_id = ant_id;
        this.data = data;
    }
}
