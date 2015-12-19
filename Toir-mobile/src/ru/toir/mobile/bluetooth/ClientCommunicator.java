/**
 * 
 */
package ru.toir.mobile.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 */
public class ClientCommunicator extends Thread {

	private static final String TAG = "ClientCommunicator";

	private BluetoothSocket mSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	private Handler mHandler;
	private boolean stopDriver = false;

	public ClientCommunicator(BluetoothSocket socket, Handler handler) {

		mSocket = socket;
		mHandler = handler;
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

	@Override
	public void run() {

		int bufferLength = 1024;
		int count = 0;
		byte buffer[] = new byte[bufferLength];

		while (true) {
			try {
				Log.d(TAG, "Читаем данные с сервера...");
				count = mInputStream.read(buffer, 0, bufferLength);
				if (count > 0) {
					Log.d(TAG, "прочитано байт = " + count);
					mHandler.obtainMessage(buffer[0],
							Arrays.copyOfRange(buffer, 0, count))
							.sendToTarget();
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (!stopDriver) {
					// если драйвер не останавливается штатно, шлём сообщение
					mHandler.obtainMessage(6, new byte[] { 6 }).sendToTarget();
				}
				break;
			}
		}

	}

	public void cancel() {
		stopDriver = true;
		try {
			mSocket.close();
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	public void write(byte[] command) {

		try {
			mOutputStream.write(command);
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
}
