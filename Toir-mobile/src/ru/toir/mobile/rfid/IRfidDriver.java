package ru.toir.mobile.rfid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Интерфейс драйвера считывателя RFID
 *         </p>
 */
public interface IRfidDriver {

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
	 * Считывание id метки
	 * </p>
	 * 
	 * @return
	 */
	public void readTagId(byte type);

	/**
	 * <p>
	 * Запись в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return
	 */
	// TODO нужно пересмотреть параметры
	public boolean write(byte[] outBuffer);

	/**
	 * <p>
	 * Завершение работы драйвера
	 * </p>
	 */
	public void close();

	/**
	 * <p>
	 * Интерфейс пользователя который предоставляет драйвер
	 * </p>
	 * Здесь создаётся весь необходимый интерфейс для взаимодействия с
	 * пользователем необходимый драйверу.
	 */
	public View getView(LayoutInflater inflater, ViewGroup viewGroup);

}
