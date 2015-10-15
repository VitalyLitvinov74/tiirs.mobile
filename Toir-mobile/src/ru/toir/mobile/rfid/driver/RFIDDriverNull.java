package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который ни чего не делает.
 *         </p>
 */
public class RFIDDriverNull extends RfidDriverBase implements IRfidDriver {

	public RFIDDriverNull(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public void readTagId() {

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

	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			byte[] data) {

	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, byte[] data) {

	}

}
