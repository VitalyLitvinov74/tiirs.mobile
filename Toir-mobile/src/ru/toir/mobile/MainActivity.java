package ru.toir.mobile;

import android.support.v7.app.ActionBarActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

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
			
			Cursor c = adapter.getUsers(1);
			if(c.moveToFirst()){
				Log.d("test", c.getString(Users.NAME_COLUMN));
			}
			long id = adapter.insertUsers("demon", "demonlogin", "demonpass", 666);
			if(id !=-1 ){
				Log.d("test", "id пользоватея = " + id);
				int ucount = adapter.updateUsers(id, "1", "2", "3", 4);
				if(ucount == 1){
					int dcount = adapter.deleteUsers(id);
					if(dcount == 1){
						Log.d("test", "пользователь удалён");
					}else {
						Log.d("test", "пользователь не удалён!");
					}
				}else{
					Log.d("test", "пользователь не обновлён!");
				}
				
			}else {
				Log.d("test", "пользователь не создан!");
			}
		}

		adapter.close();
		
		TOiRServerAPI toirServerApi = new TOiRServerAPI(getApplicationContext());
		Toast toast = Toast.makeText(this, "SERVER API = " + toirServerApi.getVersion(), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

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
