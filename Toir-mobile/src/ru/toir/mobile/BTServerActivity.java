package ru.toir.mobile;

import ru.toir.mobile.bluetooth.ServerListener;
import ru.toir.mobile.rfid.RfidDialog;
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
import android.widget.TextView;

public class BTServerActivity extends Activity {

	private static final String TAG = "BTServerActivity";
	private static final int BT_ENABLE_REQUEST_CODE = 666;

	public static final String BT_SERVER_UUID = "E8627152-8F74-460B-B31E-A879194BB431";
	public static final String BT_SERVICE_RECORD_NAME = "ToirBTServer";
	public static final String SERVER_STATE_ACTION = "ru.toir.mobile.btserver.state";
	public static final String SERVER_STATE_PARAM = "state";

	public static final int SERVER_STATE_STOPED = 1;
	public static final int SERVER_STATE_WAITING_CONNECTION = 2;
	public static final int SERVER_STATE_CONNECTED = 3;
	public static final int SERVER_STATE_DISCONNECTED = 4;

	private Button startServerButton;
	private Button stopServerButton;
	private TextView serverStatusTextView;

	private IntentFilter btChangeStateFilter;
	private BroadcastReceiver btChangeStateReceiver;

	private IntentFilter serverStateFilter;
	private BroadcastReceiver serverStateReceiver;

	// объект отвечающий за ожидание входящего соединения от клиента
	private ServerListener serverListener;

	private RfidDialog rfidDialog;

	// флаг необходимости запуска сервера, в ситуации когда адаптер находится в
	// промежуточных состояниях
	private boolean needStart;

	public BTServerActivity() {

		needStart = false;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_btserver);

		// создаём приёмник для сообщений о изменении состояния сервера
		serverStateFilter = new IntentFilter(SERVER_STATE_ACTION);
		serverStateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				int state = intent.getIntExtra(SERVER_STATE_PARAM, 0);

				switch (state) {
				case SERVER_STATE_STOPED:
					serverStatusTextView.setText("Остановлен...");
					startServerButton.setEnabled(true);
					stopServerButton.setEnabled(false);
					break;
				case SERVER_STATE_WAITING_CONNECTION:
					serverStatusTextView
							.setText("Ожидание входящего соединения от клиента...");
					startServerButton.setEnabled(false);
					stopServerButton.setEnabled(true);
					break;
				case SERVER_STATE_CONNECTED:
					serverStatusTextView.setText("Соединение установленно...");
					startServerButton.setEnabled(false);
					stopServerButton.setEnabled(false);
					break;
				case SERVER_STATE_DISCONNECTED:
					serverStatusTextView.setText("Клиент отключился...");
					startServerButton.setEnabled(true);
					stopServerButton.setEnabled(false);
					startServerListener();
					break;
				default:
					break;
				}
			}
		};

		// создаём приёмник для обработки сообщений о изменении состояния
		// адаптера блютус
		btChangeStateFilter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		btChangeStateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				Bundle extra = intent.getExtras();
				int state = extra.getInt(BluetoothAdapter.EXTRA_STATE);

				switch (state) {
				case BluetoothAdapter.STATE_ON:
					Log.d(TAG, "BT On");

					if (needStart) {
						stopServerListener();

						// запускаем поток с ожиданием подключения от клиента
						serverListener = new ServerListener(
								getApplicationContext());
						serverListener.start();
						needStart = false;
					}

					break;

				case BluetoothAdapter.STATE_OFF:
					Log.d(TAG, "BT Off");

					stopServerListener();

					if (needStart) {
						// просим включить блютус
						Intent intentStartBT = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(intentStartBT,
								BT_ENABLE_REQUEST_CODE);
					}

					break;
				}
			}
		};

		// обработчик кнопки запуска сервера
		Button testButton = (Button) findViewById(R.id.testBTServerButton);
		testButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				serverListener.test();
			}
		});


		
		// обработчик кнопки запуска сервера
		startServerButton = (Button) findViewById(R.id.startBTServerButton);
		startServerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "Запускаем сервер с кнопки...");
				startServerListener();
			}
		});

		// обработчик кнопки остановки сервера
		stopServerButton = (Button) findViewById(R.id.stopBTServerButton);
		stopServerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "Останавливаем сервер с кнопки...");
				stopServerListener();
			}
		});

		if (BluetoothAdapter.getDefaultAdapter() == null) {
			startServerButton.setEnabled(false);
		}

		serverStatusTextView = (TextView) findViewById(R.id.bluetoothServerStatus);

	}

	@Override
	protected void onStart() {

		Log.d(TAG, "onStart");

		registerReceiver(btChangeStateReceiver, btChangeStateFilter);
		registerReceiver(serverStateReceiver, serverStateFilter);

		startServerListener();

		super.onStart();
	}

	@Override
	protected void onStop() {

		Log.d(TAG, "onStop");

		unregisterReceiver(btChangeStateReceiver);
		unregisterReceiver(serverStateReceiver);

		stopServerListener();

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == BT_ENABLE_REQUEST_CODE) {
			if (resultCode != Activity.RESULT_OK) {
				Log.d(TAG, "отказались включать блютус");
				// пользователь отказался от включения блютус
				// либо произошла еще какая-то ошибка
				needStart = false;
			}
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startServerListener() {

		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			// проверяем что адаптер находится в состоянии доступности
			int state = adapter.getState();

			switch (state) {
			case BluetoothAdapter.STATE_ON:
				Log.d(TAG, "Запускаем сервер...");

				stopServerListener();

				// запускаем ожидание соединения
				serverListener = new ServerListener(getApplicationContext());
				serverListener.start();
				break;

			case BluetoothAdapter.STATE_OFF:
				Log.d(TAG, "Просим включить блютус...");

				// указываем на необходимость запуска ожидания входящего
				// соединения
				needStart = true;

				// просим включить блютус
				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(intent, BT_ENABLE_REQUEST_CODE);
				break;

			case BluetoothAdapter.STATE_TURNING_ON:
				Log.d(TAG,
						"Адаптер стартует, необходимость запуска ожидания входящего соединения...");

				// указываем на необходимость запуска ожидания входящего
				// соединения
				needStart = true;
				break;

			case BluetoothAdapter.STATE_TURNING_OFF:
				Log.d(TAG,
						"Адаптер выключается, необходимость запуска ожидания входящего соединения...");

				// указываем на необходимость запуска ожидания входящего
				// соединения
				needStart = true;
				break;
			}
		}
	}

	private void stopServerListener() {

		if (serverListener != null) {
			serverListener.cancel();
			serverListener = null;
		}
	}

}
