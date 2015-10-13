/**
 * 
 */
package ru.toir.mobile.rfid;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.driver.RFIDDriver;
import android.app.DialogFragment;
import android.content.Context;
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

	private String TAG = "RfidDialog";
	private String driverClassName;
	private Class<?> driverClass;
	private RFIDDriver driver;
	private RFID rfid;
	private Context mContext;
	private Handler mHandler;

	public RfidDialog(Context context) {

		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getDialog().setTitle("Текстовый драйвер: считывание метки");

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
			message.arg1 = RFID.RESULT_RFID_CLASS_NOT_FOUND;
			mHandler.sendMessage(message);

		}

		// пытаемся создать объект драйвера
		try {
			driver = (RFIDDriver) driverClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			Message message = new Message();
			message.arg1 = RFID.RESULT_RFID_CLASS_NOT_FOUND;
			mHandler.sendMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			Message message = new Message();
			message.arg1 = RFID.RESULT_RFID_CLASS_NOT_FOUND;
			mHandler.sendMessage(message);
		}

		rfid = new RFID(driver);

		// это нужно было для создания внутри драйвера элементов интерфейса
		// специфических для каждого драйвера
		// rfid.setActivity(this);

		View view = rfid.getView(inflater, viewGroup);

		// инициализируем драйвер
		if (!rfid.init((byte) 0)) {
			Message message = new Message();
			message.arg1 = RFID.RESULT_RFID_INIT_ERROR;
			mHandler.sendMessage(message);

		}
		
		rfid.setHandler(mHandler);

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DialogFragment#onStart()
	 */
	@Override
	public void onStart() {

		super.onStart();
		//rfid.read((byte) 0);
	}

	/**
	 * @return the mhHandler
	 */
	public Handler getHandler() {
		return mHandler;
	}

	/**
	 * @param mhHandler the mhHandler to set
	 */
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

}
