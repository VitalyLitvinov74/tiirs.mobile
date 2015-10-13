package ru.toir.mobile.rfid.driver;

import android.app.DialogFragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Интерфейс драйвера считывателя RFID
 *         </p>
 */
public interface RFIDDriver {

	static byte types = 0;

	/**
	 * <p>
	 * Инициализация драйвера
	 * </p>
	 * 
	 * @return
	 */
	public boolean init(byte type);

	/**
	 * <p>
	 * Считывание метки
	 * </p>
	 * 
	 * @return
	 */
	public void read(byte type);

	/**
	 * <p>
	 * Запись в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return
	 */
	public boolean write(byte[] outBuffer);

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	public boolean SetOperationType(byte type);

	/**
	 * <p>
	 * Завершение работы драйвера
	 * </p>
	 */
	public void close();

	/**
	 * <p>
	 * Интерфейс который предоставляет драйвер
	 * </p>
	 */
	public View getView(LayoutInflater inflater, ViewGroup viewGroup);

	/**
	 * <p>
	 * Handler который будет обрабатывать сообщение от драйвера
	 * </p>
	 */
	public void setHandler(Handler handler);

	/**
	 * <p>
	 * Передаём в драйвер текущий фрагмент
	 * </p>
	 * 
	 * @return
	 */
	public void setDialogFragment(DialogFragment fragment);

}
