/*
 * Decompiled with CFR 0_118.
 */
package ru.toir.mobile.multi.p6300.lib.src.main.java.android.hardware.p6300.uhf.api;

public class Multi_interval {
    public int com_type;
    public int work_time;
    public int work_time_msb;
    public int work_time_lsb;
    public int interval;
    public int interval_msb;
    public int interval_lsb;

    public Multi_interval() {
    }

    public Multi_interval(int com_type, int work_time, int work_time_msb, int work_time_lsb,
                          int interval, int interval_msb, int interval_lsb) {
        this.com_type = com_type;
        this.work_time = work_time;
        this.work_time_msb = work_time_msb;
        this.work_time_lsb = work_time_lsb;
        this.interval = interval;
        this.interval_msb = interval_msb;
        this.interval_lsb = interval_lsb;
    }
}
