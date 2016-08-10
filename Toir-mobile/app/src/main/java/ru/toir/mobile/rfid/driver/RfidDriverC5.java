package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.hardware.uhf.magic.reader;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author koputo
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
@SuppressWarnings("unused")
// объект класса создаётся не напрямую
public class RfidDriverC5 extends RfidDriverBase implements IRfidDriver {
	@SuppressWarnings("unused")
    // к этому свойству обращаемся не на прямую
	public static final String DRIVER_NAME = "Драйвер UHF C5";
	private static final String TAG = "RfidDriverC5";

	// по умолчанию таймаут на операцию 5 секунд
	private static final int timeOut = 5000;

	@Override
	public boolean init() {
		Log.d(TAG, "init");

		if (reader.Init("/dev/ttyMT2") == 0) {
			reader.Open("/dev/ttyMT2");
			if (reader.SetTransmissionPower(1950) == 0x11) {
				if (reader.SetTransmissionPower(1950) == 0x11) {
					reader.SetTransmissionPower(1950);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void readTagId() {
		reader.m_handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				if (msg.what == reader.RESULT_SUCCESS) {
					String data = (String) msg.obj;
					Log.d(TAG, data);
					sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg.obj)
							.sendToTarget();
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					sHandler.obtainMessage(RESULT_RFID_READ_ERROR)
							.sendToTarget();
				}
				return true;
			}
		});

		// запускаем поиск метки
		reader.readTagId(timeOut);
	}

	@Override
	public void close() {
		reader.m_handler = null;
		sHandler = null;
		//reader.Close();
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.rfid_read, viewGroup);
	}

	/**
	 * Читаем произвольную метку.
	 */
	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		final String lPassword = password;
		final int lMemoryBank = memoryBank;
		final int lAddress = address;
		final int lCount = count;

        reader.m_handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == reader.RESULT_SUCCESS) {
					String pcepc = (String) msg.obj;
					Log.d("TAG", "tagId = " + pcepc);
					reader.m_handler = new Handler(new Handler.Callback() {

						@Override
						public boolean handleMessage(Message msg) {

							if (msg.what == reader.RESULT_SUCCESS) {
								String data = (String) msg.obj;
								Log.d(TAG, data);
								sHandler.obtainMessage(RESULT_RFID_SUCCESS,
										msg.obj).sendToTarget();
							} else if (msg.what == reader.RESULT_TIMEOUT) {
								sHandler.obtainMessage(RESULT_RFID_TIMEOUT)
										.sendToTarget();
							} else {
								sHandler.obtainMessage(RESULT_RFID_READ_ERROR)
										.sendToTarget();
							}
							return true;
						}
					});

					// читаем данные из памяти метки
					reader.readTagData(lPassword, pcepc, lMemoryBank, lAddress,
							lCount, timeOut);
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					Log.d("TAG", "что-то пошло не так.");
					sHandler.obtainMessage(RESULT_RFID_READ_ERROR)
							.sendToTarget();
				}
				return true;
			}
		});

		// запускаем поиск метки
		reader.readTagId(timeOut);

	}

	/**
	 * Читаем метку с известным Id
	 */
	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {
        reader.m_handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == reader.RESULT_SUCCESS) {
					// данные успешно прочитаны
					sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg.obj)
							.sendToTarget();
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					Log.d("TAG", "что-то пошло не так.");
					sHandler.obtainMessage(RESULT_RFID_READ_ERROR)
							.sendToTarget();
				}
				return true;
			}
		});

		// запускаем чтение данных из метки
		reader.readTagData(password, tagId, memoryBank, address, count, timeOut);
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {
		final String lPassword = password;
		final int lMemoryBank = memoryBank;
		final int lAddress = address;
		final String lData = data;

        reader.m_handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == reader.RESULT_SUCCESS) {
					String pcepc = (String) msg.obj;
					Log.d("TAG", "tagId = " + pcepc);
					reader.m_handler = new Handler(new Handler.Callback() {

						@Override
						public boolean handleMessage(Message msg) {
							if (msg.what == reader.RESULT_SUCCESS) {
								sHandler.obtainMessage(RESULT_RFID_SUCCESS)
										.sendToTarget();
							} else if (msg.what == reader.RESULT_TIMEOUT) {
								sHandler.obtainMessage(RESULT_RFID_TIMEOUT)
										.sendToTarget();
							} else {
								sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR)
										.sendToTarget();
							}
							return true;
						}
					});

					// пишем данные в метку
					reader.writeTagData(lPassword, pcepc, lMemoryBank,
							lAddress, lData, timeOut);
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					Log.d("TAG", "что-то пошло не так.");
					sHandler.obtainMessage(RESULT_RFID_READ_ERROR)
							.sendToTarget();
				}
				return true;
			}
		});

		// запускаем поиск метки
		reader.readTagId(timeOut);
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {
        reader.m_handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == reader.RESULT_SUCCESS) {
					// данные успешно записаны
					sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					Log.d("TAG", "что-то пошло не так.");
					sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR)
							.sendToTarget();
				}
				return true;
			}
		});

		reader.writeTagData(password, tagId, memoryBank, address, data, timeOut);
	}
}
