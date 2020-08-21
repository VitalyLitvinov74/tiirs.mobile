package ru.toir.mobile.multi.rfid.driver;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.User;
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(inflater.getContext());
        String defaultLogin = sp.getString("defaultLogin", "");
        ArrayList<String> adapterData = new ArrayList<>();
        adapterData.add("Новый пользователь");
        HashMap<String, String> loginList = User.getLoginList(inflater.getContext());
        int idx = 1;
        int defaultIdx = 0;
        for (String login : loginList.keySet()) {
            adapterData.add(loginList.get(login) + " (" + login + ")");
            if (login.equals(defaultLogin)) {
                defaultIdx = idx;
            }

            idx++;
        }

        Spinner loginSpinner = view.findViewById(R.id.rfid_dialog_pin_login_spinner);
        SpinnerAdapter spinnerAdapter =
                new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_spinner_dropdown_item, adapterData);
        loginSpinner.setAdapter(spinnerAdapter);
        loginSpinner.setSelection(defaultIdx);
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EditText et = adapterView.getRootView().findViewById(R.id.rfid_dialog_pin_login_input);
                String selected = (String) adapterView.getItemAtPosition(i);
                Log.d(TAG, selected);
                Log.d(TAG, "idx: " + l);

                if (l == 0) {
                    et.setText("");
                    et.setVisibility(View.VISIBLE);
                } else {
                    Pattern pattern = Pattern.compile("\\((.*)\\)");
                    Matcher matcher = pattern.matcher(selected);
                    if (matcher.find()) {
                        et.setText(matcher.group(1));
                        et.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected");
            }
        };

        loginSpinner.setOnItemSelectedListener(listener);

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
