/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class Kill {
    public int com_type;
    public String password;
    public int FMB;
    public int filterData_len;
    public int filterData_len_msb;
    public int filterData_len_lsb;
    public char[] filterData;
    public int ant_id;

    public Kill() {
    }

    public Kill(int com_type, String password, int FMB, int filterData_len, char[] filterData, int ant_id) {
        this.com_type = com_type;
        this.password = password;
        this.FMB = FMB;
        this.filterData_len = filterData_len;
        this.filterData = filterData;
        this.ant_id = ant_id;
    }
}
