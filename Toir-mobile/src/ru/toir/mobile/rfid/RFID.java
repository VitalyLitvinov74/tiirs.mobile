/**
 * 
 */
package ru.toir.mobile.rfid;

import android.app.Activity;
import android.view.Menu;
import ru.toir.mobile.rfid.driver.RFIDDriver;

/**
 * @author koputo
 * <p>Класс работы с RFID</p>
 */
public class RFID {
	public static final int RESULT_RFID_READ_ERROR = android.app.Activity.RESULT_FIRST_USER;
	public static final int RESULT_RFID_INIT_ERROR = android.app.Activity.RESULT_FIRST_USER + 1;
	public static final int RESULT_RFID_CLASS_NOT_FOUND = android.app.Activity.RESULT_FIRST_USER + 2;
	RFIDDriver mDriver;
	
	public void setActivity(Activity activity) {
		mDriver.setActivity(activity);
	}
	
	/**
	 * @param driver
	 * @return {@link RFID}
	 */
	public RFID(RFIDDriver driver){
		this.mDriver = driver;
	}
	
	/**
	 * <p>Инициализация драйвера</p>
	 * @return boolean
	 */
	public boolean init(byte type){
		return mDriver.init(type);
	}

	/**
	 * <p>Считывание метки</p>
	 * @return byte[]
	 */
	public void read(byte type){
		mDriver.read(type);
	}
	
	/**
	 * <p>Запись в метку</p>
	 * @param outBuffer
	 * @return boolean
	 */
	public boolean write(byte[] outBuffer){
		return mDriver.write(outBuffer);
	}
	
	/**
	 * <p>Завершаем работу</p> 
	 */
	public void close(){
		mDriver.close();
	}
	
	/**
	 * <p>Меню которое предоставляет драйвер</p>
	 */
	public void getMenu(Menu menu) {
		mDriver.getMenu(menu);
	}

}
