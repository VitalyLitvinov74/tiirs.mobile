/**
 * 
 */
package ru.toir.mobile.rfid.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

	/**
	 * @param handler
	 */
	public RfidDriverBluetooth(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#init()
	 */
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#readTagId()
	 */
	@Override
	public void readTagId() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#readTagData(java.lang.String, int,
	 * int, int)
	 */
	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#readTagData(java.lang.String,
	 * java.lang.String, int, int, int)
	 */
	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#writeTagData(java.lang.String, int,
	 * int, java.lang.String)
	 */
	@Override
	public void writeTagData(String password, int memoryBank, int address,
			String data) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#writeTagData(java.lang.String,
	 * java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, String data) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.IRfidDriver#getView(android.view.LayoutInflater,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return null;
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
			listPreference.setKey("rfidDrvBluetoothServer");
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
