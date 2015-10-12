package ru.toir.mobile;

import com.google.zxing.integration.android.IntentIntegrator;

import ru.toir.mobile.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.UserTagStructure;
import ru.toir.mobile.rfid.driver.RFIDDriver;
import ru.toir.mobile.rfid.driver.RFIDDriverC5;
/**
 * @author Dmitriy Logachov
 *
 */
public class RFIDActivity extends Activity {
	
	private String TAG = "RFIDActivity";
	private String driverClassName;
	private Class<?> driverClass;
	private RFIDDriver driver;
	private static RFID rfid;

	UserTagStructure usertag;
	
	@Override
	protected void onStart() {
		super.onStart();
		// запускаем процедуру считывания
		rfid.read((byte)RFIDDriverC5.READ_USER_LABLE);	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);		
		
		// получаем текущий драйвер считывателя
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		driverClassName = sp.getString(getString(R.string.RFIDDriver), "RFIDDriverNull");

		setContentView(R.layout.rfid_read);
		
		// пытаемся получить класс драйвера
		try {
			driverClass = Class.forName("ru.toir.mobile.rfid.driver." + driverClassName);
		}catch(ClassNotFoundException e){
			Log.e(TAG, e.toString());
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			finish();
		}
		
		// пытаемся создать объект драйвера
		try{
			driver = (RFIDDriver)driverClass.newInstance();
		}catch(InstantiationException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			e.printStackTrace();
			finish();
		}catch(IllegalAccessException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			e.printStackTrace();
			finish();
		}
		
		rfid = new RFID(driver);
		rfid.setActivity(this);

		// инициализируем драйвер
		if (!rfid.init((byte)RFIDDriverC5.READ_USER_LABLE)) {
			setResult(RFID.RESULT_RFID_INIT_ERROR);
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			if (data != null) {
				String result = data.getStringExtra("SCAN_RESULT");
				if (result != null && !result.equals("")) {
					Intent i = new Intent();
					i.setData(Uri.parse(result));
					i.putExtras(getIntent().getExtras());
					setResult(RESULT_OK, i);
				} else {
					setResult(RFID.RESULT_RFID_READ_ERROR);
				}
			} else {
				setResult(RESULT_CANCELED);
			}
			//rfid.close();
			break;
		case RESULT_CANCELED:
			setResult(RESULT_CANCELED);
			break;
		default:
			setResult(RESULT_CANCELED);
			break;
		}
		finish();
	}
	
	public void cancelOnClick(View view){
		setResult(RESULT_CANCELED);
		finish();
	}

	/* 
	 * Элементы меню запрашиваются из драйвера считывателя
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		driver.getMenu(menu);
		return true;
	}
	
	public void Callback(String result) {
		Intent data = null;
		if(result == null){
			setResult(RFID.RESULT_RFID_READ_ERROR);	
		} else {
			data = new Intent();
			// TODO parse data from tag
			data.setData(Uri.parse(result));
			data.putExtras(getIntent().getExtras());
			setResult(RESULT_OK, data);
		}
		finish();
	}
}
