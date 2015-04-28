/**
 * 
 */
package ru.toir.mobile.rfid;

import android.view.Menu;
import ru.toir.mobile.rfid.driver.RFIDDriver;
import ru.toir.mobile.rfid.driver.TOIRCallback;

/**
 * @author koputo
 * <p>Класс работы с RFID</p>
 */
public class RFID {
	public static final int RESULT_RFID_READ_ERROR = android.app.Activity.RESULT_FIRST_USER;
	public static final int RESULT_RFID_INIT_ERROR = android.app.Activity.RESULT_FIRST_USER + 1;
	public static final int RESULT_RFID_CLASS_NOT_FOUND = android.app.Activity.RESULT_FIRST_USER + 2;
	RFIDDriver driver;
	
	/**
	 * @param driver
	 * @return {@link RFID}
	 */
	public RFID(RFIDDriver driver){
		this.driver = driver;
	}
	/**
	 * <p>Инициализация драйвера</p>
	 * @return boolean
	 */
	public boolean init(TOIRCallback callback){
		return driver.init(callback);
	}
	
	/**
	 * <p>Считывание метки</p>
	 * @return byte[]
	 */
	public void read(){
		driver.read();
	}
	
	/**
	 * <p>Запись в метку</p>
	 * @param outBuffer
	 * @return boolean
	 */
	public boolean write(byte[] outBuffer){
		return driver.write(outBuffer);
	}
	
	/**
	 * <p>Завершаем работу</p> 
	 */
	public void close(){
		driver.close();
	}
	
	/**
	 * <p>Меню которое предоставляет драйвер</p>
	 */
	public void getMenu(Menu menu) {
		driver.getMenu(menu);
	}

}
