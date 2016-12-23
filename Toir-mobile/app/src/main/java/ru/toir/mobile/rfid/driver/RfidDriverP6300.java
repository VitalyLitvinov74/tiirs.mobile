package ru.toir.mobile.rfid.driver;

import android.hardware.p6300.jni.Linuxc;
import android.hardware.p6300.uhf.api.CommandType;
import android.hardware.p6300.uhf.api.Query_epc;
import android.hardware.p6300.uhf.api.ShareData;
import android.hardware.p6300.uhf.api.Tags_data;
import android.hardware.p6300.uhf.api.UHF;
import android.hardware.p6300.uhf.api.Ware;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.toir.mobile.R;
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
    // TODO: в переспективе нужно переписать код из "SDK"
    // Один из недостатков SDK это то что разбор данных из считывателя происходит в том же потоке
    // который отправляет команду в считыватель. В частности это приводит к тому, что диалог с
    // указанием поднести метку пользователю не показывается.
    // Помимо этого нестабильно работает отправка двух команд подряд. В частности при записи
    // в два шага данных размером более 32 байт.
    @SuppressWarnings("unused")
    // к этому свойству обращаемся не на прямую
    public static final String DRIVER_NAME = "Драйвер UHF P6300";
    private static final String TAG = "RfidDriverP6300";
    private UHF mUhf;

    private static final int EPC = 0;
    private static final int TID = 1;

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

        // задержка чтоб дать время для инициализации считывателя
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        Ware ware = new Ware(CommandType.GET_FIRMWARE_VERSION, 0, 0, 0);

        // тупая форма инициализации считывателя, как есть в примерах SDK
        boolean result = mUhf.command(CommandType.GET_FIRMWARE_VERSION, ware);
        if (!result) {
            result = mUhf.command(CommandType.GET_FIRMWARE_VERSION, ware);
            if (!result) {
                result = mUhf.command(CommandType.GET_FIRMWARE_VERSION, ware);
            }
        }

        if (result) {
            Log.d(TAG, "FW Ver." + ware.major_version + "." + ware.minor_version + "." + ware.revision_version);
            if (checkVersion(ware)) {
                result = true;
            } else {
                powerOff();
                result = false;
            }
        } else {
            powerOff();
            result = false;
        }

        return result;
    }

    @Override
    public void readTagId() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Query_epc query_epc = new Query_epc();
                boolean result = mUhf.command(CommandType.SINGLE_QUERY_TAGS_EPC, query_epc);
                if (result) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%02X%02X", query_epc.epc.pc_msb, query_epc.epc.pc_lsb));
                    for(char c : query_epc.epc.epc) {
                        sb.append(String.format("%02X", (int)c));
                    }

                    Log.d(TAG, "EPC len = " + query_epc.epc.epc_len + ", tagId = " + sb.toString());
                    sHandler.obtainMessage(RESULT_RFID_SUCCESS, sb.toString()).sendToTarget();
                } else {
                    Log.d(TAG, "EPC not readed...");
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }
            }
        });

        thread.start();
    }

    @Override
    public void readTagData(final String password, final int memoryBank, final int address,
                            final int count) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // ищем первую попавшуюся метку
                Query_epc query_epc = new Query_epc();
                String tagId;
                boolean result = mUhf.command(CommandType.SINGLE_QUERY_TAGS_EPC, query_epc);
                if (result) {
                    StringBuilder sb = new StringBuilder();
                    for(char c : query_epc.epc.epc) {
                        sb.append(String.format("%02X", (int)c));
                    }

                    Log.d(TAG, "EPC len = " + query_epc.epc.epc_len + ", tagId = " + sb.toString());
                    tagId = sb.toString();
                } else {
                    Log.d(TAG, "EPC not readed...");
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    return;
                }

                // читаем данные из найденной метки
                int filterLength = tagId.length() / 2;
                if (filterLength % 2 != 0) {
                    // Filter Hex number must be multiples of 4
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    return;
                }

                Tags_data tags_data = new Tags_data();
                char[] tmpTagId = new char[filterLength];
                result = ShareData.StringToChar(tagId, tmpTagId, filterLength);
                if (result) {
                    tags_data.filterData_len = filterLength;
                    tags_data.filterData = tmpTagId;
                } else {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    return;
                }

                tags_data.password = password;
                tags_data.FMB = EPC;
                tags_data.start_addr = address;
                tags_data.data_len = count / 2;
                tags_data.mem_bank = memoryBank;

                result = mUhf.command(CommandType.READ_TAGS_DATA, tags_data);
                if (result) {
                    // TODO: заменить всю эту херобору на простые функциональные методы!
                    String content = ShareData.CharToString(tags_data.data, tags_data.data.length);
                    content = content.replaceAll("\\s", "");
                    sHandler.obtainMessage(RESULT_RFID_SUCCESS, content).sendToTarget();
                } else {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }
            }
        });
        thread.start();
    }

    @Override
    public void readTagData(final String password, final String tagId, final int memoryBank,
                            final int address, final int count) {

        final int filterLength = tagId.length() / 2;
        if (filterLength % 2 != 0) {
            // Filter Hex number must be multiples of 4
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Tags_data tags_data = new Tags_data();
                char[] tmpTagId = new char[filterLength];
                boolean result;
                result = ShareData.StringToChar(tagId, tmpTagId, filterLength);
                if (result) {
                    tags_data.filterData_len = filterLength;
                    tags_data.filterData = tmpTagId;
                } else {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    return;
                }

                tags_data.password = password;
                tags_data.FMB = EPC;
                tags_data.start_addr = address;
                tags_data.data_len = count / 2;
                tags_data.mem_bank = memoryBank;

                result = mUhf.command(CommandType.READ_TAGS_DATA, tags_data);
                if (result) {
                    // TODO: заменить всю эту херобору на простые функциональные методы!
                    String content = ShareData.CharToString(tags_data.data, tags_data.data.length);
                    content = content.replaceAll("\\s", "");
                    sHandler.obtainMessage(RESULT_RFID_SUCCESS, content).sendToTarget();
                } else {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }
            }
        });

        thread.start();
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        // TODO: реализовать запись в метку если объём данных для записи более 32 байт в два шага
        // ищем первую попавшуюся метку
        Query_epc query_epc = new Query_epc();
        String tagId;
        boolean result = mUhf.command(CommandType.SINGLE_QUERY_TAGS_EPC, query_epc);
        if (result) {
            Log.d(TAG, "EPC readed...");
            Log.d(TAG, "len = " + query_epc.epc.epc_len);
            StringBuilder sb = new StringBuilder();
            for(char c : query_epc.epc.epc) {
                sb.append(String.format("%02X", (int)c));
            }

            tagId = sb.toString();
        } else {
            Log.d(TAG, "EPC not readed...");
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            return;
        }

        int filterLength = tagId.length() / 2;
        if (filterLength % 2 != 0) {
            // Filter Hex number must be multiples of 4
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            return;
        }

        Tags_data tags_data = new Tags_data();
        char[] tmpTagId = new char[filterLength];
        result = ShareData.StringToChar(tagId, tmpTagId, filterLength);
        if (result) {
            tags_data.filterData_len = filterLength;
            tags_data.filterData = tmpTagId;
        } else {
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            return;
        }

        tags_data.password = password;
        tags_data.FMB = EPC;
        tags_data.start_addr = address;
        tags_data.mem_bank = memoryBank;
        int dataLength = data.length() / 2;
        char dataToWrite[] = new char[dataLength];
        result = ShareData.StringToChar(data, dataToWrite, dataLength);
        if (!result) {
            sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
            return;
        }

        tags_data.data_len = dataLength;
        tags_data.data = dataToWrite;
        result = mUhf.command(CommandType.WRITE_TAGS_DATA, tags_data);
        if (result) {
            sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
        } else {
            sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
        }
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank, int address, String data) {

        int filterLength = tagId.length() / 2;
        if (filterLength % 2 != 0) {
            // Filter Hex number must be multiples of 4
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            return;
        }

        Tags_data tags_data = new Tags_data();
        char[] tmpTagId = new char[filterLength];
        boolean result = ShareData.StringToChar(tagId, tmpTagId, filterLength);
        if (result) {
            tags_data.filterData_len = filterLength;
            tags_data.filterData = tmpTagId;
        } else {
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            return;
        }

        tags_data.password = password;
        tags_data.FMB = EPC;
        tags_data.start_addr = address;
        tags_data.mem_bank = memoryBank;
        int dataLength = data.length() / 2;
        char dataToWrite[];

        if (dataLength > 32) {
            // пишем только первыве 32 байта
            dataToWrite = new char[32];
            result = ShareData.StringToChar(data, dataToWrite, 32);
            if (!result) {
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                return;
            }

            tags_data.data_len = 16; // 32 bytes = 16 words
            tags_data.data = dataToWrite;
            if (!mUhf.command(CommandType.WRITE_TAGS_DATA, tags_data)) {
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                return;
            }

            // пишем остатки
            dataLength -= 32;
            dataToWrite = new char[dataLength];
            String restData = data.substring(64);
            result = ShareData.StringToChar(restData, dataToWrite, dataLength);
            if (!result) {
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                return;
            }

            // пытаемся завершить поток разбора предыдущей команды, для отправки следующей
            mUhf.mReceiveThread.cancel(true);
            try {
                mUhf.mReceiveThread.get();
            } catch (Exception e) {
//                Log.d(TAG, e.getLocalizedMessage());
            }

            tags_data.start_addr = address + 16;
            tags_data.data_len = dataLength / 2;
            tags_data.data = dataToWrite;
            if (mUhf.command(CommandType.WRITE_TAGS_DATA, tags_data)) {
                sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
            } else {
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
            }

        } else {
            // пишем то что передали
            dataToWrite = new char[dataLength];
            result = ShareData.StringToChar(data, dataToWrite, dataLength);
            if (!result) {
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                return;
            }

            tags_data.data_len = dataLength / 2;
            tags_data.data = dataToWrite;
            if (mUhf.command(CommandType.WRITE_TAGS_DATA, tags_data)) {
                sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
            } else {
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
            }
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
        return inflater.inflate(R.layout.rfid_read, viewGroup);
    }

    private boolean powerOn() {
        String cmdPowerOff = "echo 1 > /sys/devices/soc.0/xt_dev.68/xt_dc_in_en";
        CommandResult tt = ShellUtils.execCommand(cmdPowerOff, false);
        return tt.result == 0;
    }

    private boolean powerOff() {
        String cmdPowerOn = "echo 0 > /sys/devices/soc.0/xt_dev.68/xt_dc_in_en";
        CommandResult tt = ShellUtils.execCommand(cmdPowerOn, false);
        return tt.result == 0;
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
