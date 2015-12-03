package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Handler;
import android.os.Message;
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

	public RfidDriverNull(Handler handler) {
		super(handler);
	}

	public RfidDriverNull(Handler handler, Activity activity) {
		super(handler);
	}

	public RfidDriverNull(Handler handler, DialogFragment dialogFragment) {
		super(handler);
	}

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public void readTagId() {

		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
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

		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {

		Message message = new Message();
		message.what = RESULT_RFID_WRITE_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

		Message message = new Message();
		message.what = RESULT_RFID_WRITE_ERROR;
		mHandler.sendMessage(message);
	}

	/**
	 * <p>
	 * Интерфейс настроек драйвера
	 * </p>
	 * 
	 * @return PreferenceScreen Если настроек нет должен вернуть null
	 */
	public static PreferenceScreen getSettingsScreen(PreferenceScreen screen) {

		CheckBoxPreference checkBoxPreference = new CheckBoxPreference(
				screen.getContext());
		checkBoxPreference.setTitle("Тестовый чек бокс");
		checkBoxPreference.setKey("rfidDrvNullTestCheckBox");
		screen.addPreference(checkBoxPreference);

		return screen;
	}

}
