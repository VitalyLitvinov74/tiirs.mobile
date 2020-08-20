package ru.toir.mobile.multi.rfid.driver;

import android.hardware.barcode.Scanner;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.rfid.IRfidDriver;
import ru.toir.mobile.multi.rfid.RfidDialog;
import ru.toir.mobile.multi.rfid.RfidDriverBase;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;
import ru.toir.mobile.multi.rfid.Tag;

/**
 * @author olejek
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RfidDriverBarcode2D extends RfidDriverBase implements IRfidDriver {
    public static final String DRIVER_NAME = "Драйвер лазерного считывателя штрихкодов";
    private final static String TAG = "RfidDriverBarcode2D";
    // view в котором будет текстовое поле,
    // в которое будет помещен распознанный код
    private static View driverView;
    private Handler c5Handler;
    private int command;

    @Override
    public boolean init() {
        // initialize the scanner
        Scanner.InitSCA();
        c5Handler = new MainHandler();
        Scanner.m_handler = c5Handler;
        return true;
    }

    @Override
    public void readTagId() {
        command = RfidDialog.READER_COMMAND_READ_ID;
        Scanner.ReadHW();
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        command = RfidDialog.READER_COMMAND_READ_MULTI_ID;
        Scanner.ReadHW();
    }

    @Override
    public void close() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        driverView = inflater.inflate(R.layout.bar2d_read, viewGroup);

        // инициализируем текстовое поле ввода в которое по нажатии железной
        // кнопки будет введено считанное значение
        EditText ed = driverView.findViewById(R.id.catch2bbarcode);
        if (ed != null) {
            ed.requestFocus();
            ed.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String tmpText = s.toString();
                    if (tmpText.equals("")) {
                        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    }

                    String tagId = "0000" + Tag.Type.TAG_TYPE_GRAPHIC_CODE + ":" + s.toString();
                    RfidDriverMsg msg = RfidDriverMsg.tagMsg(tagId);
                    switch (command) {
                        case RfidDialog.READER_COMMAND_READ_ID:
                            sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg).sendToTarget();
                            break;
                        case RfidDialog.READER_COMMAND_READ_MULTI_ID:
                            sHandler.obtainMessage(RESULT_RFID_SUCCESS, new RfidDriverMsg[]{msg})
                                    .sendToTarget();
                            break;
                        default:
                            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                            break;
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            Button button = driverView.findViewById(R.id.cancelBar2DScan);
            button.setOnClickListener(v -> sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget());
        }

        return driverView;
    }

    @Override
    public void readTagData(String password, int memoryBank, int address, int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank, int address, int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank, int address, String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    private static class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Scanner.BARCODE_READ: {
                    String tagId;
                    if (!msg.obj.equals("")) {
                        tagId = (String) msg.obj;
                    } else {
                        if (driverView != null) {
                            EditText ed = driverView.findViewById(R.id.catch2bbarcode);
                            if (ed != null) {
                                tagId = ed.getText().toString();
                            } else {
                                tagId = "";
                            }
                        } else {
                            tagId = "";
                        }
                    }

                    Log.d(TAG, tagId);
                    if (!tagId.equals("")) {
                        tagId = "0000" + Tag.Type.TAG_TYPE_GRAPHIC_CODE + ":" + tagId;
                        RfidDriverMsg rfidDriverMsg = RfidDriverMsg.tagMsg(tagId);
                        sHandler.obtainMessage(RESULT_RFID_SUCCESS, rfidDriverMsg).sendToTarget();
                    } else {
                        sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
                    }

                    break;
                }
                case Scanner.BARCODE_NOREAD: {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    break;
                }
                default:
                    break;
            }
        }
    }
}
