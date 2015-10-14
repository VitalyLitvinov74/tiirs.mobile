package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
import android.hardware.barcode.Scanner;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author olejek
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RfidDriverBarcode2D extends RfidDriverBase implements IRfidDriver {

	private Handler c5Handler = new MainHandler();
	static private TextView scanText;

	public RfidDriverBarcode2D(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	static private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Scanner.BARCODE_READ: {
				// Toast.makeText(mActivity.getApplicationContext(),"Код: " +
				// msg.obj, Toast.LENGTH_LONG).show();
				scanText.setText((String) msg.obj);
				// temporary
				msg.obj = "01234567";

				// ((RFIDActivity) mActivity).Callback((String) msg.obj);
				Message message = new Message();
				message.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
				Bundle bundle = new Bundle();
				bundle.putString(RfidDriverBase.RESULT_RFID_TAG_ID, (String) msg.obj);
				message.setData(bundle);
				mHandler.sendMessage(message);
				break;
			}
			case Scanner.BARCODE_NOREAD: {
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public boolean init(byte type) {
		Scanner.m_handler = c5Handler;
		return true;
	}

	@Override
	public void readTagId(byte type) {
		// запускаем отдельную задачу для считывания метки
		// while (attempt<MAX_ATTEMPT_TO_SCAN)
		Scanner.Read();
	}

	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		// initialize the scanner
		Scanner.InitSCA();
		View view = inflater.inflate(R.layout.bar2d_read, viewGroup);
		scanText = (TextView) view.findViewById(R.id.code_from_bar);

		return view;
	}

}
