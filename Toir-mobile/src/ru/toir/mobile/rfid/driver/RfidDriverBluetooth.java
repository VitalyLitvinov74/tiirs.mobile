/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
import ru.toir.mobile.BTServerActivity;
import ru.toir.mobile.bluetooth.ClientCommunicator;
import ru.toir.mobile.bluetooth.ICommunicator;
import ru.toir.mobile.bluetooth.ICommunicatorListener;
import ru.toir.mobile.rfid.IRfidDriver;
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
	private String serverMAC;
	private static final String serverMACPrefKey = "rfidDrvBluetoothServer";
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private UUID serverUuid;
	private ICommunicator communicator;

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

		// TODO инициализация возможно должна быть реализована по другому,
		// т.к. запрос на выполнение команд серверу может быть отдан до того,
		// как отработает поток соединения с сервером, соответсвенно нужно
		// инициализацию делать блокирующей, чтоб не было запуска команды
		// серверу
		// до того как с ним будет установленно соединение!!!

		if (context != null) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			serverMAC = preferences.getString(serverMACPrefKey, null);

			if (serverMAC == null) {
				return false;
			}
			Log.d(TAG, "serverMAC = " + serverMAC);

			adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter == null) {
				return false;
			}

			// если адаптер не включен, вернём false
			int btState = adapter.getState();
			if (btState != BluetoothAdapter.STATE_ON) {
				return false;
			}

			device = adapter.getRemoteDevice(serverMAC);

			Set<BluetoothDevice> devices = adapter.getBondedDevices();
			boolean devicePaired = false;
			for (BluetoothDevice device : devices) {
				if (serverMAC.equals(device.getAddress())) {
					devicePaired = true;
					break;
				}
			}

			// если MAC адреса сервера нет среди спареных устройств,
			// инициализация не удалась
			if (!devicePaired) {
				return false;
			}

			try {
				serverUuid = UUID.fromString(BTServerActivity.BT_SERVER_UUID);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			// TODO реализовать запуск потока для соединение с сервером
			// нужно реализовать блокирующий вариант соединения с сервером

			if (device != null) {
				BluetoothSocket socket;
				try {
					socket = device
							.createRfcommSocketToServiceRecord(serverUuid);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				try {
					socket.connect();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				
				
				// TODO создаём объект/поток? работы с сервером
				ICommunicatorListener listener = new ICommunicatorListener() {

					@Override
					public void onMessage(byte[] message) {

						Message msg = new Message();

						Log.d(TAG, "Получили сообщение от сервера!!!");

						switch (message[0]) {
						case 1:
							String tagId = new String(Arrays.copyOfRange(
									message, 1, message.length));
							Log.d(TAG, "Прочитали id метки... id=" + tagId);
							msg.what = RESULT_RFID_SUCCESS;
							msg.obj = tagId;
							break;
						case 2:
							Log.d(TAG, "Прочитали данные случайной метки..");
							msg.what = RESULT_RFID_CANCEL;
							break;
						case 3:
							Log.d(TAG, "Прочитали данные конкретной метки...");
							msg.what = RESULT_RFID_CANCEL;
							break;
						case 4:
							Log.d(TAG, "Записали данные в случайную метку...");
							msg.what = RESULT_RFID_CANCEL;
							break;
						case 5:
							Log.d(TAG, "Записали данные в конкретную метку...");
							msg.what = RESULT_RFID_CANCEL;
							break;
						default:
							Log.d(TAG, "Неизвестный ответ сервера...");
							msg.what = RESULT_RFID_CANCEL;
							break;
						}

						mHandler.sendMessage(msg);
					}
				};

				communicator = new ClientCommunicator(socket, listener);

				new Thread(){

					@Override
					public void run() {
						communicator.startCommunication();
					}
					
				}.start();


			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void readTagId() {

		if (communicator != null) {
			communicator.write(new byte[] { 1 });
		}
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

		if (communicator != null) {
			communicator.write(new byte[] { 2 });
		}
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

		if (communicator != null) {
			communicator.write(new byte[] { 3 });
		}
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {

		if (communicator != null) {
			communicator.write(new byte[] { 4 });
		}
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

		if (communicator != null) {
			communicator.write(new byte[] { 5 });
		}
	}

	@Override
	public void close() {

		if (communicator != null) {
			communicator.stopCommunication();
		}
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		// создаём текстовое поле
		TextView textView = new TextView(context);
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
			listPreference.setKey(serverMACPrefKey);
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

}
