/**
 * 
 */
package ru.toir.mobile.rfid;

import ru.toir.mobile.rfid.driver.RFIDDriver;

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
	public boolean init(){
		return driver.init();
	}
	
	/**
	 * <p>Считывание метки</p>
	 * @return byte[]
	 */
	public String read(){
		return driver.read();
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

}
