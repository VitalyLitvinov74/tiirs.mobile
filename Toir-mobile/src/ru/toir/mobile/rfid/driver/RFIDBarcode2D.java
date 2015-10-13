package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.ActivityInfo;
import android.hardware.barcode.Scanner;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author olejek
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RFIDBarcode2D implements RFIDDriver {

	private Handler mHandler = new MainHandler();
	private static Activity mActivity;
	static private TextView scanText;

	static private class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Scanner.BARCODE_READ: {
				Toast.makeText(mActivity.getApplicationContext(),
						"Код: " + msg.obj, Toast.LENGTH_LONG).show();
				scanText.setText((String) msg.obj);
				// temporary
				msg.obj = "01234567";
				((RFIDActivity) mActivity).Callback((String) msg.obj);
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
	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	/**
	 * <p>
	 * Инициализируем драйвер
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean init(byte type) {
		Scanner.m_handler = mHandler;
		// initialize the scanner
		Scanner.InitSCA();
		mActivity.setContentView(R.layout.bar2d_read);
		mActivity
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		scanText = (TextView) mActivity.findViewById(R.id.code_from_bar);
		return true;
	}

	/**
	 * <p>
	 * Считываем метку
	 * </p>
	 * <p>
	 * Здесь нужно запустить отдельную задачу в которой пытаемся считать метку
	 * </p>
	 * <p>
	 * Расчитано на вызов метода Callback() объекта {@link TOIRCallback} в
	 * onPostExecute() и onCancelled() объекта {@link AsyncTask}
	 * </p>
	 */
	@Override
	public void read(byte type) {
		// запускаем отдельную задачу для считывания метки
		// while (attempt<MAX_ATTEMPT_TO_SCAN)
		Scanner.Read();
	}

	/**
	 * <p>
	 * Записываем в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	/**
	 * <p>
	 * Завершаем работу драйвера
	 * </p>
	 */
	@Override
	public void close() {
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {

		return true;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#getView(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		return null;
	}

	/* (non-Javadoc)
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setHandler(android.os.Handler)
	 */
	@Override
	public void setHandler(Handler handler) {

	}

}
