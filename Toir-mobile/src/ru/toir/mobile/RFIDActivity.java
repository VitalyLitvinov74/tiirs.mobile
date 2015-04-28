package ru.toir.mobile;

import ru.toir.mobile.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.driver.RFIDDriver;

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
	
	}
	
	public void readTagOnClick(View view){
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		driverClassName = sp.getString("RFIDDriver", "RFIDDriverNull");
		
		try {
			driverClass = Class.forName("ru.toir.mobile.rfid.driver." + driverClassName);
		}catch(ClassNotFoundException e){
			Log.e(TAG, e.toString());
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			finish();
			return;
		}
		
		try{
			driver = (RFIDDriver)driverClass.newInstance();
			RFID rfid = new RFID(driver);
			if (rfid.init()) {
				String tagData = rfid.read();
				Intent data = new Intent();
				if(tagData == null){
					setResult(RFID.RESULT_RFID_READ_ERROR);	
				}else {
					data.setData(Uri.parse(tagData));
					setResult(RESULT_OK, data);
				}
			} else {
				setResult(RFID.RESULT_RFID_INIT_ERROR);
			}
		}catch(InstantiationException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			Log.e(TAG, e.toString());
		}catch(IllegalAccessException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			Log.e(TAG, e.toString());
		}
		finish();
	}
	
	public void cancelOnClick(View view){
		setResult(RESULT_CANCELED);
		finish();
	}
}
