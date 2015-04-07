package ru.toir.mobile.rfid.driver;

/**
 * @author koputo
 * <p>Драйвер считывателя RFID который "считывает" содержимое меток из текстового файла.</p>
 */
public class RFIDDriverText implements RFIDDriver{
	/**
	 * <p>Инициализируем драйвер</p>
	 * @return
	 */
	@Override
	public boolean init(){
		return true;
	}
	
	/**
	 * <p>Считываем метку</p>
	 * @return
	 */
	@Override
	public String read() {
		return "01234567";
	}
	
	/**
	 * <p>Записываем в метку</p>
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer){
		// TODO Auto-generated method stub
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
