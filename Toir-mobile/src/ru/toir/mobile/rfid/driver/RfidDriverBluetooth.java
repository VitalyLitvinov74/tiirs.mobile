/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.util.concurrent.ExecutionError;

import android.app.Activity;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
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

			// TODO реализовать проверку состояния адаптера
			// при необходимости запросить включение
			// что-то очень сложно как-то получается, может если адаптер не
			// включен, вернём false?
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
			new Thread() {

				@Override
				public void run() {

					if (device != null) {
						BluetoothSocket socket;
						try {
							socket = device
									.createRfcommSocketToServiceRecord(serverUuid);
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}

						try {
							socket.connect();
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}

						InputStream inputStream;
						OutputStream outputStream;
						try {
							inputStream = socket.getInputStream();
							outputStream = socket.getOutputStream();
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}

						int bufferLength = 1024;
						int count;
						byte buffer[] = new byte[bufferLength];
						while (true) {
							try {
								count = inputStream.read(buffer, 0,
										bufferLength);
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						}
					}
				}

			}.start();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void readTagId() {

	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {

	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {

	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {

	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {

	}

	@Override
	public void close() {

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
