package ru.toir.mobile.rfid.driver;

import android.hardware.p6300.jni.Linuxc;
import android.hardware.p6300.uhf.api.CommandType;
import android.hardware.p6300.uhf.api.EPC;
import android.hardware.p6300.uhf.api.Query_epc;
import android.hardware.p6300.uhf.api.Tags_data;
import android.hardware.p6300.uhf.api.UHF;
import android.hardware.p6300.uhf.api.Ware;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.ShellUtils;
import ru.toir.mobile.utils.ShellUtils.CommandResult;

/**
 * @author Dmitriy Logachev
 *         <p>
 *         Драйвер считывателя RFID для устройства p6300
 *         </p>
 */
@SuppressWarnings("unused")
public class RfidDriverP6300 extends RfidDriverBase {
    @SuppressWarnings("unused")
    // к этому свойству обращаемся не на прямую
    public static final String DRIVER_NAME = "Драйвер UHF P6300";
    private static final String TAG = "RfidDriverP6300";
    private UHF mUhf;

    @Override
    public boolean init() {
        if (powerOn()) {
            Log.d(TAG, "Powered successeful...");
        } else {
            Log.e(TAG, "Powered fail...");
            return false;
        }

        mUhf = new UHF("/dev/ttysWK2", Linuxc.BAUD_RATE_115200, 1, 0);
        mUhf.com_fd = mUhf.transfer_open(mUhf);
        if(mUhf.com_fd < 0) {
            powerOff();
            return false;
        }

        Ware ware = new Ware(CommandType.GET_FIRMWARE_VERSION, 0, 0, 0);
        boolean result = mUhf.command(CommandType.GET_FIRMWARE_VERSION, ware);
        result = mUhf.command(CommandType.GET_FIRMWARE_VERSION, ware);
        if (result) {
            Log.d(TAG, "FW Ver." + ware.major_version + "." + ware.minor_version + "." + ware.revision_version);
            if (checkVersion(ware)) {
                return true;
            } else {
                powerOff();
                return false;
            }
        } else {
            powerOff();
            return false;
        }
    }

    @Override
    public void readTagId() {
        Query_epc query_epc = new Query_epc();
        boolean result = mUhf.command(CommandType.SINGLE_QUERY_TAGS_EPC, query_epc);
        if (result) {
            Log.d(TAG, "EPC readed...");
            Log.d(TAG, "len = " + query_epc.epc.epc_len);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%02X%02X", query_epc.epc.pc_msb, query_epc.epc.pc_lsb));
            for(char c : query_epc.epc.epc) {
                sb.append(String.format("%02X", (int)c));
            }

            sHandler.obtainMessage(RESULT_RFID_SUCCESS, sb.toString()).sendToTarget();
        } else {
            Log.d(TAG, "EPC not readed...");
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
        }
    }

    @Override
    public void readTagData(String password, int memoryBank, int address, int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank, int address, int count) {
        Tags_data tags_data = new Tags_data();
//         TODO: разобраться как указывается id метки которую читаем
//        String[] strArray=str_filter.split(" ");
//        char[] data = new char[strArray.length];
//        if(!ShareData.StringToChar(str_filter, data,strArray.length)) {
//            this.setTitle("handleAccRead1 00 11 22 33 44");
//            return;
//        }
//
//        if(count_filter%2!=0) {
//            this.setTitle("Filter Hex number must be multiples of 4");
//            return;
//        }
//        mTags_data.filterData_len=data.length;
//        mTags_data.filterData=data;

//         TODO: уточнить в каком виде передаётся пароль к метке и остальные параметры
        tags_data.password = password;
//        tags_data.FMB = int_filter;
        tags_data.start_addr = address;
        tags_data.data_len = count;
        tags_data.mem_bank = memoryBank;

        boolean result = mUhf.command(CommandType.READ_TAGS_DATA, tags_data);
        if (result) {
            sHandler.obtainMessage(RESULT_RFID_SUCCESS, tags_data.data).sendToTarget();
        } else {
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
        }
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank, int address, String data) {
        Tags_data tags_data = new Tags_data();
//         TODO: разобраться как указывается id метки которую пишем
//        String[] strArray=str_filter.split(" ");
//        char[] data = new char[strArray.length];
//        if(!ShareData.StringToChar(str_filter, data,strArray.length)) {
//            this.setTitle("handleAccRead1 00 11 22 33 44");
//            return;
//        }
//
//        if(count_filter%2!=0) {
//            this.setTitle("Filter Hex number must be multiples of 4");
//            return;
//        }
//        mTags_data.filterData_len=data.length;
//        mTags_data.filterData=data;

//         TODO: уточнить в каком виде передаётся пароль к метке и остальные параметры
        tags_data.password = password;
//        tags_data.FMB = int_filter;
        tags_data.start_addr = address;
        tags_data.mem_bank = memoryBank;
        tags_data.data_len = data.length();
        tags_data.data = data.toCharArray();

        boolean result = mUhf.command(CommandType.WRITE_TAGS_DATA, tags_data);
        if (result) {
            sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
        } else {
            sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
        }
    }

    @Override
    public void close() {
        mUhf.transfer_close(mUhf);
        mUhf = null;
        powerOff();
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        return null;
    }

    private boolean powerOn() {
        String cmdPowerOff = "echo 1 > /sys/devices/soc.0/xt_dev.68/xt_dc_in_en";
        CommandResult tt = ShellUtils.execCommand(cmdPowerOff, false);
        return tt.result == 0 ? true : false;
    }

    private boolean powerOff() {
        String cmdPowerOn = "echo 0 > /sys/devices/soc.0/xt_dev.68/xt_dc_in_en";
        CommandResult tt = ShellUtils.execCommand(cmdPowerOn, false);
        return tt.result == 0 ? true : false;
    }

    /**
     * Проверяем что
     * major_version = 1
     * minor_version равно любому значению из 0, 1, 2, 3
     * revision_version равно любому значению из 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
     *
     * @param ware результат запроса версии RFID считывателя
     * @return boolean
     */
    private boolean checkVersion(Ware ware) {

        if (ware.major_version == 1) {
            if (ware.minor_version >=0 && ware.minor_version <=3) {
                if (ware.revision_version >=0 && ware.revision_version <=9) {
                    return true;
                }
            }
        }

        return false;
    }
}
