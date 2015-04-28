package ru.toir.mobile;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.rfid.RFID;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private static final int RETURN_CODE_READ_RFID = 1;
	private boolean isLogged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Log.d(TAG, "onCreate:before read: isLogged=" + isLogged);
		if ( savedInstanceState != null ) {
			isLogged = savedInstanceState.getBoolean("isLogged");
			Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
		}
		
		if (isLogged) {
			setContentView(R.layout.main_layout);
		} else {
			setContentView(R.layout.login_layout);
		}
		
		// инициализация приложения
		init();
		
	}

	/**
	 * Инициализация приложения при запуске
	 */
	public void init() {
		if (!initDB()) {
			// принудительное обновление приложения
		}
	}
	
	public boolean initDB() {
		boolean success = false;
		// создаём базу данных, в качестве контекста передаём свой, с переопределёнными путями к базе 
		TOiRDBAdapter adapter = new TOiRDBAdapter(new TOiRDatabaseContext(getApplicationContext()));	
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
			success = true;
		}
		adapter.close();
		adapter = null;
		return success;
	}
	
	/**
	 * 
	 */
	public void startAuthorise() {
		isLogged = false;
		Intent rfidRead = new Intent(this, RFIDActivity.class);
		startActivityForResult(rfidRead, RETURN_CODE_READ_RFID);
	}
	
	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String msg = null;
		Uri tagData = null;
		
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RETURN_CODE_READ_RFID:
			if (resultCode == RESULT_OK) {
				tagData = data.getData();
				if (tagData == null) {
					msg = "Данные не получены!";
				} else {
					Log.d(TAG, "Прочитаны данные из метки: " + tagData.toString());
				}
			} else if (resultCode == RESULT_CANCELED) {
				msg = "Чтение метки отменено пользователем!";
			} else if(resultCode == RFID.RESULT_RFID_READ_ERROR) {
				msg = "Не удалось прочитать содержимое метки!";
			} else if(resultCode == RFID.RESULT_RFID_INIT_ERROR) {
				msg = "Не удалось инициализировать драйвер считывателя!";
			} else if(resultCode == RFID.RESULT_RFID_CLASS_NOT_FOUND) {
				msg = "Не найден драйвер считывателя!";
			}
			break;

		default:
			msg = "Не известный код возврата.";
			break;
		}
		
		// что-то пошло не так при считывании метки
		if (msg != null) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		} else {
			// проверяем наличие пользователя в локальной базе
			String tagId = tagData.toString();
			UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			Users user = users.getUserByTagId(tagId);
			if (user == null) {
				Toast.makeText(this, "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
			} else {
				Log.d(TAG, user.toString());
				isLogged = true;
				setContentView(R.layout.main_layout);
			}
		}
	}
	
	/**
	 * Обработчик клика в главную активность приложения 
	 * @param view
	 */
	public void onClickLogin(View view) {
		Intent rfidRead = new Intent(this, RFIDActivity.class);
		startActivityForResult(rfidRead, RETURN_CODE_READ_RFID);
	}
	
	/**
	 * Обработчик клика меню обновления приложения
	 * @param view
	 */
	public void onActionUpdate(MenuItem menuItem) {
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		// урл по которому забираем файл обновления
		String updateUrl = sp.getString("updateUrl", "");
		
		if (updateUrl.equals("")) {
			Toast toast = Toast.makeText(this, "Не указан URL для обновления!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		String path = Environment.getExternalStorageDirectory() + "/Download/";
		File file = new File(path);
		file.mkdirs();
		File outputFile = new File(path, "Toir-mobile.apk");

		Downloader d = new Downloader(MainActivity.this);
		d.execute(updateUrl, outputFile.toString());
	}
	
	public void onActionSettings(MenuItem menuItem) {
		Log.d(TAG, "onActionSettings");
		Intent i = new Intent(MainActivity.this, TOiRPreferences.class);
		startActivity(i);
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	/*
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isLogged = savedInstanceState.getBoolean("isLogged");
		Log.d(TAG, "onRestoreInstanceState: isLogged=" + isLogged);
	}
	*/
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState: isLogged=" + isLogged);
		outState.putBoolean("isLogged", isLogged);
	}
}
