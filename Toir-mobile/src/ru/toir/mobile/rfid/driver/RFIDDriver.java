package ru.toir.mobile.rfid.driver;

/**
 * @author koputo
 * <p>Интерфейс драйвера считывателя RFID</p>
 */
public interface RFIDDriver {
	/**
	 * <p>Инициализация драйвера</p>
	 * @return
	 */
	public boolean init();
	
	/**
	 * <p>Считывание метки</p>
	 * @return
	 */
	public String read();
	
	/**
	 * <p>Запись в метку</p>
	 * @param outBuffer
	 * @return
	 */
	public boolean write(byte[] outBuffer);
	
	/**
	 * <p>Завершение работы драйвера</p>
	 */
	public void close();

}
