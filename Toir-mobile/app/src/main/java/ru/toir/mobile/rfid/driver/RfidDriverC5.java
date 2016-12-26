package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.DataUtils;
import android.hardware.uhf.magic.reader;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachev
 *         <p>
 *             Драйвер считывателя RFID в устройстве С5.
 *         </p>
 *         <p>
 *             В прошивке считывателя содержится ошибка, в результате которой при записи данных
 *             в метку пишется N+1 байт. Это приводит к порче данных следующих за последним байтом
 *             записываемых данных. Так же при записи данных по границе памяти метки,
 *             данные записываются успешно, но прошивка возвращает ошибку записи. Для "комфортной"
 *             работы реализована запись в несколько приёмов. То есть, считать данные
 *             следующие за предполагаемыми к записи данными до конца памяти метки. Записать данные
 *             в метку. Записать ранее считаные данные в метку по границе памяти метки.
 *             Получить ошибку записи. Считать для проверки данные из метки и сравнить с с ранее
 *             считанными.
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
					sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg.obj).sendToTarget();
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
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
	public void readTagData(final String password, final int memoryBank, final int address,
							final int count) {

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
								sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg.obj).sendToTarget();
							} else if (msg.what == reader.RESULT_TIMEOUT) {
								sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
							} else {
								sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
							}

							return true;
						}
					});

					// читаем данные из памяти метки
					reader.readTagData(password, pcepc, memoryBank, address, count, timeOut);
				} else if (msg.what == reader.RESULT_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
				} else {
					Log.d("TAG", "что-то пошло не так.");
					sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
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
	public void readTagData(final String password, String tagId, final int memoryBank,
                            final int address, final int count) {

        Handler findTagIdHandler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                if (msg.what == reader.RESULT_SUCCESS) {
                    String data = (String) msg.obj;
                    Log.d(TAG, "tagId within readTagData - " + data);

                    if (reader.SetSelect(reader.SELECT_DISABLE) == 0) {
                        Log.d(TAG, "маска как бы сборошена после чтения tagId!!!");
                    }

					reader.m_handler = new Handler(new Handler.Callback() {

						@Override
						public boolean handleMessage(Message msg) {
							if (msg.what == reader.RESULT_SUCCESS) {
								Log.d("TAG", "данные успешно прочитаны.");
								sHandler.obtainMessage(RESULT_RFID_SUCCESS, msg.obj).sendToTarget();
							} else if (msg.what == reader.RESULT_TIMEOUT) {
								Log.d("TAG", "вышел таймаут.");
								sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
							} else {
								Log.d("TAG", "что-то пошло не так.");
								sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
							}

							return true;
						}
					});

                    // запускаем чтение данных из метки
                    reader.readTagData(password, data, memoryBank, address, count, timeOut);

				} else if (msg.what == reader.RESULT_TIMEOUT) {
                    sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
                } else {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }

                reader.SetSelect(reader.SELECT_DISABLE);
                return true;
            }
        });

        // устанавливаем "фильтр" по tagId для поиска метки
        boolean result;
        result = reader.select((byte)0x01, 0x20, (byte)(tagId.length() / 2 * 8), (byte)0x00, DataUtils.hexStringTobyte(tagId));
        if (result) {
            // обработчик сообщений на команду поиска определённой метки
            reader.m_handler = findTagIdHandler;
            // запускаем поиск метки
            reader.readTagId(timeOut);
        } else {
            Log.d(TAG, "не смогли установить маску для фильтра по tagId");
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
        }
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {
		// TODO: реализовать запись в метку в три шага
        // TODO: 1) считать данные сразу за данными для записм до конца метки
        // TODO: 2) записать переданные данные
        // TODO: 3) записать данные считанные ранее (в ответ будет ошибка)
        // TODO: возможно счтитать "контрольные" данные для проверки записи в метку

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
        // TODO: реализовать запись в метку в три шага
        // TODO: 1) считать данные сразу за данными для записм до конца метки
        // TODO: 2) записать переданные данные
        // TODO: 3) записать данные считанные ранее (в ответ будет ошибка)
        // TODO: возможно счтитать "контрольные" данные для проверки записи в метку

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
