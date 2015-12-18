/**
 * 
 */
package ru.toir.mobile.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 */
public class ClientCommunicator implements ICommunicator {

	private static final String TAG = "ClientCommunicator";

	private BluetoothSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ICommunicatorListener listener;
	private boolean stopDriver = false;

	public ClientCommunicator(BluetoothSocket socket,
			ICommunicatorListener listener) {

		this.socket = socket;
		this.listener = listener;

		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void startCommunication() {

		int bufferLength = 1024;
		int count = 0;
		byte buffer[] = new byte[bufferLength];

		while (true) {
			try {
				Log.d(TAG, "Читаем данные с сервера...");
				count = inputStream.read(buffer, 0, bufferLength);
				if (count > 0) {
					Log.d(TAG, "прочитано байт = " + count);
					listener.onMessage(Arrays.copyOfRange(buffer, 0, count));
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (!stopDriver) {
					// если драйвер не останавливается штатно, шлём сообщение
					listener.onMessage(new byte[] { 6 });
				}
				break;
			}
		}

	}

	@Override
	public void stopCommunication() {
		stopDriver = true;
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(byte[] command) {

		try {
			outputStream.write(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
