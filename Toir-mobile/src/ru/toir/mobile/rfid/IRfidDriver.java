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
	 * Читаем данные с метки в два этапа, сначала находим метку(получаем Id),
	 * потом читаем данные.
	 * 
	 * @param password
	 *            Пароль для доступа к метке
	 * @param memoryBank
	 *            Область памяти метки
	 * @param address
	 *            Адрес с которого начать чтение (кратен 2, 0 = 0, 1 = 2, 2 = 4
	 *            и т.д.)
	 * @param count
	 *            Количество байт для чтения (кратен 2, 1 = 2, 2 = 4, 3 = 6 и
	 *            т.д.)
	 */
	public void readTagData(String password, int memoryBank, int address,
			int count);

	/**
	 * Читаем данные с метки в один этап.
	 * 
	 * @param password
	 *            Пароль для доступа к метке
	 * @param tagId
	 *            Id метки
	 * @param memoryBank
	 *            Область памяти метки
	 * @param address
	 *            Адрес с которого начать чтение (кратен 2, 0 = 0, 1 = 2, 2 = 4
	 *            и т.д.)
	 * @param count
	 *            Количество байт для чтения (кратен 2, 1 = 2, 2 = 4, 3 = 6 и
	 *            т.д.)
	 */
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count);

	/**
	 * Пишем в метку в два этапа, сначала находим метку(получаем Id), потом
	 * пишем данные.
	 * 
	 * @param password
	 *            Пароль для доступа к метке
	 * @param memoryBank
	 *            Область памяти метки
	 * @param address
	 *            Адрес с которого начать чтение (кратен 2, 0 = 0, 1 = 2, 2 = 4
	 *            и т.д.)
	 * @param data
	 *            Данные для записи
	 */
	public void writeTagData(String password, int memoryBank, int address, byte[] data);

	/**
	 * Пишем в метку в один этап.
	 * 
	 * @param password
	 *            Пароль для доступа к метке
	 * @param tagId
	 *            Id метки
	 * @param memoryBank
	 *            Область памяти метки
	 * @param address
	 *            Адрес с которого начать чтение (кратен 2, 0 = 0, 1 = 2, 2 = 4
	 *            и т.д.)
	 * @param data
	 *            Данные для записи
	 */
	public void writeTagData(String password, String tagId, int memoryBank, int address,
			byte[] data);

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
