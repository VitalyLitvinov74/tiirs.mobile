/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;

import com.google.zxing.integration.android.IntentIntegrator;
import android.app.DialogFragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 * 
 */
public class RfidDriverBarcode extends RfidDriverBase implements IRfidDriver {

	private IntentIntegrator integrator;

	public RfidDriverBarcode(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	@Override
	public boolean init() {

		return true;
	}

	@Override
	public void readTagId() {

		integrator.initiateScan();
	}

	@Override
	public void close() {

	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		integrator = new IntentIntegrator(mDialogFragment);

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
			String data) {

	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

	}

}
