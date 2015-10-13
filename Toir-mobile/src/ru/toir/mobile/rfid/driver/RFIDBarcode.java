/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import com.google.zxing.integration.android.IntentIntegrator;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 * 
 */
public class RFIDBarcode implements RFIDDriver {

	private Activity mActivity;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.toir.mobile.rfid.driver.RFIDDriver#setActivity(android.app.Activity)
	 */
	@Override
	public void setActivity(Activity activity) {
		this.mActivity = activity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#init(byte)
	 */
	@Override
	public boolean init(byte type) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#read()
	 */
	@Override
	public void read(byte type) {
		IntentIntegrator integrator = new IntentIntegrator(mActivity);
		integrator.initiateScan();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#write(byte[])
	 */
	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.toir.mobile.rfid.driver.RFIDDriver#getView(android.view.LayoutInflater
	 * , android.view.ViewGroup)
	 */
	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setHandler(android.os.Handler)
	 */
	@Override
	public void setHandler(Handler handler) {

	}

}
