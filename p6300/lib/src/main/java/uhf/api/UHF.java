/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.AsyncTask
 *  android.util.Log
 *  jni.Linuxc
 */
package uhf.api;

import android.os.AsyncTask;
import android.util.Log;
import jni.Linuxc;
import uhf.api.Baud_rate;
import uhf.api.CommandType;
import uhf.api.Fastid;
import uhf.api.Frequency_region;
import uhf.api.Gen2;
import uhf.api.Kill;
import uhf.api.Lock;
import uhf.api.MultiLableCallBack;
import uhf.api.Multi_interval;
import uhf.api.Multi_query_epc;
import uhf.api.Output_frequency;
import uhf.api.Power;
import uhf.api.ReceiveThread;
import uhf.api.ShareData;
import uhf.api.Tags_data;

public class UHF {
    public int com_fd;
    public String com_name;
    public int com_baudrate;
    public int returntimeout;
    public int returnminlen;
    public ReceiveThread mReceiveThread;
    public Object mObject;

    public UHF() {
    }

    public UHF(String com_name, int com_baudrate, int returntimeout, int returnminlen) {
        this.com_name = com_name;
        this.com_baudrate = com_baudrate;
        this.returntimeout = returntimeout;
        this.returnminlen = returnminlen;
    }

    public int transfer_open(UHF mUHF) {
        mUHF.com_fd = Linuxc.openUart(mUHF.com_name);
        if (mUHF.com_fd > 0) {
            Linuxc.setUart(mUHF.com_fd, mUHF.com_baudrate, mUHF.returntimeout, mUHF.returnminlen);
            this.mReceiveThread = new ReceiveThread(mUHF);
            this.mReceiveThread.execute(new Integer[]{0,0,0,});
        }
        return mUHF.com_fd;
    }

    public void transfer_close(UHF mUHF) {
        this.mReceiveThread.isFlag = false;
        Linuxc.closeUart(mUHF.com_fd);
    }

    public void transfer_send(int fd, char[] data, int len) {
        char[] cmd = new char[len];
        System.arraycopy(data, 0, cmd, 0, len);
        int i = 0;
        while (i < cmd[2] + 6) {
            Log.e("", (UHF.change(Integer.toHexString(cmd[i])) + " "));
            ++i;
        }
        String strcmd = String.valueOf(cmd);
        Linuxc.sendMsgUartHex(fd, strcmd, strcmd.length());
    }

    public String transfer_recv(int fd) {
        return Linuxc.receiveMsgUartHex(fd);
    }

