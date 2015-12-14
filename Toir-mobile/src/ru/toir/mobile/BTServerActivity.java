package ru.toir.mobile;

import java.util.UUID;

import ru.toir.mobile.bluetooth.ICommunicatorListener;
import ru.toir.mobile.bluetooth.ServerCommunicator;
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
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BTServerActivity extends Activity {

	private static final String TAG = "BTServerActivity";
	private static final int BT_ENABLE_REQUEST_CODE = 666;

	public static final String BT_SERVER_UUID = "E8627152-8F74-460B-B31E-A879194BB431";
	public static final String BT_SERVICE_RECORD_NAME = "ToirBTServer";

	private BluetoothAdapter adapter;
	private Button startServerButton;
	private TextView serverStatusTextView;

	private IntentFilter btChangeStateFilter;
	private BroadcastReceiver btChangeStateReceiver;

	private ServerListener listener;

	private RfidDialog rfidDialog;
	private ServerCommunicator communicator;

	private boolean needStart;

	// обработчик сообщений из потока чтения данных с
	// клиента для работы с интерфейсом
	private Handler uiHandler;

	private class ServerListener extends Thread {

		private BluetoothServerSocket serverSocket;
		private UUID uuid;
		private Handler handler;

		public ServerListener(Handler handler) {

			this.handler = handler;
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

			Message message;

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
						message = new Message();
						message.what = 1;
						message.obj = "Ожидание соединения с клиентом...";
						if (handler != null) {
							handler.sendMessage(message);
						}

						Log.d(TAG, "Запускаем приём соединения...");
						BluetoothSocket socket = serverSocket.accept();

						Log.d(TAG, "Входящее соединение получено...");

						message = new Message();
						message.what = 2;
						message.obj = "Соединение установленно...";
						if (handler != null) {
							handler.sendMessage(message);
						}

						// запускаем поток сервера, ожидающего команды
						ICommunicatorListener listener = new ICommunicatorListener() {

							@Override
							public void onMessage(byte[] message) {
								Log.d(TAG, "Получили сообщение от клиента!!!");
								switch (message[0]) {
								case 1:
									Log.d(TAG, "Чтение id метки...");
									byte[] data = new byte[] { 1, '0', '1',
											'2', '3', '4', '5', '6', '7' };
									communicator.write(data);
									break;
								case 2:
									Log.d(TAG,
											"Чтение данных случайной метки..");
									communicator.write(new byte[] { 2 });
									break;
								case 3:
									Log.d(TAG,
											"Чтение данных конкретной метки...");
									communicator.write(new byte[] { 3 });
									break;
								case 4:
									Log.d(TAG,
											"Запись данных в случайную метку...");
									communicator.write(new byte[] { 4 });
									break;
								case 5:
									Log.d(TAG,
											"Запись данных в конкретную метку...");
									communicator.write(new byte[] { 5 });
									break;
								default:
									Log.d(TAG,
											"Неизвестная команда от клиента...");
									break;
								}
							}
						};
						communicator = new ServerCommunicator(socket, listener);
						communicator.startCommunication();
						break;
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
			// разблокируем кнопку запуска
			message = new Message();
			message.what = 3;
			message.obj = "Остановлен...";
			if (handler != null) {
				handler.sendMessage(message);
			}

		}
	}

	public BTServerActivity() {

		needStart = false;

		uiHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				serverStatusTextView.setText((String) msg.obj);

				switch (msg.what) {
				case 1:
					startServerButton.setEnabled(false);
					break;
				case 2:
					break;
				case 3:
					startServerButton.setEnabled(true);
					break;
				}
				return true;
			}
		});
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
						listener = new ServerListener(uiHandler);
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
						listener = new ServerListener(uiHandler);
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

		serverStatusTextView = (TextView) findViewById(R.id.bluetoothServerStatus);

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

		uiHandler = null;

		if (communicator != null) {
			communicator.stopCommunication();
			communicator = null;
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
