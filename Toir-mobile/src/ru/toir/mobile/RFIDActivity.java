package ru.toir.mobile;

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
import ru.toir.mobile.rfid.driver.RFIDDriver;
import ru.toir.mobile.rfid.driver.TOIRCallback;

/**
 * @author Dmitriy Logachov
 *
 */
public class RFIDActivity extends Activity {
	
	private String TAG = "RFIDActivity";
	private String driverClassName;
	private Class<?> driverClass;
	private RFIDDriver driver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.rfid_read);
		
		// получаем текущий драйвер считывателя
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		driverClassName = sp.getString("RFIDDriver", "RFIDDriverNull");

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
		
		// запускаем процедуру считывания
		RFID rfid = new RFID(driver);
		
		// объект функция которого будет вызвана когда данные будут прочитаны
		TOIRCallback tagReadCallback = new TOIRCallback() {
			@Override
			public void Callback(String result) {
				Intent data = new Intent();
				if(result == null){
					setResult(RFID.RESULT_RFID_READ_ERROR);	
				} else {
					data.setData(Uri.parse(result));
					setResult(RESULT_OK, data);
				}
				finish();
			}
		};
		
		// инициализируем драйвер
		if (rfid.init(tagReadCallback)) {
			rfid.read();
		} else {
			setResult(RFID.RESULT_RFID_INIT_ERROR);
			finish();
		}
		
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
}
