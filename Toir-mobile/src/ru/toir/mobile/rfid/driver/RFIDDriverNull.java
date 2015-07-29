package ru.toir.mobile.rfid.driver;

import android.app.Activity;
import android.view.Menu;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который ни чего не делает.</p>
 */
public class RFIDDriverNull implements RFIDDriver{
	
	@Override
	public boolean init() {
		return true;
	}
	
	/**
	 * <p>Считываем метку</p>
	 * @return
	 */
	@Override
	public void read() {
	}
	
	/**
	 * <p>Записываем в метку</p>
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer){
		return false;
	}

	/**
	 * <p>Завершаем работу драйвера</p>
	 */
	@Override
	public void close() {
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#getMenu()
	 */
	@Override
	public void getMenu(Menu menu) {
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setActivity(android.app.Activity)
	 */
	@Override
	public void setActivity(Activity activity) {
	}
}
