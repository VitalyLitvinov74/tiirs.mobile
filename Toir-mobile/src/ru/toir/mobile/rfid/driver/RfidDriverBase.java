/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import android.app.DialogFragment;
import android.os.Handler;

/**
 * @author Dmitriy Logachov
 * 
 */
public abstract class RfidDriverBase implements IRfidDriver {

	public static final int RESULT_RFID_SUCCESS = 0;
	public static final int RESULT_RFID_READ_ERROR = 1;
	public static final int RESULT_RFID_INIT_ERROR = 2;
	public static final int RESULT_RFID_CLASS_NOT_FOUND = 3;
	public static final int RESULT_RFID_WRITE_ERROR = 4;
	public static final int RESULT_RFID_CANCEL = 5;

	public static final String RESULT_RFID_TAG_ID = "tagId";

	// диалог в котором драйвер реализует интерфейс пользователя
	protected DialogFragment mDialogFragment;

	// Handler который будет обрабатывать сообщение от драйвера
	// TODO нужно избавиться от static
	protected static Handler mHandler;

	// тип команды, костыль, нужно пересмотреть код
	// TODO нужно удалить
	static byte types = 0;

	public RfidDriverBase(DialogFragment dialog, Handler handler) {
		mDialogFragment = dialog;
		mHandler = handler;
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	public boolean SetOperationType(byte type) {
		types = type;
		return true;
	}

}
