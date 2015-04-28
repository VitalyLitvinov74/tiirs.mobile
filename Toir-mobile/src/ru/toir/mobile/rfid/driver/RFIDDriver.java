package ru.toir.mobile.rfid.driver;

import android.view.Menu;

/**
 * @author koputo
 * <p>Интерфейс драйвера считывателя RFID</p>
 */
public interface RFIDDriver {
	
	/**
	 * <p>Инициализация драйвера</p>
	 * @return
	 */
	public boolean init(TOIRCallback callback);
	
	/**
	 * <p>Считывание метки</p>
	 * @return
	 */
	public void read();
	
	/**
	 * <p>Запись в метку</p>
	 * @param outBuffer
	 * @return
	 */
	public boolean write(byte[] outBuffer);
	
	/**
	 * <p>Завершение работы драйвера</p>
	 */
	public void close();
	
	/**
	 * <p>Меню которое предоставляет драйвер</p>
	 */
	public void getMenu(Menu menu);

}
