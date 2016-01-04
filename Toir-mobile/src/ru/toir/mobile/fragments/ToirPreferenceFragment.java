/**
 * 
 */
package ru.toir.mobile.fragments;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import ru.toir.mobile.R;
import ru.toir.mobile.rfid.RfidDriverBase;
import dalvik.system.DexFile;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * @author koputo
 * 
 */
public class ToirPreferenceFragment extends PreferenceFragment {

	private static final String TAG = "ToirPreferenceFragment";

	private ListPreference drvList;
	private PreferenceScreen drvSettingScr;
	private PreferenceCategory drvSettingCategory;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());

		// получаем список драйверов по имени класса
		List<String> driverClassList = new ArrayList<String>();
		try {
			DexFile df = new DexFile(getActivity().getApplicationContext()
					.getPackageCodePath());
			for (Enumeration<String> iter = df.entries(); iter
					.hasMoreElements();) {
				String classPath = iter.nextElement();
				if (classPath.contains("ru.toir.mobile.rfid.driver")
						&& !classPath.contains("$")) {
					driverClassList.add(classPath);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// строим список драйверов с именами и классами
		Class<?> driverClass;
		List<String> drvNames = new ArrayList<String>();
		List<String> drvKeys = new ArrayList<String>();
		for (String classPath : driverClassList) {

			driverClass = null;
			try {
				// пытаемся получить класс драйвера
				driverClass = Class.forName(classPath);
				// пытаемся получить свойство DRIVER_NAME
				String name = (String) (driverClass
						.getDeclaredField("DRIVER_NAME").get(new String()));
				if (name != null && !name.equals("")) {
					drvNames.add(name);
					drvKeys.add(classPath);
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}

		// категория с экраном настроек драйвера
		drvSettingCategory = (PreferenceCategory) findPreference("drvSettingsCategory");

		// элемент интерфейса со списком драйверов считывателей
		drvList = (ListPreference) findPreference(getResources().getString(
				R.string.rfidDriverListPrefKey));

		// указываем названия и значения для элементов списка
		drvList.setEntries(drvNames.toArray(new String[] {}));
		drvList.setEntryValues(drvKeys.toArray(new String[] {}));

		// при изменении драйвера, включаем дополнительный экран с
		// настройками драйвера
		drvList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				String value = (String) newValue;
				PreferenceScreen screen = getDrvSettingsSreen(value,
						drvSettingScr);

				// проверяем есть ли настройки у драйвера
				if (screen != null) {
					drvSettingCategory.setEnabled(true);
				} else {
					drvSettingCategory.setEnabled(false);
				}

				return true;
			}
		});

		drvSettingScr = (PreferenceScreen) findPreference(getResources()
				.getString(R.string.rfidDrvSettingKey));

		// проверяем есть ли настройки у драйвера
		String currentDrv = preferences.getString(
				getResources().getString(R.string.rfidDriverListPrefKey), null);
		if (currentDrv != null) {
			if (getDrvSettingsSreen(currentDrv, drvSettingScr) != null) {
				drvSettingCategory.setEnabled(true);
			} else {
				drvSettingCategory.setEnabled(false);
			}
		} else {
			drvSettingCategory.setEnabled(false);
		}

	}

	private PreferenceScreen getDrvSettingsSreen(String classPath,
			PreferenceScreen rootScreen) {

		Class<?> driverClass;
		PreferenceScreen screen;

		try {
			// пытаемся получить класс драйвера
			driverClass = Class.forName(classPath);

			// пытаемся создать объект драйвера
			Constructor<?> c = driverClass.getConstructor();
			RfidDriverBase driver = (RfidDriverBase) c.newInstance();

			// передаём драйверу "чистый" экран
			rootScreen.removeAll();

			// пытаемся вызвать метод
			screen = driver.getSettingsScreen(rootScreen);

			// возвращаем результат
			return screen;
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
			return null;
		}
	}
}
