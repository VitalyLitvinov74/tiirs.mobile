/*
 * Decompiled with CFR 0_118.
 */
package uhf.api;

public class Lock {
    public static final char MASK_AND_OPERATION_LEN = '\u0003';
    public int com_type;
    public String password;
    public int FMB;
    public int filterData_len;
    public int filterData_len_msb;
    public int filterData_len_lsb;
    public char[] filterData;
    public int lData_Mask;
    public int lData_Action;
    public char[] lData_a;
    public int ant_id;

    public Lock() {
    }

    public Lock(int com_type, String password, int FMB, int filterData_len, char[] filterData, int lData_Mask, int lData_Action, int ant_id) {
        this.com_type = com_type;
        this.password = password;
        this.FMB = FMB;
        this.filterData_len = filterData_len;
        this.filterData = filterData;
        this.lData_Mask = lData_Mask;
        this.lData_Action = lData_Action;
        this.ant_id = ant_id;
    }
}
