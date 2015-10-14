/**
 * 
 */
package ru.toir.mobile.rfid.driver;

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
	public boolean init(byte type) {
		return true;
	}

	@Override
	public void readTagId(byte type) {
		integrator.initiateScan();
	}

	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		integrator = new IntentIntegrator(mDialogFragment);

		return null;
	}

}
