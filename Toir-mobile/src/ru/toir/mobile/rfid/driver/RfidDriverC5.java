package ru.toir.mobile.rfid.driver;

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
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author koputo
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RfidDriverC5 extends RfidDriverBase implements IRfidDriver {

	public final static int READ_USER_LABLE = 1;
	public final static int READ_EQUIPMENT_LABLE_ID = 2;
	public final static int READ_EQUIPMENT_OPERATION_LABLE_ID = 3;
	public final static int READ_EQUIPMENT_MEMORY = 4;
	public final static int READ_EQUIPMENT_OPERATION_MEMORY = 5;
	public final static int WRITE_EQUIPMENT_OPERATION_MEMORY = 6;
	public final static int WRITE_EQUIPMENT_MEMORY = 7;
	public final static int WRITE_USER_MEMORY = 8;

	public final static int USER_MEMORY_BANK = 3;

	private Handler c5Handler = new MainHandler();
	static String m_strresult = "";
	static int m_nCount = 0;
	CheckBox m_check;
	EditText m_address;
	static String mPCEPC = "";

	public RfidDriverC5(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	@Override
	public boolean init(byte type) {

		types = type;
		if (type == READ_USER_LABLE) {
			// mActivity.setContentView(R.layout.bar2d_read);
			// mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		reader.m_handler = c5Handler;
		android.hardware.uhf.magic.reader.init("/dev/ttyMT1");
		android.hardware.uhf.magic.reader.Open("/dev/ttyMT1");
		Log.e("7777777777", "111111111111111111111111111111111111");
		if (reader.SetTransmissionPower(1950) != 0x11) {
			if (reader.SetTransmissionPower(1950) != 0x11) {
				reader.SetTransmissionPower(1950);
			}
		}
		return true;
	}

	@Override
	public void readTagId(byte type) {

		types = type;
		// scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);
		if (type <= READ_USER_LABLE)
			android.hardware.uhf.magic.reader.InventoryLablesLoop();
		if (type == READ_EQUIPMENT_LABLE_ID
				|| type == READ_EQUIPMENT_OPERATION_LABLE_ID)
			android.hardware.uhf.magic.reader.InventoryLables();
		if (type == READ_EQUIPMENT_MEMORY
				|| type == READ_EQUIPMENT_OPERATION_MEMORY) {
			reader.m_strPCEPC = mPCEPC;
			byte[] epc = reader.stringToBytes(reader.m_strPCEPC);
			byte memoryBank = USER_MEMORY_BANK; // user memory
			int address = 0; // читаем всегда с начала
			int dataLength = 32; // длина памяти данных
			String passwordString = "00000000"; // пароль
			byte[] password = reader.stringToBytes(passwordString);
			try {
				Thread.sleep(690);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m_strresult = "";
			reader.ReadLables(password, epc.length, epc, memoryBank, address,
					dataLength);
		}
		// reader.m_strPCEPC = "";
	}

	@Override
	public void close() {

		reader.StopLoop();
		reader.m_handler = null;
		android.hardware.uhf.magic.reader.Close();
	}

	static private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what != 0) {
				if (m_strresult.indexOf((String) msg.obj) < 0) {
					// Log.e("8888888888",(String)msg.obj+"\r\n");
					m_strresult += (String) msg.obj;
					// Toast.makeText(mActivity.getApplicationContext(), "Код: "
					// + m_strresult, Toast.LENGTH_LONG).show();
					// m_strresult+="\r\n";
					// возврат при чтении метки пользователя
					if (types == READ_USER_LABLE || types == 0) {
						// m_strresult = "01234567";
						reader.StopLoop();

						// ((RFIDActivity) mActivity).Callback(m_strresult);
						Message message = new Message();
						message.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
						Bundle bundle = new Bundle();
						bundle.putString(RfidDriverBase.RESULT_RFID_TAG_ID,
								m_strresult);
						message.setData(bundle);
						mHandler.sendMessage(message);
					}
					// возврат при чтении метки оборудования
					if (types == READ_EQUIPMENT_LABLE_ID
							|| types == READ_EQUIPMENT_OPERATION_LABLE_ID) {
						reader.m_strPCEPC = m_strresult;
						mPCEPC = m_strresult;
						if (types == READ_EQUIPMENT_LABLE_ID) {
							// ((EquipmentInfoActivity)
							// mActivity).CallbackOnReadLable(m_strresult);
						}
						if (types == READ_EQUIPMENT_OPERATION_LABLE_ID) {
							// ((OperationActivity)
							// mActivity).CallbackOnReadLable(m_strresult);
						}
					}
					// возврат при чтении памяти оборудования
					if (types == READ_EQUIPMENT_MEMORY
							|| types == READ_EQUIPMENT_OPERATION_MEMORY) {
						if (mPCEPC != null && !mPCEPC.equals("")) {
							if (types == READ_EQUIPMENT_MEMORY) {
								// ((EquipmentInfoActivity)
								// mActivity).Callback(m_strresult);
							}
							if (types == READ_EQUIPMENT_OPERATION_MEMORY) {
								// ((OperationActivity)
								// mActivity).Callback(m_strresult);
							}
						}
					}
					// возврат при записи памяти оборудования
					if (types == WRITE_EQUIPMENT_OPERATION_MEMORY) {
						// ((OperationActivity)
						// mActivity).CallbackOnWrite(m_strresult);
					}
					// возврат при записи памяти оборудования
					if (types == WRITE_EQUIPMENT_MEMORY
							|| types == WRITE_USER_MEMORY) {
						// ((EquipmentInfoActivity)
						// mActivity).CallbackOnWrite(m_strresult);
					}
					// m_strresult="";
				}
				m_nCount++;
				// Log.e("8888888888",m_nCount+"\r\n");
				if (m_nCount > 65534) {
					m_nCount = 0;
				}
				// android.hardware.uhf.magic.reader.StopLoop();
			}

		}
	};

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		return null;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			byte[] data) {

		if (types == WRITE_EQUIPMENT_OPERATION_MEMORY
				|| types == WRITE_EQUIPMENT_MEMORY
				|| types == WRITE_USER_MEMORY) {
			// byte[] epc = reader.stringToBytes(reader.m_strPCEPC);
			byte[] epc = reader.stringToBytes(mPCEPC);
			// TODO старый вариант
			// byte memoryBank = USER_MEMORY_BANK; // user memory
			// TODO старый вариант
			// int address = 0; // пишем буфер с начала
			// int dataLength = 32; // длина памяти данных
			byte[] dataForWrite = new byte[50];
			// int dataLength = Integer.valueOf(outBuffer.length) * 2;
			// TODO старый вариант
			// String passwordString = "00000000"; // пароль
			// TODO старый вариант
			// byte[] password = reader.stringToBytes(passwordString);

			int rc = reader.Writelables(password.getBytes(), epc.length, epc,
					(byte) memoryBank, (byte) address, (byte) 24, data);
			System.arraycopy(data, 24, dataForWrite, 0, 24);

			rc = reader.Writelables(password.getBytes(), epc.length, epc,
					(byte) memoryBank, (byte) address + 48, (byte) 12,
					dataForWrite);
			/*
			 * int rc = reader.Writelables(password, epc.length, epc,
			 * memoryBank, (byte) address, (byte) realDataLength, dataForWrite);
			 */
			Message message = new Message();
			if (rc >= 0) {
				message.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
			} else {
				message.arg1 = RfidDriverBase.RESULT_RFID_WRITE_ERROR;
			}
			mHandler.sendMessage(message);
		}
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, byte[] data) {

	}

}
