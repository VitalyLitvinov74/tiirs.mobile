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
public class ServerCommunicator implements ICommunicator {
	
	private static final String TAG = "ServerCommunicator";

	private BluetoothSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ICommunicatorListener listener;

	public ServerCommunicator(BluetoothSocket socket,
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
		int count;
		byte buffer[] = new byte[bufferLength];

		while (true) {
			try {
				count = inputStream.read(buffer, 0, bufferLength);
				if (count >= 0) {
					listener.onMessage(Arrays.copyOfRange(buffer, 0, count));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	@Override
	public void stopCommunication() {

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
			Log.d(TAG, "Успешно отправили данные клиенту...");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
