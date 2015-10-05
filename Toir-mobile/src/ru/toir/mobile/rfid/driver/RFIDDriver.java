package ru.toir.mobile.rfid.driver;

import android.app.Activity;
import android.view.Menu;

/**
 * @author koputo
 * <p>Интерфейс драйвера считывателя RFID</p>
 */
public interface RFIDDriver {
	static byte types=0;	
	
	public void setActivity(Activity activity);
	
	/**
	 * <p>Инициализация драйвера</p>
	 * @return
	 */
	public boolean init(byte type);

	/**
	 * <p>Считывание метки</p>
	 * @return
	 */
	public void read(byte type);

	/**
	 * <p>Запись в метку</p>
	 * @param outBuffer
	 * @return
	 */
	public boolean write(byte[] outBuffer);

	/**
	 * <p>Устанавливаем тип операции</p>
	 * @return boolean
	 */
	public boolean SetOperationType(byte type);

	/**
	 * <p>Завершение работы драйвера</p>
	 */
	public void close();
	
	/**
	 * <p>Меню которое предоставляет драйвер</p>
	 */
	public void getMenu(Menu menu);	
}
