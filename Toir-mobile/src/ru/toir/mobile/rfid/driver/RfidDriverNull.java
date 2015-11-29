package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который ни чего не делает.
 *         </p>
 */
public class RfidDriverNull extends RfidDriverBase implements IRfidDriver {

	public RfidDriverNull(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
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

}
