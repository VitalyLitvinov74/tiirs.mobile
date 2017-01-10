package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.DataUtils;

import android.hardware.uhf.magic.UHFCommandResult;
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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                UHFCommandResult result;

                // запускаем поиск метки
                result = reader.readTagId(timeOut);
                if (result.result == reader.RESULT_SUCCESS) {
                    Log.d(TAG, result.data);
                    sHandler.obtainMessage(RESULT_RFID_SUCCESS, result.data).sendToTarget();
                } else if (result.result == reader.RESULT_TIMEOUT) {
                    sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
                } else {
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }
            }
        });
        thread.start();
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
	public void readTagData(String password, int memoryBank, int address, int count) {
		// запускаем поиск метки
		UHFCommandResult result = reader.readTagId(timeOut);
        if (result.result == reader.RESULT_SUCCESS) {
            String pcepc = result.data;
            Log.d("TAG", "tagId = " + pcepc);
            // читаем данные из памяти метки
            result = reader.readTagData(password, pcepc, memoryBank, address, count, timeOut);
            if (result.result == reader.RESULT_SUCCESS) {
                Log.d(TAG, result.data);
                sHandler.obtainMessage(RESULT_RFID_SUCCESS, result.data).sendToTarget();
            } else if (result.result == reader.RESULT_TIMEOUT) {
                sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
            } else {
                sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
            }
        } else if (result.result == reader.RESULT_TIMEOUT) {
            Log.d("TAG", "вышел таймаут.");
            sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
        } else {
            Log.d("TAG", "что-то пошло не так.");
            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
        }
	}

	/**
	 * Читаем метку с известным Id
	 */
	@Override
	public void readTagData(final String password, final String tagId, final int memoryBank,
                            final int address, final int count) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // устанавливаем "фильтр" по tagId для поиска метки
                boolean success;
                success = reader.select(reader.SELECT_ENABLE, 0x20, (byte)(tagId.length() / 2 * 8),
                        (byte)0x00, DataUtils.hexStringTobyte(tagId));

                if (success) {
                    // запускаем поиск метки
                    UHFCommandResult result = reader.readTagId(timeOut);
                    if (reader.SetSelect(reader.SELECT_DISABLE) == 0) {
                        Log.d(TAG, "Маска сборошена.");
                    } else {
                        Log.e(TAG, "Маска не сборошена!!!");
                    }

                    if (result.result == reader.RESULT_SUCCESS) {
                        String pcepc = result.data;
                        Log.d(TAG, "tagId within readTagData - " + pcepc);
                        // запускаем чтение данных из метки
                        result = reader.readTagData(password, pcepc, memoryBank, address, count, timeOut);
                        if (result.result == reader.RESULT_SUCCESS) {
                            Log.d("TAG", "данные успешно прочитаны.");
                            sHandler.obtainMessage(RESULT_RFID_SUCCESS, result.data).sendToTarget();
                        } else if (result.result == reader.RESULT_TIMEOUT) {
                            Log.d("TAG", "вышел таймаут.");
                            sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
                        } else {
                            Log.d("TAG", "что-то пошло не так.");
                            sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                        }
                    } else if (result.result == reader.RESULT_TIMEOUT) {
                        sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
                    } else {
                        sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                    }
                } else {
                    Log.d(TAG, "не смогли установить маску для фильтра по tagId");
                    sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
                }
            }
        });
        thread.start();
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
	public void writeTagData(final String password, final String tagId, final int memoryBank,
			final int address, final String data) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                UHFCommandResult result;
                String realTagId;

                // пытаемся найти подходящую метку установив фильтр
                boolean success;
                success = reader.select(reader.SELECT_ENABLE, 0x20, (byte)(tagId.length() / 2 * 8),
                        reader.TRUNCATE_DISABLE, DataUtils.hexStringTobyte(tagId));

                if (success) {
                    Log.d(TAG, "маска установленна!!!");
                    result = reader.readTagId(5000);
                    if (result.result == reader.RESULT_SUCCESS) {
                        realTagId = result.data;
                        reader.SetSelect(reader.SELECT_DISABLE);
                    } else {
                        reader.SetSelect(reader.SELECT_DISABLE);
                        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                        return;
                    }
                } else {
                    Log.d(TAG, "не удалось установить маску!!!");
                    sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                    return;
                }


                int endWriteData = address + data.length() / 2;
                String dataToWrite = data;

                if (endWriteData < 64) {
                    // читаем данные которые при записи будут затёрты, чтоб записать их по новой
                    result = reader.readTagData(password, realTagId, memoryBank,
                            endWriteData / 2, (64 - endWriteData) / 2, 5000);

                    if (result.result == reader.RESULT_SUCCESS) {
                        // данные успешно прочитаны
                        dataToWrite += result.data;
                    } else if (result.result == reader.RESULT_TIMEOUT) {
                        Log.d("TAG", "вышел таймаут.");
                        sHandler.obtainMessage(RESULT_RFID_TIMEOUT).sendToTarget();
                        return;
                    } else {
                        Log.d("TAG", "что-то пошло не так.");
                        sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                        return;
                    }
                }

                // TODO: вся поганка в том что если в "середине" процесса произойдёт ошибка, в метке будет "мусор"
                result = null;
                boolean prevCommandSuccess = true;
                int iter = (dataToWrite.length() / 2) / 16;
                int addressToWrite;
                int startData;
                int endData;
                String portion;
                for (int i = 0; i < iter; i++) {
                    addressToWrite = (address + i * 16) / 2;
                    startData = i * 32;
                    endData = startData + 32;
                    portion = dataToWrite.substring(startData, endData);
                    result = reader.writeTagData(password, realTagId, memoryBank, addressToWrite, portion, timeOut);
                    if (result.result != reader.RESULT_SUCCESS) {
                        // произошла ошибка
                        prevCommandSuccess = false;
                        break;
                    }
                }

                if (prevCommandSuccess) {
                    Log.d("TAG", "Данные успешно записаны.");
                    sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
                } else {
                    Log.e("TAG", "Не удалось записать данные в метку!");
                    sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                }
            }
        });
        thread.start();
    }
}
