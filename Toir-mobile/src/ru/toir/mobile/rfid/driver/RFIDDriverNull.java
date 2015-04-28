package ru.toir.mobile.rfid.driver;

import android.view.Menu;
import android.view.MenuItem;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который ни чего не делает.</p>
 */
public class RFIDDriverNull implements RFIDDriver{

	/**
	 * <p>Инициализируем драйвер</p>
	 * @return
	 */
	@Override
	public boolean init(TOIRCallback callback){
		return false;
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
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#getMenu()
	 */
	@Override
	public void getMenu(Menu menu) {
		// TODO Auto-generated method stub
	}
}
