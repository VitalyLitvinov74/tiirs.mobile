/**
 * 
 */
package ru.toir.mobile.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 */
public class ServerListener extends Thread {

	private static final String TAG = "ServerListener";

	private BluetoothServerSocket serverSocket;
	private BluetoothAdapter adapter;
	private Context context;

	// TODO вообще это здесь должно быть?
	// видимо нет, т.к. работа с комуникатором должна проходить либо в активити,
	// либо в потоке комуникатора
	private ServerCommunicator communicator;

	ICommunicatorListener communicatorListener;

	public ServerListener(Context context) {

		this.context = context;
		serverSocket = null;
		adapter = BluetoothAdapter.getDefaultAdapter();

		final Context context2 = context;
		communicatorListener = new ICommunicatorListener() {

			@Override
			public void onMessage(byte[] message) {
				Log.d(TAG, "Получили сообщение от клиента!!!");
				switch (message[0]) {
				case 1:
					Log.d(TAG, "Чтение id метки...");
					byte[] data = new byte[] { 1, '0', '1', '2', '3', '4', '5',
							'6', '7' };
					communicator.write(data);
					break;
				case 2:
					Log.d(TAG, "Чтение данных случайной метки..");
					communicator.write(new byte[] { 2 });
					break;
				case 3:
					Log.d(TAG, "Чтение данных конкретной метки...");
					communicator.write(new byte[] { 3 });
					break;
				case 4:
					Log.d(TAG, "Запись данных в случайную метку...");
					communicator.write(new byte[] { 4 });
					break;
				case 5:
					Log.d(TAG, "Запись данных в конкретную метку...");
					communicator.write(new byte[] { 5 });
					break;
				case 6:
					Log.d(TAG, "Соединение с клиентом разорвано...");
					Intent intent = new Intent(BTRfidServer.SERVER_STATE_ACTION);
					intent.putExtra(BTRfidServer.SERVER_STATE_PARAM,
							BTRfidServer.SERVER_STATE_DISCONNECTED);
					context2.sendBroadcast(intent);
					break;
				default:
					Log.d(TAG, "Неизвестная команда от клиента...");
					break;
				}
			}
		};

		// получаем серверный сокет
		try {
			serverSocket = adapter.listenUsingRfcommWithServiceRecord(
					BTRfidServer.BT_SERVICE_RECORD_NAME,
					BTRfidServer.BT_SERVICE_RECORD_UUID);
			Log.d(TAG, "Получили серверный сокет...");
		} catch (Exception e) {
			Log.d(TAG, e.getLocalizedMessage());
		}

	}

	@Override
	public void run() {

		Intent intent;

		Log.d(TAG, "Запускаем поток ожидания входящего соединения...");

		adapter.cancelDiscovery();

		// запускаем ожидание соединения от клиента
		if (serverSocket != null) {

			intent = new Intent(BTRfidServer.SERVER_STATE_ACTION);
			intent.putExtra(BTRfidServer.SERVER_STATE_PARAM,
					BTRfidServer.SERVER_STATE_WAITING_CONNECTION);
			context.sendBroadcast(intent);

			while (true) {
				try {
					Log.d(TAG,
							"Запуск ожидания входящего соединения от клиента...");
					BluetoothSocket socket = serverSocket.accept();

					Log.d(TAG, "Входящее соединение получено...");

					// освобождаем серверный сокет
					cancel();

					// сообщаем о том что соединение установленно
					intent = new Intent(BTRfidServer.SERVER_STATE_ACTION);
					intent.putExtra(BTRfidServer.SERVER_STATE_PARAM,
							BTRfidServer.SERVER_STATE_CONNECTED);
					context.sendBroadcast(intent);

					// запускаем поток сервера, ожидающего команды
					communicator = new ServerCommunicator(socket,
							communicatorListener);
					communicator.start();
					// communicator.startCommunication();

					break;
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
					break;
				}
			}
		} else {
			Log.e(TAG, "Серверный сокет не получили!!!");
			intent = new Intent(BTRfidServer.SERVER_STATE_ACTION);
			intent.putExtra(BTRfidServer.SERVER_STATE_PARAM,
					"Серверный сокет не получили!!!");
			context.sendBroadcast(intent);
			return;
		}

		Log.d(TAG, "Завершился поток ожидания входящего соединения...");

	}

	public void cancel() {

		try {
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			serverSocket = null;
			Intent intent = new Intent(BTRfidServer.SERVER_STATE_ACTION);
			intent.putExtra(BTRfidServer.SERVER_STATE_PARAM,
					BTRfidServer.SERVER_STATE_STOPED);
			context.sendBroadcast(intent);
		}
	}

	public void test() {
		Log.d("test", "1");
		try {
			serverSocket.close();
		} catch (Exception e) {
			Log.d(TAG, e.getLocalizedMessage());
		}
		Log.d("test", "2");
		try {
			serverSocket.close();
		} catch (Exception e) {
			Log.d(TAG, e.getLocalizedMessage());
		}
	}
}
