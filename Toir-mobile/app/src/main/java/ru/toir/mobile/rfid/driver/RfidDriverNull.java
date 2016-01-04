package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.RfidDriverBase;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который ни чего не делает.
 *         </p>
 */
public class RfidDriverNull extends RfidDriverBase {
	public static final String DRIVER_NAME = "Null драйвер";

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public void readTagId() {
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
		CheckBoxPreference checkBoxPreference = new CheckBoxPreference(
				screen.getContext());
		checkBoxPreference.setTitle("Тестовый чек бокс");
		checkBoxPreference.setKey("rfidDrvNullTestCheckBox");
		screen.addPreference(checkBoxPreference);
		return screen;
	}
}
