package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
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

	public final static int MEMORY_BANK_RESERVED = 0;
	public final static int MEMORY_BANK_EPC = 1;
	public final static int MEMORY_BANK_TID = 2;
	public final static int MEMORY_BANK_USER = 3;

	private static String m_strresult = "";

	public RfidDriverC5(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	@Override
	public boolean init() {

		Log.d(TAG, "init");

		android.hardware.uhf.magic.reader.init("/dev/ttyMT1");
		android.hardware.uhf.magic.reader.Open("/dev/ttyMT1");
		if (reader.SetTransmissionPower(1950) != 0x11) {
			if (reader.SetTransmissionPower(1950) != 0x11) {
				reader.SetTransmissionPower(1950);
			}
		}
		return true;
	}

	@Override
	public void readTagId() {

		reader.m_handler = new ReadTagIdHandler();
		android.hardware.uhf.magic.reader.InventoryLablesLoop();
	}

	@Override
	public void close() {

		reader.StopLoop();
		reader.m_handler = null;
		android.hardware.uhf.magic.reader.Close();
	}

	static private class ReadTagIdHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			if (message.what != 0) {
				if (m_strresult.indexOf((String) message.obj) < 0) {
					Log.d(TAG, (String) message.obj);
					m_strresult += (String) message.obj;
					reader.StopLoop();
					Message result = new Message();
					result.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
					Bundle bundle = new Bundle();
					bundle.putString(RfidDriverBase.RESULT_RFID_TAG_ID,
							m_strresult);
					result.setData(bundle);
					mHandler.sendMessage(result);

				}
			}
		}
	};

	static private class ReadTagDataHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			if (message.what != 0) {
				if (m_strresult.indexOf((String) message.obj) < 0) {
					Log.d(TAG, (String) message.obj);
					m_strresult += (String) message.obj;
					reader.StopLoop();
					Message result = new Message();
					result.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
					Bundle bundle = new Bundle();
					bundle.putString(RfidDriverBase.RESULT_RFID_TAG_ID,
							m_strresult);
					result.setData(bundle);
					mHandler.sendMessage(result);

				}
			}
		}
	};

	static private class writeTagDataHandler extends Handler {
		@Override
		public void handleMessage(Message message) {

			// TODO реализовать обработку сообщений считывателя во время записи
			// в метку
			if (message.what != 0) {
				if (m_strresult.indexOf((String) message.obj) < 0) {
					Log.d(TAG, (String) message.obj);
					m_strresult += (String) message.obj;
					// возврат при записи памяти оборудования
					// ((???) mActivity).CallbackOnWrite(m_strresult);
				}
			}
		}
	};

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		View view = inflater.inflate(R.layout.rfid_read, viewGroup);
		return view;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

		// TODO реализовать получение Id метки
		// TODO реализовать чтение метки по полученому Id
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		m_strresult = "";
		reader.m_handler = new ReadTagDataHandler();
		reader.ReadLables(password.getBytes(), tagId.getBytes().length,
				tagId.getBytes(), (byte) memoryBank, (byte) address, count);
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			byte[] data) {

		// TODO реализовать получение Id метки
		// TODO реализовать запись метки по полученому Id
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, byte[] data) {

		reader.m_handler = new writeTagDataHandler();

		int rc = reader.Writelables(password.getBytes(),
				tagId.getBytes().length, tagId.getBytes(), (byte) memoryBank,
				(byte) address, data.length, data);

		Message message = new Message();
		if (rc >= 0) {
			message.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
		} else {
			message.arg1 = RfidDriverBase.RESULT_RFID_WRITE_ERROR;
		}
		mHandler.sendMessage(message);
	}

}
