/**
 * 
 */
package ru.toir.mobile.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 *         Класс сервера работающего с блютус.
 */
public class BTRfidServer {

	private static final String TAG = "BTRfidServer";

	private Context mContext;
	private Handler mHandler;

	private BluetoothAdapter mAdapter;

	public static final UUID BT_SERVICE_RECORD_UUID = UUID.fromString("E8627152-8F74-460B-B31E-A879194BB431");
	public static final String BT_SERVICE_RECORD_NAME = "ToirBTServer";

	private AcceptThread mAcceptThread;
	private CommunicationThread mCommunicationThread;

	public static final String SERVER_STATE_ACTION = "ru.toir.mobile.btserver.state";
	public static final String SERVER_STATE_PARAM = "state";

	public static final int SERVER_STATE_STOPED = 1;
	public static final int SERVER_STATE_WAITING_CONNECTION = 2;
	public static final int SERVER_STATE_CONNECTED = 3;
	public static final int SERVER_STATE_DISCONNECTED = 4;

	/**
	 * Конструктор.
	 */
	public BTRfidServer(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * Запускаем ожидание входящего соединения от клиента.
	 */
	public void startListen() {
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}

		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
	}

	/**
	 * Запускаем поток взаимодействующий с клиентом.
	 * 
	 * @param socket
	 *            Сокет через который работаем с клиентом.
	 */
	private void startCommunication(BluetoothSocket socket) {
		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}

		if (mCommunicationThread == null) {
			mCommunicationThread = new CommunicationThread(socket);
		}
	}

	/**
	 * Отправка данных клиенту.
	 * 
	 * @param buffer
	 *            Массив данных отправляемых клиенту.
	 */
	public void write(byte[] buffer) {
		mCommunicationThread.write(buffer);
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 *         Класс отвечающий за ожидание входящего соединения от клиента.
	 * 
	 */
	private class AcceptThread extends Thread {
		private static final String TAG = "ServerListener";
		private BluetoothServerSocket mServerSocket;

		/**
		 * Конструктор.
		 */
		public AcceptThread() {
			BluetoothServerSocket socket = null;

			// получаем серверный сокет
			try {
				socket = mAdapter.listenUsingRfcommWithServiceRecord(
						BT_SERVICE_RECORD_NAME, BT_SERVICE_RECORD_UUID);
				Log.d(TAG, "Получили серверный сокет...");
			} catch (Exception e) {
				Log.d(TAG, e.getLocalizedMessage());
			}

			mServerSocket = socket;
		}

		@Override
		public void run() {
			Intent intent;
			Log.d(TAG, "Запускаем поток ожидания входящего соединения...");
			mAdapter.cancelDiscovery();

			// запускаем ожидание соединения от клиента
			if (mServerSocket != null) {
				intent = new Intent(SERVER_STATE_ACTION);
				intent.putExtra(SERVER_STATE_PARAM,
						SERVER_STATE_WAITING_CONNECTION);
				mContext.sendBroadcast(intent);

				while (true) {
					try {
						Log.d(TAG,
								"Запуск ожидания входящего соединения от клиента...");
						BluetoothSocket socket = mServerSocket.accept();
						Log.d(TAG, "Входящее соединение получено...");

						// освобождаем серверный сокет
						cancel();

						// сообщаем о том что соединение установленно
						intent = new Intent(SERVER_STATE_ACTION);
						intent.putExtra(SERVER_STATE_PARAM,
								SERVER_STATE_CONNECTED);
						mContext.sendBroadcast(intent);

						// запускаем поток сервера, ожидающего команды
						startCommunication(socket);
						break;
					} catch (Exception e) {
						Log.e(TAG, e.getLocalizedMessage());
						break;
					}
				}
			} else {
				Log.e(TAG, "Серверный сокет не получили!!!");
				intent = new Intent(SERVER_STATE_ACTION);
				intent.putExtra(SERVER_STATE_PARAM,
						"Серверный сокет не получили!!!");
				mContext.sendBroadcast(intent);
				return;
			}

			Log.d(TAG, "Завершился поток ожидания входящего соединения...");
		}

		public void cancel() {
			try {
				mServerSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mServerSocket = null;
				Intent intent = new Intent(SERVER_STATE_ACTION);
				intent.putExtra(SERVER_STATE_PARAM, SERVER_STATE_STOPED);
				mContext.sendBroadcast(intent);
			}
		}
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 *         Класс отвечающий за работу через установленное соединение.
	 */
	private class CommunicationThread extends Thread {
		private static final String TAG = "ServerCommunicator";

		private BluetoothSocket mSocket;
		private InputStream mInputStream;
		private OutputStream mOutputStream;

		public CommunicationThread(BluetoothSocket socket) {
			mSocket = socket;
			InputStream tmpInputStream = null;
			OutputStream tmpOutputStream = null;

			try {
				tmpInputStream = socket.getInputStream();
				tmpOutputStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mInputStream = tmpInputStream;
			mOutputStream = tmpOutputStream;
		}

		public void write(byte[] command) {
			try {
				mOutputStream.write(command);
				Log.d(TAG, "Успешно отправили данные клиенту...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			int bufferLength = 1024;
			int count;
			byte buffer[] = new byte[bufferLength];

			while (true) {
				try {
					count = mInputStream.read(buffer, 0, bufferLength);
					if (count >= 0) {
						// TODO: Реализовать разбор данных от клиента
						// TODO: Заменить 666 на подходящий код комманды
						// считывателя
						Message msg = mHandler.obtainMessage(666,
								Arrays.copyOfRange(buffer, 0, count));
						mHandler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: Заменить 666 на подходящий код комманды
					// считывателя
					Message msg = mHandler.obtainMessage(666, new byte[] { 6 });
					mHandler.sendMessage(msg);
					return;
				}
			}
		}

		public void cancel() {
			try {
				mSocket.close();
			} catch (IOException e) {
				Log.d(TAG, e.getLocalizedMessage());
			}
		}
	}
}
