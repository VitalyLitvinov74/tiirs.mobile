/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.AsyncTask
 *  android.util.Log
 *  jni.Linuxc
 *  uhf.api.Tags_data
 *  uhf.api.Temperature
 *  uhf.api.UHF
 *  uhf.api.Ware
 */
package uhf.api;

import android.os.AsyncTask;
import android.util.Log;
import java.util.Calendar;
import jni.Linuxc;

public class ReceiveThread
extends AsyncTask<Integer, String, Integer> {
    char[] data = new char[2048];
    char[] data1 = new char[2048];
    int datalen = 0;
    int mark_head;
    Calendar c = null;
    String time;
    public boolean isFlag = false;
    int tag_len = 0;
    long seconds = 0;
    long seconds1 = 0;
    public int com_fd = -1;
    public UHF mUHF;
    public MultiLableCallBack mc;

    public ReceiveThread(UHF mUHF) {
        this.mUHF = mUHF;
        this.com_fd = this.mUHF.com_fd;
        this.isFlag = true;
    }

    public void setCallfuc(MultiLableCallBack mc) {
        this.mc = mc;
    }

    protected Integer doInBackground(Integer ... params) {
        int len = 0;
        char[] d = new char[1024];
        String str_receive = null;
        Log.v("onProgressUpdate", "ReceiveThread ++++++ ");
        block16 : while (this.isFlag) {
            str_receive = Linuxc.receiveMsgUartHex(this.com_fd);
            if (str_receive == null || (len = str_receive.length()) == 0) continue;
            d = str_receive.toCharArray();
            System.arraycopy(d, 0, this.data, this.datalen, len);
            this.datalen += len;
            block17 : while (this.datalen >= 6) {
                this.mark_head = this.findCharInReceiveData(0, '\u00bb');
                if (this.mark_head < 0) {
                    this.datalen = 0;
                    continue block16;
                }
                if (this.mark_head + 2 > this.datalen || this.mark_head + 6 + this.data[this.mark_head + 2] > this.datalen) continue block16;
                if (this.data[this.mark_head + 3 + this.data[this.mark_head + 2] + 1] != '\r' || this.data[this.mark_head + 3 + this.data[this.mark_head + 2] + 2] != '\n') {
                    this.datalen = this.datalen - this.mark_head - this.data[this.mark_head + 2] - 5;
                    System.arraycopy(this.data, this.mark_head + this.data[this.mark_head + 2] + 5, this.data, 0, this.datalen);
                    continue block16;
                }
                char checksum = this.data[this.mark_head + 3 + this.data[this.mark_head + 2]];
                Log.e("2", ("checksum ==" + Integer.toHexString(checksum)));
                this.CheckSum(this.mark_head + 1, this.data, this.data[this.mark_head + 2] + 2);
                if (checksum != this.data[this.mark_head + 3 + this.data[this.mark_head + 2]]) {
                    Log.e("2", "check error");
                    Log.e("2", ("checksum ==" + Integer.toHexString(this.data[this.mark_head + 3 + this.data[this.mark_head + 2]])));
                    this.datalen = this.datalen - this.mark_head - this.data[this.mark_head + 2] - 5;
                    System.arraycopy(this.data, this.mark_head + this.data[this.mark_head + 2] + 5, this.data, 0, this.datalen);
                    continue block16;
                }
                Log.e("Data", "OK");
                if (this.mc != null && this.data[this.mark_head + 1] == '\u0097') {
                    char[] copydata = new char[this.data[this.mark_head + 2]];
                    System.arraycopy(this.data, this.mark_head + 3, copydata, 0, copydata.length);
                    this.mc.method(copydata);
                    this.datalen = this.datalen - this.mark_head - this.data[this.mark_head + 2] - 5;
                    System.arraycopy(this.data, this.mark_head + this.data[this.mark_head + 2] + 5, this.data, 0, this.datalen);
                }
                switch (CommandType.LastCommand) {
                    case '\u0000': 
                    case '\u0002': 
                    case '\u0007': 
                    case '\t': 
                    case '\u001a': 
                    case '\u001d': 
                    case '!': 
                    case '#': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u0018': {
                        CommandType.CommandOK = true;
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\n': 
                    case '\u000b': {
                        CommandType.CommandOK = true;
                        ((Ware)this.mUHF.mObject).major_version = this.data[this.mark_head + 3];
                        ((Ware)this.mUHF.mObject).minor_version = this.data[this.mark_head + 4];
                        ((Ware)this.mUHF.mObject).revision_version = this.data[this.mark_head + 5];
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\f': {
                        CommandType.CommandOK = true;
                        ((Power)this.mUHF.mObject).loop = this.data[this.mark_head + 3];
                        ((Power)this.mUHF.mObject).read = this.data[this.mark_head + 4];
                        ((Power)this.mUHF.mObject).write = this.data[this.mark_head + 5];
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\r': {
                        CommandType.CommandOK = true;
                        ((Output_frequency)this.mUHF.mObject).frequency_num = this.data[this.mark_head + 3];
                        ((Output_frequency)this.mUHF.mObject).frequency = new float[((Output_frequency)this.mUHF.mObject).frequency_num];
                        int iLen = ((Output_frequency)this.mUHF.mObject).frequency_num;
                        int iFreq = 0;
                        int index = 0;
                        while (index < iLen) {
                            iFreq = (this.data[this.mark_head + 4 + 3 * index] << 16) + (this.data[this.mark_head + 4 + 3 * index + 1] << 8) + this.data[this.mark_head + 4 + 3 * index + 2];
                            ((Output_frequency)this.mUHF.mObject).frequency[index] = (float)((double)iFreq / 1000.0);
                            ++index;
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u0011': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Frequency_region)this.mUHF.mObject).region = this.data[this.mark_head + 4];
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u0012': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Temperature)this.mUHF.mObject).temp_msb = this.data[this.mark_head + 4];
                            ((Temperature)this.mUHF.mObject).temp_lsb = this.data[this.mark_head + 5];
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u0014': {
                        CommandType.CommandOK = true;
                        ((Gen2)this.mUHF.mObject).Q = this.data[this.mark_head + 3] & '\u0001';
                        ((Gen2)this.mUHF.mObject).startQ = this.data[this.mark_head + 4] >> 4 & 15;
                        ((Gen2)this.mUHF.mObject).MinQ = this.data[this.mark_head + 4] & 15;
                        ((Gen2)this.mUHF.mObject).MaxQ = this.data[this.mark_head + 5] >> 4 & 15;
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u001e': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Multi_interval)this.mUHF.mObject).work_time_msb = this.data[this.mark_head + 4];
                            ((Multi_interval)this.mUHF.mObject).work_time_lsb = this.data[this.mark_head + 5];
                            ((Multi_interval)this.mUHF.mObject).interval_msb = this.data[this.mark_head + 6];
                            ((Multi_interval)this.mUHF.mObject).interval_lsb = this.data[this.mark_head + 7];
                            ((Multi_interval)this.mUHF.mObject).work_time = ((Multi_interval)this.mUHF.mObject).work_time_msb << 8 | ((Multi_interval)this.mUHF.mObject).work_time_lsb;
                            ((Multi_interval)this.mUHF.mObject).interval = ((Multi_interval)this.mUHF.mObject).interval_msb << 8 | ((Multi_interval)this.mUHF.mObject).interval_lsb;
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\"': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Fastid)this.mUHF.mObject).fastid_switch = this.data[this.mark_head + 4];
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u0019': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            int reveive_len = this.data[this.mark_head + 4] << 8 | this.data[this.mark_head + 5];
                            ((Tags_data)this.mUHF.mObject).data = new char[reveive_len *= 2];
                            int i = 0;
                            while (i < reveive_len) {
                                ((Tags_data)this.mUHF.mObject).data[i] = this.data[this.mark_head + 6 + i];
                                ++i;
                            }
                            ((Tags_data)this.mUHF.mObject).ant_id = this.data[this.mark_head + 6 + reveive_len];
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u001b': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Lock)this.mUHF.mObject).ant_id = this.data[this.mark_head + 4];
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u001c': {
                        if (this.data[this.mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Kill)this.mUHF.mObject).ant_id = this.data[this.mark_head + 4];
                        }
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                    case '\u0016': {
                        if (this.data[this.mark_head + 1] == '\u00ff') {
                            CommandType.CommandOK = false;
                            this.datalen = 0;
                            System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                            continue block17;
                        }
                        EPC mTemp = new EPC();
                        mTemp.pc_msb = this.data[this.mark_head + 3];
                        mTemp.pc_lsb = this.data[this.mark_head + 4];
                        int pc = (mTemp.pc_msb & 255) << 8 | mTemp.pc_lsb & 255;
                        pc = (pc & 63488) >> 11;
                        mTemp.epc_len = pc * 2;
                        char[] epc_data = new char[mTemp.epc_len];
                        int i = 0;
                        while (i < epc_data.length) {
                            epc_data[i] = this.data[this.mark_head + 5 + i];
                            ++i;
                        }
                        mTemp.epc = epc_data;
                        ((Query_epc)this.mUHF.mObject).epc = mTemp;
                        ((Query_epc)this.mUHF.mObject).rssi_msb = this.data[this.mark_head + 5 + mTemp.epc_len];
                        ((Query_epc)this.mUHF.mObject).rssi_msb = this.data[this.mark_head + 5 + mTemp.epc_len + 1];
                        ((Query_epc)this.mUHF.mObject).ant_id = this.data[this.mark_head + 5 + mTemp.epc_len + 2];
                        CommandType.CommandOK = true;
                        this.datalen = 0;
                        System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
                        continue block17;
                    }
                }
                if (this.mc == null) continue;
                this.datalen = 0;
                System.arraycopy(this.data1, 0, this.data, 0, this.data1.length);
            }
        }
        return 1;
    }

    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        Log.v("onProgressUpdate", "ReceiveThread ------");
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected /* varargs */ void onProgressUpdate(String ... values) {
    }

    private int findCharInReceiveData(int start, char ch) {
        int i = start;
        while (i < this.datalen) {
            if (this.data[i] == ch) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void CheckSum(int start, char[] data, int len) {
        data[start + len] = '\u0000';
        int i = start;
        while (i < start + len) {
            data[start + len] = (char)(data[start + len] + data[i]);
            ++i;
        }
        data[start + len] = (char)(data[start + len] & 255);
    }
}
