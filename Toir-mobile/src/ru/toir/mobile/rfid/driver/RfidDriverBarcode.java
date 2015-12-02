/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;

import com.google.zxing.integration.android.IntentIntegrator;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 * 
 */
public class RfidDriverBarcode extends RfidDriverBase implements IRfidDriver {

	public static final String DRIVER_NAME = "Драйвер штрихкодов Barcode";
	private IntentIntegrator integrator;
	private DialogFragment dialogFragment;
	private Activity activity;

	public RfidDriverBarcode(Handler handler) {
		super(handler);
	}

	public RfidDriverBarcode(Handler handler, DialogFragment dialog) {
		super(handler);
		dialogFragment = dialog;
	}

	public RfidDriverBarcode(Activity activity, Handler handler) {
		super(handler);
		this.activity = activity;
	}

	@Override
	public boolean init() {
		if (activity == null || dialogFragment == null) {
			return false;
		} else {
			return true;
		}
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

		if (activity != null) {
			integrator = new IntentIntegrator(dialogFragment);
		} else if (dialogFragment != null) {
			integrator = new IntentIntegrator(activity);
		}

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
