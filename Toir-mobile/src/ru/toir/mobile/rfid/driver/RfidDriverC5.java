package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
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
public class RfidDriverC5 extends RfidDriverBase implements IRfidDriver {

	private static final String TAG = "RfidDriverC5";

	public RfidDriverC5(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	@Override
	public boolean init() {

		Log.d(TAG, "init");

		reader.Init("/dev/ttyMT2");
		reader.Open("/dev/ttyMT2");
		if (reader.SetTransmissionPower(1950) == 0x11) {
			if (reader.SetTransmissionPower(1950) == 0x11) {
				reader.SetTransmissionPower(1950);
			}
		}
		return true;
	}

	@Override
	public void readTagId() {

		// TODO timeOut ????
		int timeOut = 5000;

		reader.m_handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					String data = (String) msg.obj;
					Log.d(TAG, data);
					Message message = new Message();
					message.what = RESULT_RFID_SUCCESS;
					message.obj = data;
					mHandler.sendMessage(message);
				} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
					Message message = new Message();
					message.what = RESULT_RFID_TIMEOUT;
					mHandler.sendMessage(message);
				} else {
					Message message = new Message();
					message.what = RESULT_RFID_READ_ERROR;
					mHandler.sendMessage(message);
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
		mHandler = null;
		reader.Close();
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		View view = inflater.inflate(R.layout.rfid_read, viewGroup);
		return view;
	}

	/**
	 * Читаем произвольную метку.
	 */
	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

		// TODO таймаут нужно задать константой или вынести повыше
		final int timeOut = 5000; // 5 секунд на всё

		final String lPassword = password;
		final int lMemoryBank = memoryBank;
		final int lAddress = address;
		final int lCount = count;

		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RESULT_RFID_SUCCESS) {
					String pcepc = (String) msg.obj;
					Log.d("TAG", "tagId = " + pcepc);
					reader.m_handler = new Handler(new Handler.Callback() {
						
						@Override
						public boolean handleMessage(Message msg) {

							if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
								String data = (String) msg.obj;
								Log.d(TAG, data);
								Message message = new Message();
								message.what = RESULT_RFID_SUCCESS;
								message.obj = data;
								mHandler.sendMessage(message);
							} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
								Message message = new Message();
								message.what = RESULT_RFID_TIMEOUT;
								mHandler.sendMessage(message);
							} else {
								Message message = new Message();
								message.what = RESULT_RFID_READ_ERROR;
								mHandler.sendMessage(message);
							}
							return true;
						}
					});
					// читаем данные из памяти метки
					reader.readTagData(lPassword, pcepc, lMemoryBank, lAddress,
							lCount, timeOut);
				} else if (msg.what == RESULT_RFID_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					Message message = new Message();
					message.what = RESULT_RFID_TIMEOUT;
					mHandler.sendMessage(message);
				} else {
					Log.d("TAG", "что-то пошло не так.");
					Message message = new Message();
					message.what = RESULT_RFID_READ_ERROR;
					mHandler.sendMessage(message);
				}
				return true;
			}
		});

		reader.m_handler = handler;

		// запускаем поиск метки
		reader.readTagId(timeOut);

	}

	/**
	 * Читаем метку с известным Id
	 */
	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		// TODO таймаут нужно задать константой или вынести повыше
		final int timeOut = 5000; // 5 секунд на всё

		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RESULT_RFID_SUCCESS) {
					// данные успешно прочитаны
					Message message = new Message();
					message.what = RESULT_RFID_SUCCESS;
					message.obj = msg.obj;
					mHandler.sendMessage(message);
				} else if (msg.what == RESULT_RFID_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					Message message = new Message();
					message.what = RESULT_RFID_TIMEOUT;
					mHandler.sendMessage(message);
				} else {
					Log.d("TAG", "что-то пошло не так.");
					Message message = new Message();
					message.what = RESULT_RFID_READ_ERROR;
					mHandler.sendMessage(message);
				}
				return true;
			}
		});

		reader.m_handler = handler;

		// запускаем чтение данных из метки
		reader.readTagData(password, tagId, memoryBank, address, count, timeOut);
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {

		// TODO timeOut ????
		final int timeOut = 5000;
		
		final String lPassword = password;
		final int lMemoryBank = memoryBank;
		final int lAddress = address;
		final String lData = data;

		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RESULT_RFID_SUCCESS) {
					String pcepc = (String) msg.obj;
					Log.d("TAG", "tagId = " + pcepc);
					reader.m_handler = new Handler(new Handler.Callback() {
						
						@Override
						public boolean handleMessage(Message msg) {

							if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
								Message message = new Message();
								message.what = RESULT_RFID_SUCCESS;
								mHandler.sendMessage(message);
							} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
								Message message = new Message();
								message.what = RESULT_RFID_TIMEOUT;
								mHandler.sendMessage(message);
							} else {
								Message message = new Message();
								message.what = RESULT_RFID_WRITE_ERROR;
								mHandler.sendMessage(message);
							}
							return true;
						}
					});
					// пишем данные в метку
					reader.writeTagData(lPassword, pcepc, lMemoryBank, lAddress,
							lData, timeOut);
				} else if (msg.what == RESULT_RFID_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					Message message = new Message();
					message.what = RESULT_RFID_TIMEOUT;
					mHandler.sendMessage(message);
				} else {
					Log.d("TAG", "что-то пошло не так.");
					Message message = new Message();
					message.what = RESULT_RFID_READ_ERROR;
					mHandler.sendMessage(message);
				}
				return true;
			}
		});
		reader.m_handler = handler;

		// запускаем поиск метки
		reader.readTagId(timeOut);
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

		// TODO timeOut ????
		final int timeOut = 5000; // 5 секунд на всё

		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RESULT_RFID_SUCCESS) {
					// данные успешно записаны
					Message message = new Message();
					message.what = RESULT_RFID_SUCCESS;
					mHandler.sendMessage(message);
				} else if (msg.what == RESULT_RFID_TIMEOUT) {
					Log.d("TAG", "вышел таймаут.");
					Message message = new Message();
					message.what = RESULT_RFID_TIMEOUT;
					mHandler.sendMessage(message);
				} else {
					Log.d("TAG", "что-то пошло не так.");
					Message message = new Message();
					message.what = RESULT_RFID_WRITE_ERROR;
					mHandler.sendMessage(message);
				}
				return true;
			}
		});

		reader.m_handler = handler;

		reader.writeTagData(password, tagId, memoryBank, address, data, timeOut);
	}

}
