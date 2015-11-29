package ru.toir.mobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;

public class ToirPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ToirPreferenceFragment())
				.commit();
	}

	public class ToirPreferenceFragment extends PreferenceFragment {

		ListPreference drvList;
		PreferenceScreen drvSettingScr;

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			// элемент интерфейса со списком драйверов считываетелей
			drvList = (ListPreference) findPreference(getResources().getString(
					R.string.rfidDriverListPrefKey));
			// при изменении драйвера, включаем дополнительный экран с
			// настройками драйвера
			drvList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {

					String value = (String) newValue;

					// для драйвера блютус есть настройки
					if (value.equals(getResources().getString(
							R.string.rfidDriverBluetoothClass))) {

						drvSettingScr.setEnabled(true);
						drvSettingScr.removeAll();
						// стрим интерфейс с настройками драйвера блютус
						BluetoothAdapter adapter;
						adapter = BluetoothAdapter.getDefaultAdapter();
						if (adapter != null) {
							ListPreference listPreference = new ListPreference(
									getActivity());
							listPreference.setKey(getResources().getString(
									R.string.rfidDrvBluetoothServer));
							listPreference.setTitle("Доступные устройства");
							List<String> names = new ArrayList<String>();
							List<String> values = new ArrayList<String>();

							Set<BluetoothDevice> deviceSet = adapter
									.getBondedDevices();
							for (BluetoothDevice device : deviceSet) {
								names.add(device.getName());
								values.add(device.getAddress());
							}

							listPreference.setEntries(names
									.toArray(new String[] {}));
							listPreference.setEntryValues(values
									.toArray(new String[] {}));
							drvSettingScr.addPreference(listPreference);
						}

					} else {
						drvSettingScr.setEnabled(false);
					}

					// принудительно перерисовываем элемент
					View view = drvSettingScr.getView(null, null);
					view.requestLayout();
					view = null;
					return true;
				}
			});

			drvSettingScr = (PreferenceScreen) findPreference(getResources()
					.getString(R.string.rfidDrvSettingKey));

			// перед показом экрана настроек приложения пользователю,
			// включаем экран настройки драйвера считывателя
			String currentDrv = preferences.getString(
					getResources().getString(R.string.rfidDriverListPrefKey),
					null);
			if (currentDrv != null) {
				String bluetoothDrv = getResources().getString(
						R.string.rfidDriverBluetoothClass);
				if (!currentDrv.equals(bluetoothDrv)) {
					drvSettingScr.setEnabled(false);
				}
			} else {
				drvSettingScr.setEnabled(false);
			}

		}
	}

}
