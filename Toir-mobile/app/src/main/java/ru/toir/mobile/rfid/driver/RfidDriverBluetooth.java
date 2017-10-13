package ru.toir.mobile.rfid.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.toir.mobile.R;
import ru.toir.mobile.bluetooth.BTRfidServer;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;

/**
 * @author Dmitriy Logachev
 *         <p>
 *         Драйвер считывателя RFID который использует реальный считыватель на
 *         другом устройстве через bluetooth.
 *         </p>
 */
public class RfidDriverBluetooth extends RfidDriverBase implements IRfidDriver {

    public static final String DRIVER_NAME = "Bluetooth драйвер";
    public static final String SERVER_MAC_PREF_KEY = "rfidDrvBluetoothServer";
    public static final int DRIVER_STATE_READ_ANSWER = 1;
    public static final int DRIVER_STATE_DISCONNECT = 2;
    private String mServerMac;
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice;
    private CommunicationThread mCommunicationThread;

    @Override
    public boolean init() {
        if (mContext != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            mServerMac = preferences.getString(SERVER_MAC_PREF_KEY, null);
            if (mServerMac == null) {
                return false;
            }

            Log.d(TAG, "serverMAC = " + mServerMac);

            mAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mAdapter == null) {
                return false;
            }

            // если адаптер не включен, вернём false
            int btState = mAdapter.getState();
            if (btState != BluetoothAdapter.STATE_ON) {
                return false;
            }

            mDevice = mAdapter.getRemoteDevice(mServerMac);

            Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
            boolean devicePaired = false;
            for (BluetoothDevice device : devices) {
                if (mServerMac.equals(device.getAddress())) {
                    devicePaired = true;
                    break;
                }
            }

            // если MAC адреса сервера нет среди спареных устройств,
            // инициализация не удалась
            if (!devicePaired) {
                return false;
            }

            // блокирующий вариант соединения с сервером, для того что-бы
            // предотвратить ситуацию когда соединение еще не установлено, а
            // запрос с командой уже отправляем
            if (mDevice != null) {
                BluetoothSocket socket;
                try {
                    socket = mDevice.createRfcommSocketToServiceRecord(BTRfidServer.BT_SERVICE_RECORD_UUID);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    return false;
                }

                try {
                    socket.connect();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    return false;
                }

                // обработчик сообщений из потока работы с сервером
                Handler handler = new Handler(new Handler.Callback() {

                    @Override
                    public boolean handleMessage(Message message) {
                        Log.d(TAG, "Получили сообщение от сервера!!!");
                        Bundle bundle = (Bundle) message.obj;
                        int result = -1;
                        String data;
                        if (bundle != null) {
                            result = bundle.getInt("result");
                        }

                        switch (message.what) {
                            case DRIVER_STATE_READ_ANSWER:
                                switch (message.arg1) {
                                    case RfidDialog.READER_COMMAND_READ_ID:
                                        data = bundle != null ? bundle.getString("data") : null;
                                        Log.d(TAG, "Прочитали id метки... id=" + data);
                                        sHandler.obtainMessage(result, data).sendToTarget();
                                        break;
                                    case RfidDialog.READER_COMMAND_READ_DATA:
                                        data = bundle != null ? bundle.getString("data") : null;
                                        Log.d(TAG, "Прочитали данные случайной метки..");
                                        sHandler.obtainMessage(result, data).sendToTarget();
                                        break;
                                    case RfidDialog.READER_COMMAND_READ_DATA_ID:
                                        data = bundle != null ? bundle.getString("data") : null;
                                        Log.d(TAG, "Прочитали данные конкретной метки...");
                                        sHandler.obtainMessage(result, data).sendToTarget();
                                        break;
                                    case RfidDialog.READER_COMMAND_WRITE_DATA:
                                        data = bundle != null ? bundle.getString("data") : null;
                                        Log.d(TAG, "Записали данные в случайную метку...");
                                        sHandler.obtainMessage(result, data).sendToTarget();
                                        break;
                                    case RfidDialog.READER_COMMAND_WRITE_DATA_ID:
                                        data = bundle != null ? bundle.getString("data") : null;
                                        Log.d(TAG, "Записали данные в конкретную метку...");
                                        sHandler.obtainMessage(result, data).sendToTarget();
                                        break;
                                    case RfidDialog.READER_COMMAND_READ_MULTI_ID:
                                        String[] list = bundle != null ? bundle.getStringArray("data") : null;
                                        Log.d(TAG, "Прочитали id меток...");
                                        sHandler.obtainMessage(result, list).sendToTarget();
                                        break;
                                    default:
                                        Log.d(TAG, "Не известная команда...");
                                        sHandler.obtainMessage(RESULT_RFID_CANCEL, null).sendToTarget();
                                        break;
                                }
                                break;
                            case DRIVER_STATE_DISCONNECT:
                                Log.d(TAG, "Соединение с сервером потеряно...");
                                sHandler.obtainMessage(RESULT_RFID_DISCONNECT).sendToTarget();
                                break;
                            default:
                                Log.d(TAG, "Неизвестный ответ сервера...");
                                sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
                                break;
                        }

                        return true;
                    }
                });

                // создаём поток работы с сервером
                mCommunicationThread = new CommunicationThread(socket, handler);
                mCommunicationThread.start();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void readTagId() {
        if (mCommunicationThread != null) {
            mCommunicationThread.write(new byte[]{(byte) 0xBB, 0x00, RfidDialog.READER_COMMAND_READ_ID, 0x00, 0x00, 0x7E});
        }
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        if (mCommunicationThread == null) {
            return;
        }

        // маркер начала, тип пакета, команда, размер полезной нагрузки, маркер конца
        int serviceDataLength = 1 + 1 + 1 + 2 + 1;

        // считаем размер полезной нагрузки
        // размер поля для указание количества передаваемых tagId
        int payloadLength = 2;
        for (String tagId : tagIds) {
            // размер поля для хранения длины tagId
            payloadLength += 2;
            // длина tagId
            payloadLength += tagId.length();
        }

        byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
        int commandBufferIndex = 0;

        // маркер начала пакета
        commandBuffer[commandBufferIndex++] = (byte) 0xBB;

        // тип пакета
        commandBuffer[commandBufferIndex++] = 0x00;

        // команда
        commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_READ_MULTI_ID;

        // размер полезной нагрузки
        commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
        commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

        // количество tagId
        commandBuffer[commandBufferIndex++] = (byte) ((tagIds.length >> 8) & 0xFF);
        commandBuffer[commandBufferIndex++] = (byte) (tagIds.length & 0xFF);

        // помещаем в буфер полезную нагрузку
        for (String tagId : tagIds) {
            // длина tagId
            commandBuffer[commandBufferIndex++] = (byte) ((tagId.length() >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (tagId.length() & 0xFF);
            byte[] tagIdBytes = tagId.getBytes();
            // tagId
            for (byte b : tagIdBytes) {
                commandBuffer[commandBufferIndex++] = b;
            }
        }

        // маркер конца
        commandBuffer[commandBufferIndex] = 0x7E;
        mCommunicationThread.write(commandBuffer);
    }

    @Override
    public void readTagData(String password, int memoryBank, int address, int count) {
        if (mCommunicationThread != null) {
            // маркер начала, тип пакета, команда, размер полезной нагрузки, маркер конца
            int serviceDataLength = 1 + 1 + 1 + 2 + 1;
            int payloadLength;
            byte[] passwordBuffer = password.getBytes();

            // 2 байта на длину пароля + длина пароля
            payloadLength = 2 + passwordBuffer.length;

            // банк памяти, смещение, количество данных
            payloadLength += 1 + 2 + 2;
            byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
            int commandBufferIndex = 0;

            // маркер начала пакета
            commandBuffer[commandBufferIndex++] = (byte) 0xBB;

            // тип пакета
            commandBuffer[commandBufferIndex++] = 0x00;

            // команда
            commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_READ_DATA;

            // размер полезной нагрузки
            commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

            // размер данных пароля к метке
            commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

            // пароль к метке
            for (byte b : passwordBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // банк памяти
            commandBuffer[commandBufferIndex++] = (byte) memoryBank;

            // смещение в банке памяти
            commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

            // количество данных
            commandBuffer[commandBufferIndex++] = (byte) ((count >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (count & 0xFF);

            // маркер конца
            commandBuffer[commandBufferIndex] = 0x7E;
            mCommunicationThread.write(commandBuffer);
        }
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank, int address, int count) {
        if (mCommunicationThread != null) {
            // маркер начала, тип пакета, команда, размер полезной нагрузки,
            // маркер конца
            int serviceDataLength = 1 + 1 + 1 + 2 + 1;
            int payloadLength;
            byte[] passwordBuffer = password.getBytes();
            byte[] tagIdBuffer = tagId.getBytes();

            // 2 байта на длину пароля + длина пароля
            payloadLength = 2 + passwordBuffer.length;

            // 2 байта на длину id метки + длина id метки
            payloadLength += 2 + tagIdBuffer.length;

            // банк памяти, смещение, количество данных
            payloadLength += 1 + 2 + 2;
            byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
            int commandBufferIndex = 0;

            // маркер начала пакета
            commandBuffer[commandBufferIndex++] = (byte) 0xBB;
            // тип пакета
            commandBuffer[commandBufferIndex++] = 0x00;
            // команда
            commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_READ_DATA_ID;

            // размер полезной нагрузки
            commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

            // размер данных пароля к метке
            commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

            // пароль к метке
            for (byte b : passwordBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // размер данных id метке
            commandBuffer[commandBufferIndex++] = (byte) ((tagIdBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (tagIdBuffer.length & 0xFF);

            // id метки
            for (byte b : tagIdBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // банк памяти
            commandBuffer[commandBufferIndex++] = (byte) memoryBank;

            // смещение в банке памяти
            commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

            // количество данных
            commandBuffer[commandBufferIndex++] = (byte) ((count >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (count & 0xFF);

            // маркер конца
            commandBuffer[commandBufferIndex] = 0x7E;
            mCommunicationThread.write(commandBuffer);
        }
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        if (mCommunicationThread != null) {
            // маркер начала, тип пакета, команда, размер полезной нагрузки,
            // маркер конца
            int serviceDataLength = 1 + 1 + 1 + 2 + 1;
            int payloadLength = 0;
            byte[] passwordBuffer = password.getBytes();
            byte[] dataBuffer = data.getBytes();

            // 2 байта на длину пароля + длина пароля
            payloadLength += 2 + passwordBuffer.length;

            // 2 байта на длину данных + длина данных
            payloadLength += 2 + dataBuffer.length;

            // банк памяти, смещение
            payloadLength += 1 + 2;
            byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
            int commandBufferIndex = 0;

            // маркер начала пакета
            commandBuffer[commandBufferIndex++] = (byte) 0xBB;

            // тип пакета
            commandBuffer[commandBufferIndex++] = 0x00;

            // команда
            commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_WRITE_DATA;

            // размер полезной нагрузки
            commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

            // размер данных пароля к метке
            commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

            // пароль к метке
            for (byte b : passwordBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // банк памяти
            commandBuffer[commandBufferIndex++] = (byte) memoryBank;

            // смещение в банке памяти
            commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

            // размер данных
            commandBuffer[commandBufferIndex++] = (byte) ((dataBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (dataBuffer.length & 0xFF);

            // данные
            for (int i = 0; i < passwordBuffer.length; i++) {
                commandBuffer[commandBufferIndex++] = dataBuffer[i];
            }

            // маркер конца
            commandBuffer[commandBufferIndex] = 0x7E;
            mCommunicationThread.write(commandBuffer);
        }
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank,
                             int address, String data) {
        if (mCommunicationThread != null) {
            // маркер начала, тип пакета, команда, размер полезной нагрузки,
            // маркер конца
            int serviceDataLength;
            serviceDataLength = 1 + 1 + 1 + 2 + 1;
            int payloadLength = 0;
            byte[] passwordBuffer = password.getBytes();
            byte[] tagIdBuffer = tagId.getBytes();
            byte[] dataBuffer = data.getBytes();

            // 2 байта на длину пароля + длина пароля
            payloadLength += 2 + passwordBuffer.length;

            // 2 байта на id метки + длина id метки
            payloadLength += 2 + tagIdBuffer.length;

            // 2 байта на длину данных + длина данных
            payloadLength += 2 + dataBuffer.length;

            // банк памяти, смещение
            payloadLength += 1 + 2;
            byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
            int commandBufferIndex = 0;

            // маркер начала пакета
            commandBuffer[commandBufferIndex++] = (byte) 0xBB;

            // тип пакета
            commandBuffer[commandBufferIndex++] = 0x00;

            // команда
            commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_WRITE_DATA_ID;

            // размер полезной нагрузки
            commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

            // размер данных пароля к метке
            commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

            // пароль к метке
            for (byte b : passwordBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // размер данных id метки
            commandBuffer[commandBufferIndex++] = (byte) ((tagIdBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (tagIdBuffer.length & 0xFF);

            // id метки
            for (byte b : tagIdBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // банк памяти
            commandBuffer[commandBufferIndex++] = (byte) memoryBank;

            // смещение в банке памяти
            commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

            // размер данных
            commandBuffer[commandBufferIndex++] = (byte) ((dataBuffer.length >> 8) & 0xFF);
            commandBuffer[commandBufferIndex++] = (byte) (dataBuffer.length & 0xFF);

            // данные
            for (byte b : passwordBuffer) {
                commandBuffer[commandBufferIndex++] = b;
            }

            // маркер конца
            commandBuffer[commandBufferIndex] = 0x7E;
            mCommunicationThread.write(commandBuffer);
        }
    }

    @Override
    public void close() {
        if (mCommunicationThread != null) {
            mCommunicationThread.cancel();
        }
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

        // создаём текстовое поле
        TextView textView = new TextView(mContext);
        textView.setText("Считайте метку внешним устройством...");
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(32);
//        return textView;
//        return null;
        return inflater.inflate(R.layout.rfid_read, viewGroup);
    }

    /**
     * <p>
     * Интерфейс настроек драйвера
     * </p>
     *
     * @return PreferenceScreen Если настроек нет должен вернуть null
     */
    public PreferenceScreen getSettingsScreen(PreferenceScreen screen) {

        // строим интерфейс с настройками драйвера блютус
        BluetoothAdapter adapter;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            ListPreference listPreference = new ListPreference(screen.getContext());
            listPreference.setKey(SERVER_MAC_PREF_KEY);
            listPreference.setTitle("Доступные устройства");
            List<String> names = new ArrayList<>();
            List<String> values = new ArrayList<>();

            Set<BluetoothDevice> deviceSet = adapter.getBondedDevices();
            for (BluetoothDevice device : deviceSet) {
                names.add(device.getName());
                values.add(device.getAddress());
            }

            listPreference.setEntries(names.toArray(new String[]{}));
            listPreference.setEntryValues(values.toArray(new String[]{}));
            screen.addPreference(listPreference);
        }

        return screen;
    }

    /**
     * @author Dmitriy Logachov
     */
    private class CommunicationThread extends Thread {

        private static final String TAG = "CommunicationThread";

        private BluetoothSocket mSocket;
        private InputStream mInputStream;
        private OutputStream mOutputStream;
        private Handler mHandler;
        private boolean stopDriver = false;

        CommunicationThread(BluetoothSocket socket, Handler handler) {

            mSocket = socket;
            mHandler = handler;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = socket.getInputStream();
                tmpOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            mInputStream = tmpInputStream;
            mOutputStream = tmpOutputStream;
        }

        @Override
        public void run() {
            Log.d(TAG, "run()");
            int count;
            int bufferLength = 1024;
            byte buffer[] = new byte[bufferLength];
            int dataLength = 1024;
            byte data[] = new byte[dataLength];
            int dataIndex = 0;
            int bufferIndex = 0;
            int parseIndex = 0;

            boolean packetStart = false;
            boolean typePacketExists = false;
            boolean commandExists = false;
            int command = 0;
            boolean payloadLengthExists = false;
            int payloadLength = 0;
            byte[] payloadLenBuff = new byte[2];
            int payloadLenBuffIndex = 0;

            while (true) {
                try {
                    Log.d(TAG, "Читаем данные с сервера...");
                    count = mInputStream.read(buffer, bufferIndex, bufferLength - bufferIndex);
                    // разбор данных поступающих с сервера
                    if (count > 0) {
                        Log.d(TAG, "прочитано байт = " + count);
                        bufferIndex += count;
                        while (parseIndex < bufferIndex) {
                            if (packetStart) {
                                if (typePacketExists) {
                                    if (commandExists) {
                                        if (payloadLengthExists) {
                                            byte tmpData = buffer[parseIndex++];
                                            if (tmpData == (byte) 0x7E && dataIndex == payloadLength) {
                                                // добрались до конца пакета

                                                Bundle bundle = parseCommand(command, data);
                                                mHandler.obtainMessage(DRIVER_STATE_READ_ANSWER, command, -1, bundle).sendToTarget();

                                                // сбрасываем всё
                                                packetStart = false;
                                                typePacketExists = false;
                                                payloadLengthExists = false;
                                                payloadLength = 0;
                                                payloadLenBuffIndex = 0;
                                                commandExists = false;
                                                dataIndex = 0;
                                            } else {
                                                if (dataIndex >= payloadLength) {
                                                    // не нашли маркера конца пакета после полезной нагрузки сбрасываем всё
                                                    packetStart = false;
                                                    typePacketExists = false;
                                                    payloadLengthExists = false;
                                                    payloadLength = 0;
                                                    payloadLenBuffIndex = 0;
                                                    commandExists = false;
                                                    dataIndex = 0;
                                                } else {
                                                    data[dataIndex++] = tmpData;
                                                }
                                            }
                                        } else {
                                            payloadLenBuff[payloadLenBuffIndex++] = buffer[parseIndex++];
                                            if (payloadLenBuffIndex >= 2) {
                                                payloadLength = ((0xFF & payloadLenBuff[0]) << 8) + (0xFF & payloadLenBuff[1]);
                                                payloadLengthExists = true;
                                            }
                                        }
                                    } else {
                                        command = buffer[parseIndex++] & 0xFF;
                                        int[] commands = new int[]{
                                                RfidDialog.READER_COMMAND_READ_ID,
                                                RfidDialog.READER_COMMAND_READ_DATA,
                                                RfidDialog.READER_COMMAND_READ_DATA_ID,
                                                RfidDialog.READER_COMMAND_WRITE_DATA,
                                                RfidDialog.READER_COMMAND_WRITE_DATA_ID,
                                                RfidDialog.READER_COMMAND_READ_MULTI_ID};
                                        if (Arrays.binarySearch(commands, command) > -1) {
                                            commandExists = true;
                                        } else {
                                            command = 0;
                                            packetStart = false;
                                            typePacketExists = false;
                                        }
                                    }
                                } else {
                                    if (buffer[parseIndex++] == (byte) 0x01) {
                                        typePacketExists = true;
                                    } else {
                                        packetStart = false;
                                    }
                                }
                            } else {
                                if (buffer[parseIndex++] == (byte) 0xBB) {
                                    packetStart = true;
                                }
                            }
                        }

                        if (bufferIndex >= bufferLength) {
                            bufferIndex = 0;
                            parseIndex = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!stopDriver) {
                        // если драйвер останавливается не штатно, шлём сообщение
                        mHandler.obtainMessage(DRIVER_STATE_DISCONNECT, null).sendToTarget();
                    }
                    break;
                }
            }

        }

        /**
         * Разбор ответа на отправленную команду.
         *
         * @param command Команда на которую получен ответ.
         * @param data    Данные ответа.
         * @return @Bundle
         */
        private Bundle parseCommand(int command, byte[] data) {
            switch (command) {
                case RfidDialog.READER_COMMAND_READ_ID:
                    return parseCommandReadTagId(data);
                case RfidDialog.READER_COMMAND_READ_DATA:
                case RfidDialog.READER_COMMAND_READ_DATA_ID:
                    return parseCommandReadData(data);
                case RfidDialog.READER_COMMAND_WRITE_DATA:
                case RfidDialog.READER_COMMAND_WRITE_DATA_ID:
                    return parseCommandWriteData(data);
                case RfidDialog.READER_COMMAND_READ_MULTI_ID:
                    return parseCommandReadTagIdMulti(data);
                default:
                    return null;
            }
        }

        /**
         * Разбор ответа на команду считывания tagId первой попавшейся метки.
         *
         * @param data Полученные данные.
         * @return @Bundle
         */
        private Bundle parseCommandReadTagId(byte[] data) {
            Bundle bundle = new Bundle();
            int index = 0;

            // результат выполнения команды
            int result = reader.byteToInt(data, index, 1);
            index += 1;
            bundle.putInt("result", result);

            // длина id метки
            int tagIdLength = reader.byteToInt(data, index, 2);
            index += 2;

            // id метки
            byte[] tagIdBuffer = new byte[tagIdLength];
            for (int i = 0; i < tagIdLength; i++) {
                tagIdBuffer[i] = data[index++];
            }

            String tagId = new String(tagIdBuffer);
            bundle.putString("data", tagId);
            return bundle;
        }

        /**
         * Разбор ответа на команду считывания множества tagId.
         *
         * @param data Полученные данные.
         * @return @Bundle  В поле data возвращаем массив строк содержащих считанные tagId.
         */
        private Bundle parseCommandReadTagIdMulti(byte[] data) {
            Bundle bundle = new Bundle();
            int index = 0;

            // результат выполнения команды
            int result = reader.byteToInt(data, index, 1);
            index += 1;
            bundle.putInt("result", result);

            // количество считанных tagId
            int tagIdCount = reader.byteToInt(data, index, 2);
            index += 2;

            String[] tagIds = new String[tagIdCount];
            // разбираем данные tagIds
            for (int i = 0; i < tagIdCount; i++) {
                // длина id метки
                int tagIdLength = reader.byteToInt(data, index, 2);
                index += 2;

                // id метки
                byte[] tagIdBuffer = new byte[tagIdLength];
                for (int j = 0; j < tagIdLength; j++) {
                    tagIdBuffer[j] = data[index++];
                }

                tagIds[i] = new String(tagIdBuffer);
            }

            bundle.putStringArray("data", tagIds);
            return bundle;
        }

        /**
         * Разбор ответа на команду чтения содержимого метки.
         *
         * @param data Полученные данные.
         * @return @Bundle
         */
        private Bundle parseCommandReadData(byte[] data) {
            Bundle bundle = new Bundle();
            int index = 0;

            // результат выполнения команды
            int result = reader.byteToInt(data, index, 1);
            index += 1;
            bundle.putInt("result", result);

            // данные
            int dataLength = data.length - 1;
            byte[] dataBuffer = new byte[dataLength];
            for (int i = 0; i < dataLength; i++) {
                dataBuffer[i] = data[index++];
            }

            String tagId = new String(dataBuffer);
            bundle.putString("data", tagId);
            return bundle;
        }

        /**
         * Разбор ответа на команду записи данных в метку.
         *
         * @param data Полученные данные.
         * @return @Bundle
         */
        private Bundle parseCommandWriteData(byte[] data) {
            Bundle bundle = new Bundle();
            int index = 0;

            // результат выполнения команды
            int result = reader.byteToInt(data, index, 1);
//			index += 1;
            bundle.putInt("result", result);
            bundle.putString("data", null);
            return bundle;
        }

        public void cancel() {
            stopDriver = true;
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        public void write(byte[] command) {

            try {
                mOutputStream.write(command);
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }
}
