/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import com.google.zxing.integration.android.IntentIntegrator;

import android.app.Activity;
import android.view.Menu;

/**
 * @author koputo
 *
 */
public class RFIDBarcode implements RFIDDriver {
	
	Activity mActivity;
	static byte types=0;	

	@Override
	public void setActivity(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public boolean init(byte type) {
		return true;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#read()
	 */
	@Override
	public void read(byte type) {
		IntentIntegrator integrator = new IntentIntegrator(mActivity);
		integrator.initiateScan();		
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#write(byte[])
	 */
	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#close()
	 */
	@Override
	public void close() {
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#getMenu(android.view.Menu)
	 */
	@Override
	public void getMenu(Menu menu) {
	}

	/**
	 * <p>Устанавливаем тип операции</p>
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {
		types=type;
		return true;
	}

}
