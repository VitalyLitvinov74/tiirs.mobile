package ru.toir.mobile.rfid;

import android.app.Activity;
import android.app.Fragment;
import android.preference.PreferenceScreen;
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
	boolean init();

	/**
	 * <p>
	 * Считывание id метки
	 * </p>
	 * 
	 * @return
	 */
	void readTagId();

    /**
     * Считывание всех id меток в поле считывателя.
     */
    void readMultiplyTagId(String[] tagIds);

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
    void readTagData(String password, int memoryBank, int address, int count);

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
    void readTagData(String password, String tagId, int memoryBank, int address, int count);

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
    void writeTagData(String password, int memoryBank, int address, String data);

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
    void writeTagData(String password, String tagId, int memoryBank, int address, String data);

	/**
	 * <p>
	 * Завершение работы драйвера
	 * </p>
	 */
	void close();

	/**
	 * <p>
	 * Интерфейс пользователя который предоставляет драйвер
	 * </p>
	 * Здесь создаётся весь необходимый интерфейс для взаимодействия с
	 * пользователем необходимый драйверу.
	 */
	View getView(LayoutInflater inflater, ViewGroup viewGroup);

	/**
	 * Передаём в драйвер активити в которую при необходимости драйвер может
	 * предать результат из собственной активити.
	 * 
	 * @param activity
	 *            Активити в которой будет обрабатываться onActivityResult
	 */
	void setIntegration(Activity activity);

	/**
	 * Передаём в драйвер фрагмент в который при необходимости драйвер может
	 * предать результат из собственной активити.
	 * 
	 * @param fragment
	 *            Фрагмент в котором будет обрабатываться onActivityResult
	 */
	void setIntegration(Fragment fragment);

	/**
	 * Интерфейс индивидуальных настроек драйвера.
	 * 
	 * @param screen
	 *            Экран настроек в котором драйвер строит свой интерфейс.
	 * @return Если настроек нет, должен вернуть null, в противном случае
	 *         переданный раннее screen.
	 */
	PreferenceScreen getSettingsScreen(PreferenceScreen screen);
}
