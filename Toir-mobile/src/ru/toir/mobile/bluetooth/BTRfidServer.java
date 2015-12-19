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
import android.os.Handler;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 *         Класс сервера работающего с блютус.
 */
public class BTRfidServer {
	private static final String TAG = "BTRfidServer";

	private Handler mHandler;
	private BluetoothAdapter mAdapter;

	public static final UUID BT_SERVICE_RECORD_UUID = UUID
			.fromString("E8627152-8F74-460B-B31E-A879194BB431");
	public static final String BT_SERVICE_RECORD_NAME = "ToirBTServer";

	public AcceptThread mAcceptThread;
	public CommunicationThread mCommunicationThread;

	public static final String SERVER_STATE_ACTION = "ru.toir.mobile.btserver.state";
	public static final String SERVER_STATE_PARAM = "state";

	public static final int SERVER_STATE_STOPED = 1;
	public static final int SERVER_STATE_WAITING_CONNECTION = 2;
	public static final int SERVER_STATE_CONNECTED = 3;
	public static final int SERVER_STATE_DISCONNECTED = 4;

	// текущее состояние сервера
	private int mState;

	/**
	 * Конструктор.
	 */
	public BTRfidServer(Context context, Handler handler) {
		mHandler = handler;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * Запускаем ожидание входящего соединения от клиента.
	 */
	public void startServer() {
		Log.d(TAG, "startServer()");
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

			// сообщаем активити о том что перешли в режим ожидания входящего
			// сообщения
			mHandler.obtainMessage(SERVER_STATE_WAITING_CONNECTION)
					.sendToTarget();
		}
	}

	/**
	 * Останавливаем ожидание входящего соединения от клиента.
	 */
	public void stopServer() {
		Log.d(TAG, "stopServer()");
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}
	}

	/**
	 * Запускаем поток взаимодействующий с клиентом.
	 * 
	 * @param socket
	 *            Сокет через который работаем с клиентом.
	 */
	private void startCommunication(BluetoothSocket socket) {
		Log.d(TAG, "startCommunication()");
		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}

		if (mCommunicationThread == null) {
			mCommunicationThread = new CommunicationThread(socket);
			mCommunicationThread.start();
		}
	}

	/**
	 * Отправка данных клиенту.
	 * 
	 * @param buffer
	 *            Массив данных отправляемых клиенту.
	 */
	public void write(byte[] buffer) {
		Log.d(TAG, "write()");
		mCommunicationThread.write(buffer);
	}

	/**
	 * Установка текущего состояния сервера.
	 * 
	 * @param state
	 */
	public void setState(int state) {
		mState = state;
	}

	/**
	 * Текущее состояние сервера.
	 * 
	 * @return
	 */
	public int getState() {
		return mState;
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 *         Класс отвечающий за ожидание входящего соединения от клиента.
	 * 
	 */
	private class AcceptThread extends Thread {
		private static final String TAG = "AcceptThread";
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
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}

			mServerSocket = socket;
		}

		@Override
		public void run() {
			Log.d(TAG, "run()");
			mAdapter.cancelDiscovery();

			// запускаем ожидание соединения от клиента
			while (true) {
				try {
					BluetoothSocket socket = mServerSocket.accept();
					Log.d(TAG, "Входящее соединение получено...");
					Thread.sleep(1000);

					// запускаем поток сервера, ожидающего команды
					startCommunication(socket);
					break;
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());

					// сообщаем активити о том что отключили режим ожидания
					// входящего соединения
					mHandler.obtainMessage(SERVER_STATE_STOPED).sendToTarget();
					break;
				} catch (InterruptedException e) {
				}
			}

			Log.d(TAG, "Завершился поток ожидания входящего соединения...");
		}

		public void cancel() {
			Log.d(TAG, "cancel()");
			try {
				mServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			} catch (NullPointerException e) {
			}
		}
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 *         Класс отвечающий за работу через установленное соединение.
	 */
	private class CommunicationThread extends Thread {
		private static final String TAG = "CommunicationThread";

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
				Log.e(TAG, e.getLocalizedMessage());
			}

			mInputStream = tmpInputStream;
			mOutputStream = tmpOutputStream;
		}

		public void write(byte[] command) {
			Log.d(TAG, "write()");
			try {
				mOutputStream.write(command);
				Log.d(TAG, "Успешно отправили данные клиенту...");
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}

		@Override
		public void run() {
			Log.d(TAG, "run()");
			int bufferLength = 1024;
			int count = 0;
			byte buffer[] = new byte[bufferLength];

			while (true) {
				try {
					count = mInputStream.read(buffer, 0, bufferLength);
					if (count > 0) {
						// TODO: Реализовать разбор данных от клиента
						// TODO: Заменить 666 на подходящий код комманды
						// считывателя
						mHandler.obtainMessage(666,
								Arrays.copyOfRange(buffer, 0, count))
								.sendToTarget();
					}
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());

					// сообщаем что соединение с клиентом потеряно
					mHandler.obtainMessage(SERVER_STATE_DISCONNECTED)
							.sendToTarget();
					break;
				} catch (IndexOutOfBoundsException e) {
					// случилось невероятное
				}
			}

			Log.d(TAG, "Завершился поток взаимодействия с клиентом...");
		}

		public void cancel() {
			Log.d(TAG, "cancel()");
			try {
				mSocket.close();
				Thread.sleep(2000);
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			} catch (NullPointerException e) {
			} catch (InterruptedException e) {
			}
		}
	}
}
