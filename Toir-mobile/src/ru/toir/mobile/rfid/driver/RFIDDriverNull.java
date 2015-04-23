package ru.toir.mobile.rfid.driver;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который ни чего не делает.</p>
 */
public class RFIDDriverNull implements RFIDDriver{
	/**
	 * <p>Инициализируем драйвер</p>
	 * @return
	 */
	@Override
	public boolean init(){
		return false;
	}
	
	/**
	 * <p>Считываем метку</p>
	 * @return
	 */
	@Override
	public String read() {
		return null;
	}
	
	/**
	 * <p>Записываем в метку</p>
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer){
		return false;
	}

	/**
	 * <p>Завершаем работу драйвера</p>
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
}
