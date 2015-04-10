package ru.toir.mobile;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

			Equipment equip = new Equipment(adapter);
			Long	equip_id=0L;
			equip_id=equip.insertEquipment("Метран 300-ПР","doc/metran300pr.pdf",105,2013,"Метран",null,5);
			Log.d("test", "id оборудования = " + equip_id);
			Toast toast2 = Toast.makeText(this, "Id оборудования="+ equip_id, Toast.LENGTH_SHORT);
			toast2.setGravity(Gravity.CENTER, 0, 0);
			toast2.show();
		}else {
			Toast toast = Toast.makeText(this, "База данных актуальна!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			//equip.deleteEquipment(equip_id);			
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
