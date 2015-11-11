package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
import android.hardware.barcode.Scanner;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * @author olejek
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RfidDriverBarcode2D extends RfidDriverBase implements IRfidDriver {

	private final static String TAG = "RfidDriverBarcode2D";
	private Handler c5Handler = new MainHandler();

	public RfidDriverBarcode2D(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	static private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Scanner.BARCODE_READ: {
				String tagId;
				if (!((String) msg.obj).equals("")) {
					tagId = (String) msg.obj;
				} else {
					EditText ed = (EditText) mDialogFragment.getView()
							.findViewById(R.id.catch2bbarcode);
					tagId = ed.getText().toString();
				}
				Log.d(TAG, tagId);
				Message message = new Message();
				message.what = RESULT_RFID_SUCCESS;
				message.obj = tagId;
				mHandler.sendMessage(message);
				break;
			}
			case Scanner.BARCODE_NOREAD: {
				Message message = new Message();
				message.what = RESULT_RFID_READ_ERROR;
				mHandler.sendMessage(message);
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public boolean init() {

		// initialize the scanner
		Scanner.InitSCA();

		Scanner.m_handler = c5Handler;

		return true;
	}

	@Override
	public void readTagId() {

		Scanner.ReadHW();
	}

	@Override
	public void close() {

	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		View view = inflater.inflate(R.layout.bar2d_read, viewGroup);
		// инициализируем текстовое поле ввода в которое по нажатии железной
		// кнопки будет введено считанное значение
		EditText ed = (EditText) view.findViewById(R.id.catch2bbarcode);
		ed.requestFocus();
		ed.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				Message message = new Message();
				message.what = RESULT_RFID_SUCCESS;
				message.obj = s.toString();
				mHandler.sendMessage(message);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		return view;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		Message message = new Message();
		message.what = RESULT_RFID_READ_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {

		Message message = new Message();
		message.what = RESULT_RFID_WRITE_ERROR;
		mHandler.sendMessage(message);
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

		Message message = new Message();
		message.what = RESULT_RFID_WRITE_ERROR;
		mHandler.sendMessage(message);
	}

}
