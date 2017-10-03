package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.DataUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;


/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
@SuppressWarnings("unused")
public class RfidDriverText extends RfidDriverBase implements IRfidDriver {
    @SuppressWarnings("unused")
    public static final String DRIVER_NAME = "Текстовый драйвер";
    private static final String TEXTDRV_MODE_PREF_KEY = "textDrvMode";
    private static boolean mMode; // false - простой режим, true - расшириный режим
    private String TAG = "RfidDriverText";
    private int command;

    @Override
    public boolean init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMode = preferences.getBoolean(TEXTDRV_MODE_PREF_KEY, false);
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
        if (mMode) {
            // TODO: реализовать чтение содержимого метки из файла
            sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
        } else {
            // В данном режиме реального считывания не происходит.
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
        }
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank, int address, int count) {
        if (mMode) {
            byte[] rawData = new byte[64];
            File tagFile = new File(mContext.getFilesDir() + "/" + tagId);
            if (!tagFile.exists()) {
                OutputStream outputStream;
                try {
                    outputStream = mContext.openFileOutput(tagId, Context.MODE_PRIVATE);
                    outputStream.write(rawData);
                    outputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }
            }

            String hexData;
            FileInputStream in;
            try {
                in = mContext.openFileInput(tagId);
                int rc = in.read(rawData, address, count);
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            }

            byte[] tmpData = Arrays.copyOf(rawData, count);
            hexData = DataUtils.toHexString(tmpData);
            sHandler.obtainMessage(RESULT_RFID_SUCCESS, hexData).sendToTarget();
        } else {
            // В данном режиме реального считывания не происходит.
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
        }
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address, String data) {
        if (mMode) {
            // TODO: реализовать запись содержимого метки в файл
            sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
        } else {
            // В данном режиме реальной записи не происходит.
            sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
        }
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank, int address, String data) {
        if (mMode) {
            FileOutputStream out;
            try {
                out = mContext.openFileOutput(tagId, Context.MODE_PRIVATE);
                byte[] rawData = DataUtils.hexStringTobyte(data);
                if (rawData == null) {
                    rawData = new byte[64];
                }

                out.write(rawData, address, rawData.length);
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
        } else {
            // В данном режиме реальной записи не происходит.
            sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
        }
    }

    @Override
    public void close() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

        View view = inflater.inflate(R.layout.rfid_dialog_text, viewGroup);

        Button ok = (Button) view.findViewById(R.id.rfid_dialog_text_button_OK);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "pressed OK");
                Spinner spinner = (Spinner) v.getRootView().findViewById(R.id.rfid_dialog_text_spinner_lables);
                switch (command) {
                    case RfidDialog.READER_COMMAND_READ_ID:
                        sHandler.obtainMessage(RESULT_RFID_SUCCESS, "0000" + spinner.getSelectedItem()).sendToTarget();
                        break;
                    case RfidDialog.READER_COMMAND_READ_MULTI_ID:
                        sHandler.obtainMessage(RESULT_RFID_SUCCESS, new String[]{"0000" + spinner.getSelectedItem()}).sendToTarget();
                        break;
                    default:
                        sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
                }
            }
        });

        Button cancel = (Button) view.findViewById(R.id.rfid_dialog_text_button_CANCEL);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "pressed CANCEL");
                sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.rfid_dialog_text_spinner_lables);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
                inflater.getContext(), R.array.list,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(Adapter.NO_SELECTION, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    /**
     * <p>
     * Интерфейс настроек драйвера
     * </p>
     *
     * @return PreferenceScreen Если настроек нет должен вернуть null
     */
    @Override
    public PreferenceScreen getSettingsScreen(PreferenceScreen screen) {
        // строим интерфейс с настройками драйвера
        CheckBoxPreference modePreference = new CheckBoxPreference(screen.getContext());
        modePreference.setKey(TEXTDRV_MODE_PREF_KEY);
        modePreference.setTitle("Чтение / запись");
        screen.addPreference(modePreference);
        return screen;
    }
}
