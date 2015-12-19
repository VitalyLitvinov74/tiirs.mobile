package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;

/**
 * @author olejek
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RfidDriverBarcode2D extends RfidDriverBase implements IRfidDriver {
	public static final String DRIVER_NAME = "Драйвер лазерного считывателя штрихкодов";
	private final static String TAG = "RfidDriverBarcode2D";
	private Handler c5Handler;

	// view в котором будет текстовое поле,
	// в которое будет помещен распознанный код
	private static View driverView;

	public RfidDriverBarcode2D(Handler handler) {
		super(handler);
	}

	public RfidDriverBarcode2D(Handler handler, Activity activity) {
		super(handler);
	}

	public RfidDriverBarcode2D(Handler handler, DialogFragment dialogFragment) {
		super(handler);
	}

	private static class MainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Scanner.BARCODE_READ: {
				String tagId;
				if (!((String) msg.obj).equals("")) {
					tagId = (String) msg.obj;
				} else {
					if (driverView != null) {
						EditText ed = (EditText) driverView
								.findViewById(R.id.catch2bbarcode);
						if (ed != null) {
							tagId = ed.getText().toString();
						} else {
							tagId = "";
						}
					} else {
						tagId = "";
					}
				}

				Log.d(TAG, tagId);
				sHandler.obtainMessage(RESULT_RFID_SUCCESS, tagId)
						.sendToTarget();
				break;
			}
			case Scanner.BARCODE_NOREAD: {
				sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
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
		c5Handler = new MainHandler();
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
		driverView = inflater.inflate(R.layout.bar2d_read, viewGroup);

		// инициализируем текстовое поле ввода в которое по нажатии железной
		// кнопки будет введено считанное значение
		EditText ed = (EditText) driverView.findViewById(R.id.catch2bbarcode);
		if (ed != null) {
			ed.requestFocus();
			ed.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					sHandler.obtainMessage(RESULT_RFID_SUCCESS, s.toString())
							.sendToTarget();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			Button button = (Button) driverView
					.findViewById(R.id.cancelBar2DScan);
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					sHandler.obtainMessage(RESULT_RFID_CANCEL).sendToTarget();
				}
			});
		}

		return driverView;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {
		sHandler.obtainMessage(RESULT_RFID_READ_ERROR).sendToTarget();
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {
		sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {
		sHandler.obtainMessage(RESULT_RFID_WRITE_ERROR).sendToTarget();
	}

}
