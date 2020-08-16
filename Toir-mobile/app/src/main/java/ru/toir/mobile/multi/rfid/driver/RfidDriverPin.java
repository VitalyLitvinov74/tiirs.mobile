package ru.toir.mobile.multi.rfid.driver;

import android.preference.PreferenceScreen;
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


/**
 * @author Dmitriy Logachev
 *         <p>
 *         Драйвер считывателя RFID который "считывает" логин и PIN код.
 *         </p>
 */
@SuppressWarnings("unused")
public class RfidDriverPin extends RfidDriverBase implements IRfidDriver {
    @SuppressWarnings("unused")
    public static final String DRIVER_NAME = "PIN код";
    private String TAG = "RfidDriverPin";
    private int command;

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void readTagId() {
        // В данном драйвере реального считывания не происходит.
        command = RfidDialog.READER_COMMAND_READ_ID;
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        // В данном драйвере реального считывания не происходит.
        command = RfidDialog.READER_COMMAND_READ_MULTI_ID;
    }

    @Override
    public void readTagData(String password, int memoryBank, int address, int count) {
        // В данном режиме реального считывания не происходит.
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank, int address, int count) {
        // В данном режиме реального считывания не происходит.
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        // В данном режиме реальной записи не происходит.
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank, int address, String data) {
        // В данном режиме реальной записи не происходит.
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    @Override
    public void close() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

        View view = inflater.inflate(R.layout.rfid_dialog_pin, viewGroup);

        Button ok = view.findViewById(R.id.rfid_dialog_text_button_OK);
        ok.setOnClickListener(v -> {
            Log.d(TAG, "pressed OK");
            EditText login = v.getRootView().findViewById(R.id.rfid_dialog_pin_login_input);
            EditText password = v.getRootView().findViewById(R.id.rfid_dialog_pin_pin_input);
            RfidDriverMsg msg = RfidDriverMsg.loginMsg(login.getText().toString(),
                    password.getText().toString());
            switch (command) {
                case RfidDialog.READER_COMMAND_READ_ID:
                    sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg).sendToTarget();
                    break;
                default:
                    sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
            }
        });

        Button cancel = view.findViewById(R.id.rfid_dialog_text_button_CANCEL);
        cancel.setOnClickListener(v -> {
            Log.d(TAG, "pressed CANCEL");
            sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
        });

        return view;
    }

    /**
     * <p>
     * Интерфейс настроек драйвера
     * </p>
     *
     * @return PreferenceScreen|null Если настроек нет должен вернуть null
     */
    @Override
    public PreferenceScreen getSettingsScreen(PreferenceScreen screen) {
        return null;
    }
}
