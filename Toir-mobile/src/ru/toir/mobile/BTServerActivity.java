package ru.toir.mobile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BTServerActivity extends Activity {

	private static final String TAG = "BTServerActivity";
	BluetoothAdapter adapter;
	Button startServerButton;
	BroadcastReceiver btChangeStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btserver);

		// регистрируем обработчик сообщений изменения состояния
		// адаптера блютус
		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		if (btChangeStateReceiver == null) {
			btChangeStateReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					Bundle extra = intent.getExtras();
					int state = extra.getInt(BluetoothAdapter.EXTRA_STATE);
					if (state == BluetoothAdapter.STATE_OFF) {
						Log.d(TAG, "BT Off");
						// видимо если сервер был запущен, его нужно прибить,
						// если этого еще раньше не произошло в виду
						// изчезновения открытого сокета
					} else if (state == BluetoothAdapter.STATE_ON) {
						Log.d(TAG, "BT On");
						// unregisterReceiver(btChangeStateReceiver);
						// TODO запускаем поток ожидания соединения от
						// клиента
						adapter.cancelDiscovery();
						Log.d(TAG, "Запускаем сервер...");
					}
				}
			};
		}
		registerReceiver(btChangeStateReceiver, filter);

		startServerButton = (Button) findViewById(R.id.startBTServerButton);
		startServerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (adapter != null) {
					// проверяем что адаптер находится в состоянии доступности
					int scanMode = adapter.getScanMode();
					switch (scanMode) {
					case BluetoothAdapter.SCAN_MODE_NONE:
						Log.d(TAG, "SCAN_MODE_NONE");
						// просим пользователя включить блютус
						Intent i = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivity(i);
						break;

					case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
						Log.d(TAG, "SCAN_MODE_CONNECTABLE");
						Log.d(TAG, "Запускаем сервер...");
						adapter.cancelDiscovery();
						// TODO запускаем поток ожидания соединения от клиента
						break;

					case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
						Log.d(TAG, "SCAN_MODE_CONNECTABLE_DISCOVERABLE");
						adapter.cancelDiscovery();
						// TODO запускаем поток ожидания соединения от клиента
						break;
					}
				}
			}
		});

		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			startServerButton.setEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

}
