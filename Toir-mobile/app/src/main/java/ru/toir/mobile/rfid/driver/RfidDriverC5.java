package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.DataUtils;

import android.hardware.uhf.magic.UHFCommandResult;
import android.hardware.uhf.magic.reader;
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
	public void writeTagData(String password, int memoryBank, int address, String data) {
            writeTagData(password, null, memoryBank, address, data);
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank, int address,
                             String data) {

        WriteDataToTAG runnable = new WriteDataToTAG(password, tagId, memoryBank, address, data);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private class WriteDataToTAG implements Runnable {
        private String password;
        private String tagId;
        private int memoryBank;
        private int address;
        private String data;

        WriteDataToTAG(String password, String tagId, int memoryBank, int address, String data) {
            this.password = password;
            this.tagId = tagId;
            this.memoryBank = memoryBank;
            this.address = address;
            this.data = data;
        }

        @Override
        public void run() {
            UHFCommandResult result;
            String realTagId;

            if (tagId == null) {
                result = reader.readTagId(5000);
                if (result.result == reader.RESULT_SUCCESS) {
                    tagId = result.data.substring(4);
                    realTagId = result.data;
                } else {
                    sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
                    return;
                }
            } else {
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
            }

            boolean changePC = address == 0;

            int endWriteData = address + data.length() / 2;
            String dataToWrite = data;

            if (endWriteData < 64) {
                // читаем данные которые при записи будут затёрты, чтоб записать их по новой
                result = reader.readTagData(password, realTagId, memoryBank,
                        endWriteData, (64 - endWriteData), 5000);

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
            boolean prevCommandSuccess = true;
            int addressToWrite;
            int startData;
            int endData;
            String portion;
            int lenToWrite = dataToWrite.length() / 2;
            int index = 0;

            do {
                addressToWrite = (address + index * 16);
                startData = index * 32;
                endData = startData + (lenToWrite >= 16 ? 32 : lenToWrite * 2);
                portion = dataToWrite.substring(startData, endData);
                result = reader.writeTagData(password, realTagId, memoryBank, addressToWrite, portion, timeOut);
                if (result.result != reader.RESULT_SUCCESS) {
                    // произошла ошибка
                    prevCommandSuccess = false;
                    break;
                }

                if (changePC) {
                    changePC = false;
                    if (data.substring(0, 2).equals("00")) {
                        realTagId = "3000" + tagId;
                    } else {
                        realTagId = "3400" + tagId;
                    }
                }

                lenToWrite -= 16;
                index++;
            } while(lenToWrite > 0);

            if (prevCommandSuccess) {
                Log.d("TAG", "Данные успешно записаны.");
                sHandler.obtainMessage(RESULT_RFID_SUCCESS).sendToTarget();
            } else {
                Log.e("TAG", "Не удалось записать данные в метку!");
                sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
            }
        }
    }
}