    public boolean command(char command_type, Object param) {
        this.mObject = param;
        CommandType.LastCommand = command_type;
        CommandType.CommandOK = false;
        char[] cmd = new char[128];
        cmd[0] = 187;
        cmd[1] = command_type;
        switch (command_type) {
            case '\u0000': {
                cmd[2] = 3;
                cmd[3] = (char)((Power)param).loop;
                cmd[4] = (char)((Power)param).read;
                cmd[5] = (char)((Power)param).write;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[7] = 13;
                cmd[8] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u0002': {
                cmd[2] = (char)(((Output_frequency)param).frequency_num * 3 + 1);
                cmd[3] = (char)((Output_frequency)param).frequency_num;
                int index = 0;
                while (index < ((Output_frequency)param).frequency_num) {
                    int iFreq = Integer.valueOf(String.valueOf(((Output_frequency)param).frequency[index] * 1000.0f));
                    cmd[4 + index * 3] = (char)(iFreq >> 16);
                    cmd[4 + index * 3 + 1] = (char)(iFreq >> 8);
                    cmd[4 + index * 3 + 2] = (char)(iFreq & 255);
                    ++index;
                }
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4 + cmd[2]] = 13;
                cmd[5 + cmd[2]] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u0007': {
                cmd[2] = 4;
                cmd[3] = (char)((Gen2)param).Q;
                cmd[4] = (char)(((Gen2)param).startQ << 4 | ((Gen2)param).MinQ);
                cmd[5] = (char)(((Gen2)param).MaxQ << 4);
                cmd[6] = '\u0000';
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[8] = 13;
                cmd[9] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\t': {
                cmd[2] = 2;
                cmd[3] = (char)((Frequency_region)param).save_setting;
                cmd[4] = (char)((Frequency_region)param).region;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[6] = 13;
                cmd[7] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\n': 
            case '\u000b': 
            case '\f': 
            case '\r': 
            case '\u0011': 
            case '\u0012': 
            case '\u0014': 
            case '\u0016': 
            case '\u0018': 
            case '\u001e': 
            case '\"': {
                cmd[2] = '\u0000';
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4] = 13;
                cmd[5] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u001d': {
                ((Multi_interval)param).work_time_msb = (((Multi_interval)param).work_time & 65280) >> 8;
                ((Multi_interval)param).work_time_lsb = ((Multi_interval)param).work_time & 255;
                ((Multi_interval)param).interval_msb = (((Multi_interval)param).interval & 65280) >> 8;
                ((Multi_interval)param).interval_lsb = ((Multi_interval)param).interval & 255;
                cmd[2] = 4;
                cmd[3] = (char)((Multi_interval)param).work_time_msb;
                cmd[4] = (char)((Multi_interval)param).work_time_lsb;
                cmd[5] = (char)((Multi_interval)param).interval_msb;
                cmd[6] = (char)((Multi_interval)param).interval_lsb;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[8] = 13;
                cmd[9] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '!': {
                cmd[2] = '\u0001';
                cmd[3] = (char)((Fastid)param).fastid_switch;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[5] = 13;
                cmd[6] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '#': {
                cmd[2] = '\u0001';
                cmd[3] = (char)((Baud_rate)param).rate_type;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[5] = 13;
                cmd[6] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u0019': {
                ((Tags_data)param).filterData_len_msb = (((Tags_data)param).filterData_len & 65280) >> 8;
                ((Tags_data)param).filterData_len_lsb = ((Tags_data)param).filterData_len & 255;
                ((Tags_data)param).start_addr_msb = (((Tags_data)param).start_addr & 65280) >> 8;
                ((Tags_data)param).start_addr_lsb = ((Tags_data)param).start_addr & 255;
                ((Tags_data)param).data_len_msb = (((Tags_data)param).data_len & 65280) >> 8;
                ((Tags_data)param).data_len_lsb = ((Tags_data)param).data_len & 255;
                cmd[2] = (char)(12 + ((Tags_data)param).filterData_len);
                char[] char_pwd = new char[4];
                ShareData.HexToDec(((Tags_data)param).password, char_pwd);
                cmd[3] = char_pwd[0];
                cmd[4] = char_pwd[1];
                cmd[5] = char_pwd[2];
                cmd[6] = char_pwd[3];
                cmd[7] = (char)((Tags_data)param).FMB;
                cmd[8] = (char)((Tags_data)param).filterData_len_msb;
                cmd[9] = (char)((Tags_data)param).filterData_len_lsb;
                int index = 0;
                while (index < ((Tags_data)param).filterData_len) {
                    cmd[10 + index] = ((Tags_data)param).filterData[index];
                    ++index;
                }
                cmd[10 + ((Tags_data)param).filterData_len] = (char)((Tags_data)param).mem_bank;
                cmd[10 + ((Tags_data)param).filterData_len + 1] = (char)((Tags_data)param).start_addr_msb;
                cmd[10 + ((Tags_data)param).filterData_len + 2] = (char)((Tags_data)param).start_addr_lsb;
                cmd[10 + ((Tags_data)param).filterData_len + 3] = (char)((Tags_data)param).data_len_msb;
                cmd[10 + ((Tags_data)param).filterData_len + 4] = (char)((Tags_data)param).data_len_lsb;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4 + cmd[2]] = 13;
                cmd[5 + cmd[2]] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u001a': {
                ((Tags_data)param).filterData_len_msb = (((Tags_data)param).filterData_len & 65280) >> 8;
                ((Tags_data)param).filterData_len_lsb = ((Tags_data)param).filterData_len & 255;
                ((Tags_data)param).start_addr_msb = (((Tags_data)param).start_addr & 65280) >> 8;
                ((Tags_data)param).start_addr_lsb = ((Tags_data)param).start_addr & 255;
                ((Tags_data)param).data_len_msb = (((Tags_data)param).data_len & 65280) >> 8;
                ((Tags_data)param).data_len_lsb = ((Tags_data)param).data_len & 255;
                cmd[2] = (char)(12 + ((Tags_data)param).filterData_len + ((Tags_data)param).data_len * 2);
                char[] char_pwd1 = new char[4];
                ShareData.HexToDec(((Tags_data)param).password, char_pwd1);
                cmd[3] = char_pwd1[0];
                cmd[4] = char_pwd1[1];
                cmd[5] = char_pwd1[2];
                cmd[6] = char_pwd1[3];
                cmd[7] = (char)((Tags_data)param).FMB;
                cmd[8] = (char)((Tags_data)param).filterData_len_msb;
                cmd[9] = (char)((Tags_data)param).filterData_len_lsb;
                int index = 0;
                while (index < ((Tags_data)param).filterData_len) {
                    cmd[10 + index] = ((Tags_data)param).filterData[index];
                    ++index;
                }
                cmd[10 + ((Tags_data)param).filterData_len] = (char)((Tags_data)param).mem_bank;
                cmd[10 + ((Tags_data)param).filterData_len + 1] = (char)((Tags_data)param).start_addr_msb;
                cmd[10 + ((Tags_data)param).filterData_len + 2] = (char)((Tags_data)param).start_addr_lsb;
                cmd[10 + ((Tags_data)param).filterData_len + 3] = (char)((Tags_data)param).data_len_msb;
                cmd[10 + ((Tags_data)param).filterData_len + 4] = (char)((Tags_data)param).data_len_lsb;
                index = 0;
                while (index < ((Tags_data)param).data_len * 2) {
                    cmd[10 + ((Tags_data)param).filterData_len + 5 + index] = ((Tags_data)param).data[index];
                    ++index;
                }
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4 + cmd[2]] = 13;
                cmd[5 + cmd[2]] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u001b': {
                ((Lock)param).lData_a = new char[3];
                ((Lock)param).lData_a[0] = (char)(((Lock)param).lData_Mask >> 6 & 15);
                ((Lock)param).lData_a[1] = (char)(((((Lock)param).lData_Mask & 63) << 2) + (((Lock)param).lData_Action >> 8) & 3);
                ((Lock)param).lData_a[2] = (char)(((Lock)param).lData_Action & 255);
                ((Lock)param).filterData_len_msb = (((Lock)param).filterData_len & 65280) >> 8;
                ((Lock)param).filterData_len_lsb = ((Lock)param).filterData_len & 255;
                cmd[2] = (char)(10 + ((Lock)param).filterData_len);
                char[] char_pwd2 = new char[4];
                ShareData.HexToDec(((Lock)param).password, char_pwd2);
                cmd[3] = char_pwd2[0];
                cmd[4] = char_pwd2[1];
                cmd[5] = char_pwd2[2];
                cmd[6] = char_pwd2[3];
                cmd[7] = (char)((Lock)param).FMB;
                cmd[8] = (char)((Lock)param).filterData_len_msb;
                cmd[9] = (char)((Lock)param).filterData_len_lsb;
                int index = 0;
                while (index < ((Lock)param).filterData_len) {
                    cmd[10 + index] = ((Lock)param).filterData[index];
                    ++index;
                }
                cmd[10 + ((Lock)param).filterData_len] = ((Lock)param).lData_a[0];
                cmd[10 + ((Lock)param).filterData_len + 1] = ((Lock)param).lData_a[1];
                cmd[10 + ((Lock)param).filterData_len + 2] = ((Lock)param).lData_a[2];
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4 + cmd[2]] = 13;
                cmd[5 + cmd[2]] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u001c': {
                ((Kill)param).filterData_len_msb = (((Kill)param).filterData_len & 65280) >> 8;
                ((Kill)param).filterData_len_lsb = ((Kill)param).filterData_len & 255;
                cmd[2] = (char)(7 + ((Kill)param).filterData_len);
                char[] char_pwd3 = new char[4];
                ShareData.HexToDec(((Kill)param).password, char_pwd3);
                cmd[3] = char_pwd3[0];
                cmd[4] = char_pwd3[1];
                cmd[5] = char_pwd3[2];
                cmd[6] = char_pwd3[3];
                cmd[7] = (char)((Kill)param).FMB;
                cmd[8] = (char)((Kill)param).filterData_len_msb;
                cmd[9] = (char)((Kill)param).filterData_len_lsb;
                int index = 0;
                while (index < ((Kill)param).filterData_len) {
                    cmd[10 + index] = ((Kill)param).filterData[index];
                    ++index;
                }
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4 + cmd[2]] = 13;
                cmd[5 + cmd[2]] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
                break;
            }
            case '\u0017': {
                ((Multi_query_epc)param).query_total_msb = (((Multi_query_epc)param).query_total & 65280) >> 8;
                ((Multi_query_epc)param).query_total_lsb = ((Multi_query_epc)param).query_total & 255;
                cmd[2] = 2;
                cmd[3] = (char)((Multi_query_epc)param).query_total_msb;
                cmd[4] = (char)((Multi_query_epc)param).query_total_lsb;
                this.CheckSum(1, cmd, cmd[2] + 2);
                cmd[4 + cmd[2]] = 13;
                cmd[5 + cmd[2]] = 10;
                this.transfer_send(this.com_fd, cmd, cmd[2] + 6);
            }
        }
        return this.WaiteCMDExecution();
    }

    private static String change(String hexString) {
        String str;
        str = hexString.length() >= 2 ? hexString : "0" + hexString;
        return str;
    }

    public Boolean WaiteCMDExecution() {
        int i = 0;
        while ((long)i < CommandType.TimeOut / 10) {
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (CommandType.CommandOK) {
                CommandType.LastCommand = 254;
                return true;
            }
            ++i;
        }
        CommandType.LastCommand = 254;
        return false;
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

    public void setCallBack(MultiLableCallBack mc) {
        if (this.mReceiveThread != null) {
            this.mReceiveThread.setCallfuc(mc);
        }
    }
}
