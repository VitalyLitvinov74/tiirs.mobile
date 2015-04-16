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
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import ru.toir.mobile.rfid.RFID;
import android.widget.AdapterView;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends Activity {
	ListView lv;
	private static final String TAG = "MainActivity";

	private static final int RETURN_CODE_READ_RFID = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// создаём базу данных, в качестве контекста передаём свой, с переопределёнными путями к базе 
		TOiRDBAdapter adapter = new TOiRDBAdapter(new TOiRDatabaseContext(getApplicationContext()));	
		adapter.open();
		Log.d("test", "db.version=" + adapter.getDbVersion());
		// эту проверку необходимо перенести в более подходящее место
		// чтоб в конечном итоге пользователю предлогалось обновить приложение при расхождении версий
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
		
		lv = (ListView)findViewById(R.id.usersListView);
		lv.setAdapter(new UsersDBAdapter(new TOiRDatabaseContext(getApplicationContext())));

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "position=" + position + ", id=" + id);

				TableRow tr = (TableRow) view;
				Log.d(TAG, ((TextView)tr.getChildAt(1)).getText().toString());
				
				/*
				UsersDBAdapter udba = (UsersDBAdapter)parent.getAdapter();
				Users iuser = new Users(0, "jaga", "san", "fuck", 777);
				udba.insertUsers(iuser);
				*/
			}
		});
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
	
	/**
	 * Обработчик клика в главную активность приложения 
	 * @param view
	 */
	public void onClick(View view) {
		Intent rfidRead = new Intent(this, RFIDActivity.class);
		startActivityForResult(rfidRead, RETURN_CODE_READ_RFID);
	}
	
	/**
	 * Обработчик клика меню обновления приложения
	 * @param view
	 */
	public void onActionUpdate(MenuItem menuItem) {

		String fileName = "file:///storage/sdcard0/Download/Toir-mobile.1.0.apk";
		//String fileName = "http://apkupdate.lan/download.php";
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		//Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
		intent.setDataAndType(Uri.parse(fileName), "application/vnd.android.package-archive");
		//intent.setData(Uri.parse(fileName));

		startActivity(intent);
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
		}else if(id == R.id.action_update) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
