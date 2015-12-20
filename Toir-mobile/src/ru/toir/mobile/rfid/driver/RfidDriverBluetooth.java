/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import android.app.Activity;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.toir.mobile.bluetooth.BTRfidServer;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который использует реальный считыватель на
 *         другом устройстве через bluetooth.
 *         </p>
 */
public class RfidDriverBluetooth extends RfidDriverBase implements IRfidDriver {

	public static final String DRIVER_NAME = "Bluetooth драйвер";
	private String mServerMac;
	public static final String SERVER_MAC_PREF_KEY = "rfidDrvBluetoothServer";
	private BluetoothAdapter mAdapter;
	private BluetoothDevice mDevice;
	private CommunicationThread mCommunicationThread;

	/**
	 * @param handler
	 */
	public RfidDriverBluetooth(Handler handler) {
		super(handler);
	}

	public RfidDriverBluetooth(Handler handler, DialogFragment dialogFragment) {
		super(handler);
	}

	public RfidDriverBluetooth(Handler handler, Activity activity) {
		super(handler);
	}

	@Override
	public boolean init() {
		if (mContext != null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			mServerMac = preferences.getString(SERVER_MAC_PREF_KEY, null);
			if (mServerMac == null) {
				return false;
			}

			Log.d(TAG, "serverMAC = " + mServerMac);

			mAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mAdapter == null) {
				return false;
			}

			// если адаптер не включен, вернём false
			int btState = mAdapter.getState();
			if (btState != BluetoothAdapter.STATE_ON) {
				return false;
			}

			mDevice = mAdapter.getRemoteDevice(mServerMac);

			Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
			boolean devicePaired = false;
			for (BluetoothDevice device : devices) {
				if (mServerMac.equals(device.getAddress())) {
					devicePaired = true;
					break;
				}
			}

			// если MAC адреса сервера нет среди спареных устройств,
			// инициализация не удалась
			if (!devicePaired) {
				return false;
			}

			// блокирующий вариант соединения с сервером, для того что-бы
			// предотвратить ситуацию когда соединение еще не установлено, а
			// запрос с командой уже отправляем
			if (mDevice != null) {
				BluetoothSocket socket;
				try {
					socket = mDevice
							.createRfcommSocketToServiceRecord(BTRfidServer.BT_SERVICE_RECORD_UUID);
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());
					return false;
				}

				try {
					socket.connect();
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());
					return false;
				}

				// обработчик сообщений из потока работы с сервером
				Handler handler = new Handler(new Handler.Callback() {

					@Override
					public boolean handleMessage(Message message) {
						byte[] buffer = (byte[]) message.obj;

						Log.d(TAG, "Получили сообщение от сервера!!!");

						switch (message.what) {
						case 1:
							String tagId = new String(Arrays.copyOfRange(
									buffer, 1, buffer.length));
							Log.d(TAG, "Прочитали id метки... id=" + tagId);
							sHandler.obtainMessage(RESULT_RFID_SUCCESS, tagId)
									.sendToTarget();
							break;
						case 2:
							Log.d(TAG, "Прочитали данные случайной метки..");
							sHandler.obtainMessage(RESULT_RFID_CANCEL)
									.sendToTarget();
							break;
						case 3:
							Log.d(TAG, "Прочитали данные конкретной метки...");
							sHandler.obtainMessage(RESULT_RFID_CANCEL)
									.sendToTarget();
							break;
						case 4:
							Log.d(TAG, "Записали данные в случайную метку...");
							sHandler.obtainMessage(RESULT_RFID_CANCEL)
									.sendToTarget();
							break;
						case 5:
							Log.d(TAG, "Записали данные в конкретную метку...");
							sHandler.obtainMessage(RESULT_RFID_CANCEL)
									.sendToTarget();
							break;
						case 6:
							Log.d(TAG, "Соединение с сервером потеряно...");
							sHandler.obtainMessage(RESULT_RFID_DISCONNECT)
									.sendToTarget();
							break;
						default:
							Log.d(TAG, "Неизвестный ответ сервера...");
							sHandler.obtainMessage(RESULT_RFID_CANCEL)
									.sendToTarget();
							break;
						}

						return true;
					}
				});

