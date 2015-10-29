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

		reader.Init("");
		reader.Open("");
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
		reader.inventoryLabelsLoop();
	}

	static private class ReadTagIdHandler extends Handler {
		@Override
		public void handleMessage(Message message) {

			if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
				String data = (String) message.obj;
				Log.d(TAG, data);
				reader.StopLoop();
				Message result = new Message();
				result.arg1 = RESULT_RFID_SUCCESS;
				result.obj = data;
				mHandler.sendMessage(result);
			} else {
				Message result = new Message();
				result.arg1 = RESULT_RFID_READ_ERROR;
				mHandler.sendMessage(result);
			}
		}
	}

	@Override
	public void close() {

		reader.StopLoop();
		reader.m_handler = null;
		android.hardware.uhf.magic.reader.Close();
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		View view = inflater.inflate(R.layout.rfid_read, viewGroup);
		return view;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

		reader.m_handler = new ReadTagDataWOIdHandler(password, memoryBank,
				address, count);
		reader.inventoryLabelsLoop();
	}

	static private class ReadTagDataWOIdHandler extends Handler {

		private String password;
		private int memoryBank;
		private int address;
		private int count;

		public ReadTagDataWOIdHandler(String password, int memoryBank,
				int address, int count) {

			this.password = password;
			this.memoryBank = memoryBank;
			this.address = address;
			this.count = count;
		}

		@Override
		public void handleMessage(Message message) {

			if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
				String tagId = (String) message.obj;
				Log.d(TAG, tagId);
				reader.StopLoop();
				// запускаем чтение метки
				reader.m_handler = new ReadTagDataHandler();
				reader.ReadLables(reader.stringToBytes(password),
						reader.stringToBytes(tagId).length,
						reader.stringToBytes(tagId), (byte) memoryBank,
						(byte) address, count / 2);
			} else {
				Message result = new Message();
				result.arg1 = RESULT_RFID_READ_ERROR;
				mHandler.sendMessage(result);
			}
		}
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		reader.m_handler = new ReadTagDataHandler();
		reader.ReadLables(reader.stringToBytes(password),
				reader.stringToBytes(tagId).length,
				reader.stringToBytes(tagId), (byte) memoryBank, (byte) address,
				count / 2);
	}

	static private class ReadTagDataHandler extends Handler {
		@Override
		public void handleMessage(Message message) {

			if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
				String data = (String) message.obj;
				Log.d(TAG, data);
				reader.StopLoop();
				Message result = new Message();
				result.arg1 = RESULT_RFID_SUCCESS;
				result.obj = data;
				mHandler.sendMessage(result);
			} else {
				Message result = new Message();
				result.arg1 = RESULT_RFID_READ_ERROR;
				mHandler.sendMessage(result);
			}
		}
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			byte[] data) {

		reader.m_handler = new WriteTagDataWOIdHandler(password, memoryBank,
				address, data);
		reader.inventoryLabelsLoop();
	}

	static private class WriteTagDataWOIdHandler extends Handler {

		private String password;
		private int memoryBank;
		private int address;
		private byte[] data;

		public WriteTagDataWOIdHandler(String password, int memoryBank,
				int address, byte[] data) {

			this.password = password;
			this.memoryBank = memoryBank;
			this.address = address;
			this.data = data;
		}

		@Override
		public void handleMessage(Message message) {

			if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
				String tagId = (String) message.obj;
				Log.d(TAG, tagId);
				reader.StopLoop();
				// запускаем запись в метку
				reader.m_handler = new WriteTagDataHandler();
				reader.Writelables(reader.stringToBytes(password),
						reader.stringToBytes(tagId).length,
						reader.stringToBytes(tagId), (byte) memoryBank,
						(byte) address, data.length, data);
			} else {
				Message result = new Message();
				result.arg1 = RESULT_RFID_READ_ERROR;
				mHandler.sendMessage(result);
			}
		}
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, byte[] data) {

		reader.m_handler = new WriteTagDataHandler();

		reader.Writelables(reader.stringToBytes(password),
				reader.stringToBytes(tagId).length,
				reader.stringToBytes(tagId), (byte) memoryBank, (byte) address,
				data.length, data);
	}

	static private class WriteTagDataHandler extends Handler {
		@Override
		public void handleMessage(Message message) {

			if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
				Message result = new Message();
				result.arg1 = RESULT_RFID_SUCCESS;
				mHandler.sendMessage(result);
			} else {
				Message result = new Message();
				result.arg1 = RESULT_RFID_WRITE_ERROR;
				mHandler.sendMessage(result);
			}
		}
	}

}
