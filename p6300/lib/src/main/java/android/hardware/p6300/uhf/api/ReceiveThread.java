/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  android.os.AsyncTask
 *  android.util.Log
 *  android.hardware.p6300.jni.Linuxc
 *  android.hardware.p6300.uhf.api.Tags_data
 *  android.hardware.p6300.uhf.api.Temperature
 *  android.hardware.p6300.uhf.api.UHF
 *  android.hardware.p6300.uhf.api.Ware
 */
package android.hardware.p6300.uhf.api;

import android.hardware.p6300.BuildConfig;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import android.hardware.p6300.jni.Linuxc;

public class ReceiveThread extends AsyncTask<Integer, String, Integer> {
    boolean isFlag = false;
    private UHF mUHF;
    private MultiLableCallBack mc;

    ReceiveThread(UHF mUHF) {
        this.mUHF = mUHF;
        isFlag = true;
    }

    void setCallfuc(MultiLableCallBack mc) {
        this.mc = mc;
    }

    protected Integer doInBackground(Integer ... params) {
        int len;
        String str_receive;
        char[] recvBuff;
        char[] data = new char[2048];
        int datalen = 0;
        int mark_head;

        Log.v("onProgressUpdate", "ReceiveThread ++++++ ");

        block16 : while (isFlag) {
            str_receive = Linuxc.receiveMsgUartHex(mUHF.com_fd);
            if (str_receive == null || (len = str_receive.length()) == 0) {
                continue;
            }

            recvBuff = str_receive.toCharArray();

            if (BuildConfig.DEBUG) {
                StringBuilder sb = new StringBuilder();
                String tmpStr;
                for (char c : recvBuff) {
                    tmpStr = Integer.toHexString(c);
                    sb.append(tmpStr.length() == 1 ? "0" + tmpStr : tmpStr);
                }

                Log.d("UHF", "Считано: " + sb.toString());
            }

            System.arraycopy(recvBuff, 0, data, datalen, len);
            datalen += len;
            block17 : while (datalen >= 6) {
                mark_head = -1;
                for (int i = 0; i < datalen; i++) {
                    if (data[i] == 0xBB) {
                        mark_head = i;
                        break;
                    }
                }

                if (mark_head < 0) {
                    datalen = 0;
                    continue block16;
                }

                if (mark_head + 2 > datalen || mark_head + 6 + data[mark_head + 2] > datalen) {
                    continue block16;
                }

                if (data[mark_head + 3 + data[mark_head + 2] + 1] != '\r' || data[mark_head + 3 + data[mark_head + 2] + 2] != '\n') {
                    datalen = datalen - mark_head - data[mark_head + 2] - 5;
                    System.arraycopy(data, mark_head + data[mark_head + 2] + 5, data, 0, datalen);
                    continue block16;
                }

                char checksum = data[mark_head + 3 + data[mark_head + 2]];
                Log.e("2", ("checksum ==" + Integer.toHexString(checksum)));
                CheckSum(mark_head + 1, data, data[mark_head + 2] + 2);
                if (checksum != data[mark_head + 3 + data[mark_head + 2]]) {
                    Log.e("2", "check error");
                    Log.e("2", ("checksum ==" + Integer.toHexString(data[mark_head + 3 + data[mark_head + 2]])));
                    datalen = datalen - mark_head - data[mark_head + 2] - 5;
                    System.arraycopy(data, mark_head + data[mark_head + 2] + 5, data, 0, datalen);
                    continue block16;
                }

                Log.e("Data", "OK");
                if (mc != null && data[mark_head + 1] == '\u0097') {
                    char[] copydata = new char[data[mark_head + 2]];
                    System.arraycopy(data, mark_head + 3, copydata, 0, copydata.length);
                    mc.method(copydata);
                    datalen = datalen - mark_head - data[mark_head + 2] - 5;
                    System.arraycopy(data, mark_head + data[mark_head + 2] + 5, data, 0, datalen);
                }

                switch (CommandType.LastCommand) {
                    case CommandType.SET_POWER:
                    case CommandType.SET_OUTPUT_FREQUENCY:
                    case CommandType.SET_GEN2_PARAM:
                    case CommandType.SET_FREQUENCY_REGION:
                    case CommandType.WRITE_TAGS_DATA:
                    case CommandType.SET_MULTI_QUERY_TAGS_INTERVAL:
                    case CommandType.SET_FASTID:
                    case CommandType.SET_MODULE_BAUD_RATE: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                        } else {
                            CommandType.CommandOK = false;
                            CommandType.CommandResend = true;
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.STOP_MULTI_QUERY_TAGS_EPC: {
                        CommandType.CommandOK = true;
                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_HARDWARE_VERSION:
                    case CommandType.GET_FIRMWARE_VERSION: {
                        CommandType.CommandOK = true;
                        ((Ware)mUHF.mObject).major_version = data[mark_head + 3];
                        ((Ware)mUHF.mObject).minor_version = data[mark_head + 4];
                        ((Ware)mUHF.mObject).revision_version = data[mark_head + 5];
                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_POWER: {
                        CommandType.CommandOK = true;
                        ((Power)mUHF.mObject).loop = data[mark_head + 3];
                        ((Power)mUHF.mObject).read = data[mark_head + 4];
                        ((Power)mUHF.mObject).write = data[mark_head + 5];
                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_FREQUENCY_STATE: {
                        CommandType.CommandOK = true;
                        ((Output_frequency)mUHF.mObject).frequency_num = data[mark_head + 3];
                        ((Output_frequency)mUHF.mObject).frequency = new float[((Output_frequency)mUHF.mObject).frequency_num];
                        int iLen = ((Output_frequency)mUHF.mObject).frequency_num;
                        int iFreq;
                        int index = 0;
                        while (index < iLen) {
                            iFreq = (data[mark_head + 4 + 3 * index] << 16) + (data[mark_head + 4 + 3 * index + 1] << 8) + data[mark_head + 4 + 3 * index + 2];
                            ((Output_frequency)mUHF.mObject).frequency[index] = (float)((double)iFreq / 1000.0);
                            ++index;
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_FREQUENCY_REGION: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                            ((Frequency_region)mUHF.mObject).region = data[mark_head + 4];
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_MODULE_TEMPERATURE: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                            ((Temperature)mUHF.mObject).temp_msb = data[mark_head + 4];
                            ((Temperature)mUHF.mObject).temp_lsb = data[mark_head + 5];
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_GEN2_PARAM: {
                        CommandType.CommandOK = true;
                        ((Gen2)mUHF.mObject).Q = data[mark_head + 3] & 0x01;
                        ((Gen2)mUHF.mObject).startQ = data[mark_head + 4] >> 4 & 15;
                        ((Gen2)mUHF.mObject).MinQ = data[mark_head + 4] & 15;
                        ((Gen2)mUHF.mObject).MaxQ = data[mark_head + 5] >> 4 & 15;
                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_MULTI_QUERY_TAGS_INTERVAL: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                            ((Multi_interval)mUHF.mObject).work_time_msb = data[mark_head + 4];
                            ((Multi_interval)mUHF.mObject).work_time_lsb = data[mark_head + 5];
                            ((Multi_interval)mUHF.mObject).interval_msb = data[mark_head + 6];
                            ((Multi_interval)mUHF.mObject).interval_lsb = data[mark_head + 7];
                            ((Multi_interval)mUHF.mObject).work_time = ((Multi_interval)mUHF.mObject).work_time_msb << 8 | ((Multi_interval)mUHF.mObject).work_time_lsb;
                            ((Multi_interval)mUHF.mObject).interval = ((Multi_interval)mUHF.mObject).interval_msb << 8 | ((Multi_interval)mUHF.mObject).interval_lsb;
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.GET_FASTID: {
                        if (data[mark_head + 3] == '\u0001') {
                            CommandType.CommandOK = true;
                            ((Fastid)mUHF.mObject).fastid_switch = data[mark_head + 4];
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.READ_TAGS_DATA: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                            int reveive_len = data[mark_head + 4] << 8 | data[mark_head + 5];
                            ((Tags_data)mUHF.mObject).data = new char[reveive_len *= 2];
                            int i = 0;
                            while (i < reveive_len) {
                                ((Tags_data)mUHF.mObject).data[i] = data[mark_head + 6 + i];
                                ++i;
                            }
                            ((Tags_data)mUHF.mObject).ant_id = data[mark_head + 6 + reveive_len];
                        } else {
                            CommandType.CommandOK = false;
                            CommandType.CommandResend = true;
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.LOCK_TAGS: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                            ((Lock)mUHF.mObject).ant_id = data[mark_head + 4];
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.KILL_TAGS: {
                        if (data[mark_head + 3] == 0x01) {
                            CommandType.CommandOK = true;
                            ((Kill)mUHF.mObject).ant_id = data[mark_head + 4];
                        }

                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                    case CommandType.SINGLE_QUERY_TAGS_EPC: {
                        if (data[mark_head + 1] == 0xFF) {
                            Log.e("UHF", "Reseive COMMAND_ERROR_RESPOND");
                            CommandType.CommandOK = false;
                            CommandType.CommandResend = true;
                            datalen = 0;
                            Arrays.fill(data, (char)0);
                            continue block17;
                        }

                        EPC mTemp = new EPC();
                        mTemp.pc_msb = data[mark_head + 3];
                        mTemp.pc_lsb = data[mark_head + 4];
                        int pc = (mTemp.pc_msb & 255) << 8 | mTemp.pc_lsb & 255;
                        pc = (pc & 63488) >> 11;
                        mTemp.epc_len = pc * 2;
                        char[] epc_data = new char[mTemp.epc_len];
                        int i = 0;
                        while (i < epc_data.length) {
                            epc_data[i] = data[mark_head + 5 + i];
                            ++i;
                        }

                        mTemp.epc = epc_data;
                        ((Query_epc)mUHF.mObject).epc = mTemp;
                        ((Query_epc)mUHF.mObject).rssi_msb = data[mark_head + 5 + mTemp.epc_len];
                        ((Query_epc)mUHF.mObject).rssi_msb = data[mark_head + 5 + mTemp.epc_len + 1];
                        ((Query_epc)mUHF.mObject).ant_id = data[mark_head + 5 + mTemp.epc_len + 2];
                        CommandType.CommandOK = true;
                        CommandType.CommandResend = false;
                        datalen = 0;
                        Arrays.fill(data, (char)0);
                        continue block17;
                    }
                }
                if (mc == null) continue;
                datalen = 0;
                Arrays.fill(data, (char)0);
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

    protected void onProgressUpdate(String ... values) {
    }

    private void CheckSum(int start, char[] data, int len) {
        data[start + len] = 0x00;
        int i = start;
        while (i < start + len) {
            data[start + len] = (char)(data[start + len] + data[i]);
            ++i;
        }

        data[start + len] = (char)(data[start + len] & 255);
    }
}
