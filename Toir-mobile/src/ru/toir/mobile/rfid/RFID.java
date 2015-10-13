/**
 * 
 */
package ru.toir.mobile.rfid;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.toir.mobile.rfid.driver.RFIDDriver;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Класс работы с RFID
 *         </p>
 */
public class RFID {

	public static final int RESULT_RFID_SUCCESS = 0;
	public static final int RESULT_RFID_READ_ERROR = 1;
	public static final int RESULT_RFID_INIT_ERROR = 2;
	public static final int RESULT_RFID_CLASS_NOT_FOUND = 3;
	public static final int RESULT_RFID_WRITE_ERROR = 4;
	public static final int RESULT_RFID_CANCEL = 5;
	
	public static final String RESULT_RFID_TAG_ID = "tagId";
	
	RFIDDriver mDriver;

	/**
	 * Передаём в драйвер текущую активити
	 * 
	 * @param activity
	 */
	public void setActivity(Activity activity) {
		mDriver.setActivity(activity);
	}

	/**
	 * @param driver
	 * @return {@link RFID}
	 */
	public RFID(RFIDDriver driver) {
		this.mDriver = driver;
	}

	/**
	 * <p>
	 * Инициализация драйвера
	 * </p>
	 * 
	 * @return boolean
	 */
	public boolean init(byte type) {
		return mDriver.init(type);
	}

	/**
	 * <p>
	 * Считывание метки
	 * </p>
	 * 
	 * @return byte[]
	 */
	public void read(byte type) {
		mDriver.read(type);
	}

	/**
	 * <p>
	 * Запись в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return boolean
	 */
	public boolean write(byte[] outBuffer) {
		return mDriver.write(outBuffer);
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	public boolean SetOperationType(byte type) {
		return mDriver.SetOperationType(type);
	}

	/**
	 * <p>
	 * Завершаем работу
	 * </p>
	 */
	public void close() {
		mDriver.close();
	}

	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
		return mDriver.getView(inflater, viewGroup);
	}

	public void setHandler(Handler handler) {
		mDriver.setHandler(handler);
	}
}
