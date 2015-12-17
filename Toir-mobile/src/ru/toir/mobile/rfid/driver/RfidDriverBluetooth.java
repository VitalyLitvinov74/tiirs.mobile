/**
 * 
 */
package ru.toir.mobile.rfid.driver;

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
	private String mServerMac;
	public static final String SERVER_MAC_PREF_KEY = "rfidDrvBluetoothServer";
	private BluetoothAdapter mAdapter;
	private BluetoothDevice mDevice;
	private ICommunicator mCommunicator;

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
						case 6:
							Log.d(TAG, "Соединение с сервером потеряно...");
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

				mCommunicator = new ClientCommunicator(socket, listener);

				new Thread() {

					@Override
					public void run() {
						mCommunicator.startCommunication();
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
		if (mCommunicator != null) {
			mCommunicator.write(new byte[] { 1 });
		}
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		if (mCommunicator != null) {
			mCommunicator.write(new byte[] { 2 });
		}
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {
		if (mCommunicator != null) {
			mCommunicator.write(new byte[] { 3 });
		}
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {
		if (mCommunicator != null) {
			mCommunicator.write(new byte[] { 4 });
		}
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {
		if (mCommunicator != null) {
			mCommunicator.write(new byte[] { 5 });
		}
	}

	@Override
	public void close() {
		if (mCommunicator != null) {
			mCommunicator.stopCommunication();
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
}
