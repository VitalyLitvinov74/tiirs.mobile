package ru.toir.mobile;

import ru.toir.mobile.bluetooth.BTRfidServer;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BTServerActivity extends Activity {

	private static final String TAG = "BTServerActivity";
	private static final int BT_ENABLE_REQUEST_CODE = 666;

	private Button startServerButton;
	private Button stopServerButton;
	private TextView serverStatusTextView;

	private IntentFilter btChangeStateFilter;
	private BroadcastReceiver btChangeStateReceiver;

	// объект сервера блютус
	private BTRfidServer mBtRfidServer;

	// флаг необходимости запуска сервера, в ситуации когда адаптер находится в
	// промежуточных состояниях
	private boolean needStart;

	// обработчик сообщений от блютус сервера
	private Handler mHandler;

	private RfidDialog rfidDialog;

	public BTServerActivity() {

		needStart = false;

		mHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				switch (msg.what) {
				case BTRfidServer.SERVER_STATE_STOPED:
					Log.e(TAG, "SERVER_STATE_STOPED");
					serverStatusTextView.setText("Остановлен...");
					startServerButton.setEnabled(true);
					stopServerButton.setEnabled(false);
					break;
				case BTRfidServer.SERVER_STATE_WAITING_CONNECTION:
					Log.e(TAG, "SERVER_STATE_WAITING_CONNECTION");
					serverStatusTextView
							.setText("Ожидание входящего соединения от клиента...");
					startServerButton.setEnabled(false);
					stopServerButton.setEnabled(true);
					break;
				case BTRfidServer.SERVER_STATE_CONNECTED:
					Log.e(TAG, "SERVER_STATE_CONNECTED");
					serverStatusTextView.setText("Соединение установленно...");
					startServerButton.setEnabled(false);
					stopServerButton.setEnabled(false);
					break;
				case BTRfidServer.SERVER_STATE_DISCONNECTED:
					Log.e(TAG, "SERVER_STATE_DISCONNECTED");
					serverStatusTextView.setText("Клиент отключился...");
					startServerButton.setEnabled(true);
					stopServerButton.setEnabled(false);
					startServerListener();
					break;
				case BTRfidServer.SERVER_STATE_READ_COMMAND:
					Log.d(TAG, "Получили сообщение от клиента!!!");
					byte[] message = (byte[]) msg.obj;
					switch (message[0]) {
					case RfidDialog.READER_COMMAND_READ_ID:
						Log.d(TAG, "Чтение id метки...");
						Handler handler = new Handler(new Handler.Callback() {

							@Override
							public boolean handleMessage(Message msg) {
								Log.d(TAG, "Получили сообщение из драйвера.");

								if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
									String tagId = (String) msg.obj;
									Log.d(TAG, tagId);

									// отправляем полученное значение клиенту
									mBtRfidServer.sendId(tagId);
								} else {
									// по кодам из RFID можно показать более
									// подробные сообщения
									Toast.makeText(getApplicationContext(),
											"Операция прервана",
											Toast.LENGTH_SHORT).show();
								}

								// закрываем диалог
								rfidDialog.dismiss();
								return true;
							}
						});

						rfidDialog = new RfidDialog(getApplicationContext(),
								handler);
						rfidDialog.readTagId();
						rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
						break;
					case RfidDialog.READER_COMMAND_READ_DATA:
						Log.d(TAG, "Чтение данных случайной метки..");
						mBtRfidServer.write(new byte[] { 2 });
						break;
					case RfidDialog.READER_COMMAND_READ_DATA_ID:
						Log.d(TAG, "Чтение данных конкретной метки...");
						mBtRfidServer.write(new byte[] { 3 });
						break;
					case RfidDialog.READER_COMMAND_WRITE_DATA:
						Log.d(TAG, "Запись данных в случайную метку...");
						mBtRfidServer.write(new byte[] { 4 });
						break;
					case RfidDialog.READER_COMMAND_WRITE_DATA_ID:
						Log.d(TAG, "Запись данных в конкретную метку...");
						mBtRfidServer.write(new byte[] { 5 });
						break;
					default:
						Log.d(TAG, "Неизвестная команда от клиента...");
						break;
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

		mBtRfidServer = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_btserver);

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
						// запускаем поток с ожиданием подключения от клиента
						mBtRfidServer.startServer();
						needStart = false;
					}

					break;

				case BluetoothAdapter.STATE_OFF:
					Log.d(TAG, "BT Off");

					mBtRfidServer.stopServer();

					if (needStart) {
						// просим включить блютус
						Intent intentStartBT = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(intentStartBT,
								BT_ENABLE_REQUEST_CODE);
						// TODO: нужно просто гасить сервер и активити а при
						// старте активити проверять включен блютус или нет
					}

					break;
				}
			}
		};

		// тестовая кнопка
		Button testButton = (Button) findViewById(R.id.testBTServerButton);
		testButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				System.gc();
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
				mBtRfidServer.stopServer();
			}
		});

		if (BluetoothAdapter.getDefaultAdapter() == null) {
			startServerButton.setEnabled(false);
		}

		serverStatusTextView = (TextView) findViewById(R.id.bluetoothServerStatus);

		// создаём объект сервера блютус
		mBtRfidServer = new BTRfidServer(getApplicationContext(), mHandler);
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		registerReceiver(btChangeStateReceiver, btChangeStateFilter);
		startServerListener();
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		unregisterReceiver(btChangeStateReceiver);
		mBtRfidServer.stopServer();
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

				// запускаем ожидание соединения
				mBtRfidServer.startServer();
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
}
