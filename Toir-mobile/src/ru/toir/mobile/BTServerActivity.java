package ru.toir.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import ru.toir.mobile.rfid.RfidDialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BTServerActivity extends Activity {

	private static final String TAG = "BTServerActivity";
	private static final int BT_ENABLE_REQUEST_CODE = 666;

	public static final String BT_SERVER_UUID = "E8627152-8F74-460B-B31E-A879194BB431";
	public static final String BT_SERVICE_RECORD_NAME = "ToirBTServer";

	BluetoothAdapter adapter;
	Button startServerButton;

	IntentFilter btChangeStateFilter;
	BroadcastReceiver btChangeStateReceiver;

	ServerListener listener;

	RfidDialog rfidDialog;

	boolean needStart;

	private class ServerParser extends Thread {

		private BluetoothSocket socket;
		private InputStream inputStream;
		private OutputStream outputStream;
		private Handler handler;

		public ServerParser(BluetoothSocket socket, Handler handler) {
			if (socket != null) {
				this.socket = socket;
				this.handler = handler;
				try {
					inputStream = socket.getInputStream();
					outputStream = socket.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {

			int bufferLength = 1024;
			byte[] buffer = new byte[bufferLength];
			int count = 0;
			int offset = 0;
			int parseOffset = 0;

			if (inputStream != null) {
				try {
					while (true) {
						count = inputStream.read(buffer, offset, bufferLength
								- offset);
						if (count > 0) {
							offset += count;
							if (offset >= bufferLength) {
								offset = 0;
							}
						}

						// TODO Реализовать разбор данных от клиента
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}

	}

	private class ServerListener extends Thread {

		private BluetoothServerSocket serverSocket;
		private UUID uuid;

		public ServerListener() {

			serverSocket = null;
			uuid = null;
			try {
				uuid = UUID.fromString(BT_SERVER_UUID);
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}

		@Override
		public void run() {

			Log.d(TAG, "Запускаем поток ожидания входящего соединения...");

			adapter.cancelDiscovery();

			// получаем серверный сокет
			try {
				serverSocket = adapter.listenUsingRfcommWithServiceRecord(
						BT_SERVICE_RECORD_NAME, uuid);
				Log.d(TAG, "Получили серверный сокет...");
			} catch (Exception e) {
				Log.d(TAG, e.getLocalizedMessage());
			}

			// запускаем ожидание соединения от клиента
			if (serverSocket != null) {
				while (true) {
					try {
						Log.d(TAG, "Запускаем приём соединения...");
						BluetoothSocket socket = serverSocket.accept(5000);
						Log.d(TAG, "Входящее соединение получено...");
						// запускаем поток сервера, ожидающего команды
					} catch (Exception e) {
						Log.e(TAG, e.getLocalizedMessage());
					} finally {
						if (isInterrupted()) {
							break;
						}
					}
				}
			} else {
				Log.e(TAG, "Серверный сокет не получили!!!");
			}
			Log.d(TAG, "Завершился поток ожидания входящего соединения...");
		}
	}

	public BTServerActivity() {

		needStart = false;
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
						if (listener != null) {
							listener.interrupt();
							listener = null;
						}

						// запускаем поток с ожиданием подключения от клиента
						listener = new ServerListener();
						listener.start();
						needStart = false;
					}

					break;

				case BluetoothAdapter.STATE_OFF:
					Log.d(TAG, "BT Off");

					// если поток запущен, прерываем его
					if (listener != null) {
						listener.interrupt();
						listener = null;
					}

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
		startServerButton = (Button) findViewById(R.id.startBTServerButton);
		startServerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (adapter != null) {
					// проверяем что адаптер находится в состоянии доступности
					int state = adapter.getState();

					switch (state) {
					case BluetoothAdapter.STATE_ON:
						Log.d(TAG, "Запускаем сервер с кнопки...");

						if (listener != null) {
							listener.interrupt();
							listener = null;
						}

						// запускаем ожидание соединения
						listener = new ServerListener();
						listener.start();
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
		});

		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			startServerButton.setEnabled(false);
		}

	}

	@Override
	protected void onStart() {

		Log.d(TAG, "onStart");

		registerReceiver(btChangeStateReceiver, btChangeStateFilter);

		super.onStart();
	}

	@Override
	protected void onStop() {

		Log.d(TAG, "onStop");

		unregisterReceiver(btChangeStateReceiver);

		if (listener != null) {
			listener.interrupt();
			listener = null;
		}

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

}
