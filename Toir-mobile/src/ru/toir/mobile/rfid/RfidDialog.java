/**
 * 
 */
package ru.toir.mobile.rfid;

import java.lang.reflect.Constructor;
import com.google.zxing.integration.android.IntentIntegrator;
import ru.toir.mobile.R;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Dmitriy Logachov
 * 
 */
public class RfidDialog extends DialogFragment {

	public static final int READER_COMMAND_READ_ID = 1;
	public static final int READER_COMMAND_READ_DATA = 2;
	public static final int READER_COMMAND_READ_DATA_ID = 3;
	public static final int READER_COMMAND_WRITE_DATA = 4;
	public static final int READER_COMMAND_WRITE_DATA_ID = 5;

	private String TAG = "RfidDialog";
	private String driverClassName;
	private Class<?> driverClass;
	private RfidDriverBase driver;

	private Context mContext;

	/*
	 * обработчик передаётся в драйвер, в том числе используется для отправки
	 * сообщений об ошибках если драйвер не удаётся запустить
	 */
	private Handler mHandler;

	public RfidDialog(Context context, Handler handler) {

		mContext = context;
		mHandler = handler;

		// получаем текущий драйвер считывателя
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		driverClassName = sp.getString(mContext.getString(R.string.RFIDDriver),
				"RFIDDriverNull");

		// пытаемся получить класс драйвера
		try {
			driverClass = Class.forName("ru.toir.mobile.rfid.driver."
					+ driverClassName);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.toString());
			Message message = new Message();
			message.arg1 = RfidDriverBase.RESULT_RFID_CLASS_NOT_FOUND;
			mHandler.sendMessage(message);

		}

		// пытаемся создать объект драйвера
		try {
			Constructor<?> c = driverClass.getConstructor(DialogFragment.class,
					Handler.class);
			driver = (RfidDriverBase) c.newInstance(this, handler);
		} catch (Exception e) {
			e.printStackTrace();
			Message message = new Message();
			message.arg1 = RfidDriverBase.RESULT_RFID_CLASS_NOT_FOUND;
			mHandler.sendMessage(message);
		}

		// инициализируем драйвер
		if (!driver.init((byte) 0)) {
			Message message = new Message();
			message.arg1 = RfidDriverBase.RESULT_RFID_INIT_ERROR;
			mHandler.sendMessage(message);

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getDialog().setTitle("Поднесите метку");

		View view = driver.getView(inflater, viewGroup);

		// TODO пересмотреть алгоритм, т.к. нужно еще и писать в метку, и в
		// разные области, и читать из разных областей
		driver.readTagId((byte) 0);

		return view;
	}

	public void readTagId() {

		// while(rfid != null) {
		// Log.d(TAG, "ждём считыватель...");
		// }
		// rfid.read((byte) 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DialogFragment#onStart()
	 */
	@Override
	public void onStart() {

		super.onStart();
		// rfid.read((byte) 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		Message message = new Message();
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			if (data != null) {
				String result = data.getStringExtra("SCAN_RESULT");
				if (result != null && !result.equals("")) {
					message.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
					Bundle bundle = new Bundle();
					bundle.putString(RfidDriverBase.RESULT_RFID_TAG_ID, result);
					message.setData(bundle);
				} else {
					message.arg1 = RfidDriverBase.RESULT_RFID_READ_ERROR;
				}
			} else {
				message.arg1 = RfidDriverBase.RESULT_RFID_CANCEL;
			}
			break;
		default:
			message.arg1 = RfidDriverBase.RESULT_RFID_CANCEL;
			break;
		}
		mHandler.sendMessage(message);
	}

}
