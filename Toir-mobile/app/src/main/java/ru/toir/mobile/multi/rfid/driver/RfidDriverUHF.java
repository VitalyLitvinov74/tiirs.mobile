package ru.toir.mobile.multi.rfid.driver;

import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.toir.mobile.multi.rfid.RfidDriverBase;

/**
 * @author Dmitriy Logachev
 *         <p>
 *         Драйвер считывателя RFID который ни чего не делает а выступает заглушкой для выбора
 *         реального драйвера UHF.
 *         </p>
 */
public class RfidDriverUHF extends RfidDriverBase {
    public static final String DRIVER_NAME = "UHF драйвер";

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void readTagId() {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void readMultiplyTagId(final String[] tagIds) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void close() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void readTagData(String password, int memoryBank, int address,
                            int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void readTagData(String password, String tagId, int memoryBank,
                            int address, int count) {
        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, int memoryBank, int address,
                             String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
    }

    @Override
    public void writeTagData(String password, String tagId, int memoryBank,
                             int address, String data) {
        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
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
        // TODO: возможно сюда добавить выбор реального драйвера UHF
        return null;
    }
}