				// создаём поток работы с сервером
				mCommunicationThread = new CommunicationThread(socket, handler);
				mCommunicationThread.start();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void readTagId() {
		if (mCommunicationThread != null) {
			mCommunicationThread.write(new byte[] { (byte) 0xBB, 0x00,
					RfidDialog.READER_COMMAND_READ_ID, 0x00, 0x00, 0x7E });
		}
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		if (mCommunicationThread != null) {
			// маркер начала, тип пакета, команда, размер полезной нагрузки,
			// маркер конца
			int serviceDataLength = 1 + 1 + 1 + 2 + 1;
			int payloadLength;
			byte[] passwordBuffer = password.getBytes();

			// 2 байта на длину пароля + длина пароля
			payloadLength = 2 + passwordBuffer.length;

			// банк памяти, смещение, количество данных
			payloadLength += 1 + 2 + 2;
			byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
			int commandBufferIndex = 0;

			// маркер начала пакета
			commandBuffer[commandBufferIndex++] = (byte) 0xBB;

			// тип пакета
			commandBuffer[commandBufferIndex++] = 0x00;

			// команда
			commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_READ_DATA;

			// размер полезной нагрузки
			commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

			// размер данных пароля к метке
			commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

			// пароль к метке
			for (int i = 0; i < passwordBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = passwordBuffer[i];
			}

			// банк памяти
			commandBuffer[commandBufferIndex++] = (byte) memoryBank;

			// смещение в банке памяти
			commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

			// количество данных
			commandBuffer[commandBufferIndex++] = (byte) ((count >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (count & 0xFF);

			// маркер конца
			commandBuffer[commandBufferIndex++] = 0x7E;
			mCommunicationThread.write(commandBuffer);
		}
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {
		if (mCommunicationThread != null) {
			// маркер начала, тип пакета, команда, размер полезной нагрузки,
			// маркер конца
			int serviceDataLength = 1 + 1 + 1 + 2 + 1;
			int payloadLength;
			byte[] passwordBuffer = password.getBytes();
			byte[] tagIdBuffer = tagId.getBytes();

			// 2 байта на длину пароля + длина пароля
			payloadLength = 2 + passwordBuffer.length;

			// 2 байта на длину id метки + длина id метки
			payloadLength += 2 + tagIdBuffer.length;

			// банк памяти, смещение, количество данных
			payloadLength += 1 + 2 + 2;
			byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
			int commandBufferIndex = 0;

			// маркер начала пакета
			commandBuffer[commandBufferIndex++] = (byte) 0xBB;
			// тип пакета
			commandBuffer[commandBufferIndex++] = 0x00;
			// команда
			commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_READ_DATA_ID;

			// размер полезной нагрузки
			commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

			// размер данных пароля к метке
			commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

			// пароль к метке
			for (int i = 0; i < passwordBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = passwordBuffer[i];
			}

			// размер данных id метке
			commandBuffer[commandBufferIndex++] = (byte) ((tagIdBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (tagIdBuffer.length & 0xFF);

			// id метки
			for (int i = 0; i < tagIdBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = tagIdBuffer[i];
			}

			// банк памяти
			commandBuffer[commandBufferIndex++] = (byte) memoryBank;

			// смещение в банке памяти
			commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

			// количество данных
			commandBuffer[commandBufferIndex++] = (byte) ((count >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (count & 0xFF);

			// маркер конца
			commandBuffer[commandBufferIndex++] = 0x7E;
			mCommunicationThread.write(commandBuffer);
		}
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {
		if (mCommunicationThread != null) {
			// маркер начала, тип пакета, команда, размер полезной нагрузки,
			// маркер конца
			int serviceDataLength = 1 + 1 + 1 + 2 + 1;
			int payloadLength;
			byte[] passwordBuffer = password.getBytes();
			byte[] dataBuffer = data.getBytes();

			// 2 байта на длину пароля + длина пароля
			payloadLength = 2 + passwordBuffer.length;

			// 2 байта на длину данных + длина данных
			payloadLength = 2 + dataBuffer.length;

			// банк памяти, смещение
			payloadLength += 1 + 2;
			byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
			int commandBufferIndex = 0;

			// маркер начала пакета
			commandBuffer[commandBufferIndex++] = (byte) 0xBB;

			// тип пакета
			commandBuffer[commandBufferIndex++] = 0x00;

			// команда
			commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_WRITE_DATA;

			// размер полезной нагрузки
			commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

			// размер данных пароля к метке
			commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

			// пароль к метке
			for (int i = 0; i < passwordBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = passwordBuffer[i];
			}

			// банк памяти
			commandBuffer[commandBufferIndex++] = (byte) memoryBank;

			// смещение в банке памяти
			commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

			// размер данных
			commandBuffer[commandBufferIndex++] = (byte) ((dataBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (dataBuffer.length & 0xFF);

			// данные
			for (int i = 0; i < passwordBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = dataBuffer[i];
			}

			// маркер конца
			commandBuffer[commandBufferIndex++] = 0x7E;
			mCommunicationThread.write(commandBuffer);
		}
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {
		if (mCommunicationThread != null) {
			// маркер начала, тип пакета, команда, размер полезной нагрузки,
			// маркер конца
			int serviceDataLength;
			serviceDataLength = 1 + 1 + 1 + 2 + 1;
			int payloadLength;
			byte[] passwordBuffer = password.getBytes();
			byte[] tagIdBuffer = tagId.getBytes();
			byte[] dataBuffer = data.getBytes();

			// 2 байта на длину пароля + длина пароля
			payloadLength = 2 + passwordBuffer.length;

			// 2 байта на id метки + длина id метки
			payloadLength = 2 + tagIdBuffer.length;

			// 2 байта на длину данных + длина данных
			payloadLength = 2 + dataBuffer.length;

			// банк памяти, смещение
			payloadLength += 1 + 2;
			byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
			int commandBufferIndex = 0;

			// маркер начала пакета
			commandBuffer[commandBufferIndex++] = (byte) 0xBB;

			// тип пакета
			commandBuffer[commandBufferIndex++] = 0x00;

			// команда
			commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_WRITE_DATA_ID;

			// размер полезной нагрузки
			commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

			// размер данных пароля к метке
			commandBuffer[commandBufferIndex++] = (byte) ((passwordBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (passwordBuffer.length & 0xFF);

			// пароль к метке
			for (int i = 0; i < passwordBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = passwordBuffer[i];
			}

			// размер данных id метки
			commandBuffer[commandBufferIndex++] = (byte) ((tagIdBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (tagIdBuffer.length & 0xFF);

			// id метки
			for (int i = 0; i < tagIdBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = tagIdBuffer[i];
			}

			// банк памяти
			commandBuffer[commandBufferIndex++] = (byte) memoryBank;

			// смещение в банке памяти
			commandBuffer[commandBufferIndex++] = (byte) ((address >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (address & 0xFF);

			// размер данных
			commandBuffer[commandBufferIndex++] = (byte) ((dataBuffer.length >> 8) & 0xFF);
			commandBuffer[commandBufferIndex++] = (byte) (dataBuffer.length & 0xFF);

			// данные
			for (int i = 0; i < passwordBuffer.length; i++) {
				commandBuffer[commandBufferIndex++] = dataBuffer[i];
			}

			// маркер конца
			commandBuffer[commandBufferIndex++] = 0x7E;
			mCommunicationThread.write(commandBuffer);
		}
	}

	@Override
	public void close() {
		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		// создаём текстовое поле
		TextView textView = new TextView(mContext);
		textView.setText("Считайте метку внешним устройством...");
		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setTextSize(32);
		return textView;
	}

	/**
	 * <p>
	 * Интерфейс настроек драйвера
	 * </p>
	 * 
	 * @return PreferenceScreen Если настроек нет должен вернуть null
	 */
	public static PreferenceScreen getSettingsScreen(PreferenceScreen screen) {

		// строим интерфейс с настройками драйвера блютус
		BluetoothAdapter adapter;
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			ListPreference listPreference = new ListPreference(
					screen.getContext());
			listPreference.setKey(SERVER_MAC_PREF_KEY);
			listPreference.setTitle("Доступные устройства");
			List<String> names = new ArrayList<String>();
			List<String> values = new ArrayList<String>();

			Set<BluetoothDevice> deviceSet = adapter.getBondedDevices();
			for (BluetoothDevice device : deviceSet) {
				names.add(device.getName());
				values.add(device.getAddress());
			}

			listPreference.setEntries(names.toArray(new String[] {}));
			listPreference.setEntryValues(values.toArray(new String[] {}));
			screen.addPreference(listPreference);
		}

		return screen;
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 */
	private class CommunicationThread extends Thread {

		private static final String TAG = "CommunicationThread";

		private BluetoothSocket mSocket;
		private InputStream mInputStream;
		private OutputStream mOutputStream;
		private Handler mHandler;
		private boolean stopDriver = false;

		public CommunicationThread(BluetoothSocket socket, Handler handler) {

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
						// если драйвер не останавливается штатно, шлём
						// сообщение
						mHandler.obtainMessage(6, new byte[] { 6 })
								.sendToTarget();
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
}
