package ru.toir.mobile;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.driver.RFIDDriverText;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends Activity {
	private static final int RETURN_CODE_READ_RFID = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TOiRDBAdapter adapter = new TOiRDBAdapter(getApplicationContext());	
		adapter.open();
	
		Log.d("test", "db.version=" + adapter.getDbVersion());

		if(!adapter.isActual()){
			Toast toast = Toast.makeText(this, "База данных не актуальна!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else {
			Toast toast = Toast.makeText(this, "База данных актуальна!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		adapter.close();
		
		TOiRServerAPI toirServerApi = new TOiRServerAPI(getApplicationContext());
		Toast toast = Toast.makeText(this, "SERVER API = " + toirServerApi.getVersion(), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		
		RFID rfid = new RFID(new RFIDDriverText());
		if(rfid.init()){
			String rfidData= rfid.read();
			if(rfidData == null){
				Log.d("test", "Не удалось прочитать содержимое метки!");
			}else{
				Log.d("test", "Содержимое метки: " + rfidData);
			}
			rfid.close();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RETURN_CODE_READ_RFID:
			if (resultCode == RESULT_OK) {
				Uri tagData = data.getData();
				Log.d("test", "Прочитаны данные из метки: " + tagData.toString());
			}
			else if (resultCode == RESULT_CANCELED) {
				Log.d("test", "Чтение метки отменено пользователем!");
			}
			else if(resultCode == RFID.RESULT_RFID_READ_ERROR) {
				Log.d("test", "Не удалось прочитать содержимое метки!");
			}
			break;

		default:
			break;
		}
	}
	
	public void onClick(View view) {
		Intent rfidRead = new Intent(this, RFIDActivity.class);
		startActivityForResult(rfidRead, RETURN_CODE_READ_RFID);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
