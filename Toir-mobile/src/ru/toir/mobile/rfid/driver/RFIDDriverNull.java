package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import android.app.DialogFragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который ни чего не делает.
 *         </p>
 */
public class RFIDDriverNull implements RFIDDriver {

	static byte types = 0;

	@Override
	public boolean init(byte type) {
		return true;
	}

	/**
	 * <p>
	 * Считываем метку
	 * </p>
	 * 
	 * @return
	 */
	@Override
	public void read(byte type) {
	}

	/**
	 * <p>
	 * Записываем в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	/**
	 * <p>
	 * Завершаем работу драйвера
	 * </p>
	 */
	@Override
	public void close() {
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {
		types = type;
		return true;
	}

	public int getLayout() {
		return R.layout.rfid_dialog_text;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#getView(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setActivity(android.app.Activity)
	 */
	@Override
	public void setDialogFragment(DialogFragment fragment) {

	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setHandler(android.os.Handler)
	 */
	@Override
	public void setHandler(Handler handler) {
		
	}

}
