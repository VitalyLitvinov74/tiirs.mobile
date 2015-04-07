package ru.toir.mobile;

import ru.toir.mobile.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.driver.RFIDDriver;

/**
 * 
 */

/**
 * @author koputo
 *
 */
public class RFIDActivity extends Activity {
	private String driverClassName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rfid_read);
		
		// по умолчанию ставим "текcтовый" драйвер
		driverClassName = "RFIDDriverText";
	}
	
	public void readTagOnClick(View view){
		Class<?> driverClass;
		RFIDDriver driver;
		try {
			driverClass = Class.forName("ru.toir.mobile.rfid.driver." + driverClassName);
			try{
				driver = (RFIDDriver)driverClass.newInstance();
				RFID rfid = new RFID(driver);
				String tagData = rfid.read();
				Intent data = new Intent();
				if(tagData == null){
					setResult(RFID.RESULT_RFID_READ_ERROR);	
				}else {
					data.setData(Uri.parse(tagData));
					setResult(RESULT_OK, data);
				}
			}catch(Exception e){
				Log.d("test", e.toString());
			}
		}catch(ClassNotFoundException e){
			Log.d("test", "Класс драйвера не найден");
		}
		finish();
	}
	
	public void cancelOnClick(View view){
		setResult(RESULT_CANCELED);
		finish();
	}

}
