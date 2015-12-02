package ru.toir.mobile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import dalvik.system.DexFile;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;

public class ToirPreferences extends PreferenceActivity {

	private static final String TAG = "ToirPreferences";

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

			// получаем список драйверов по имени класса
			List<String> driverClassList = new ArrayList<String>();
			try {
				DexFile df = new DexFile(getApplicationContext()
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

					// проверяем есть ли настройки у драйвера
					if (hasSettingsSreen(value)) {
						drvSettingScr.setEnabled(true);
					} else {
						drvSettingScr.setEnabled(false);
					}

					// принудительно перерисовываем элемент
					View view = drvSettingScr.getView(null, null);
					view.invalidate();
					view = null;
					return true;
				}
			});

			drvSettingScr = (PreferenceScreen) findPreference(getResources()
					.getString(R.string.rfidDrvSettingKey));

			// проверяем есть ли настройки у драйвера
			String currentDrv = preferences.getString(
					getResources().getString(R.string.rfidDriverListPrefKey),
					null);
			if (currentDrv != null) {
				if (hasSettingsSreen(currentDrv)) {
					drvSettingScr.setEnabled(true);
				} else {
					drvSettingScr.setEnabled(false);
				}
			} else {
				drvSettingScr.setEnabled(false);
			}

		}

		private boolean hasSettingsSreen(String classPath) {

			Class<?> driverClass;
			try {
				// пытаемся получить класс драйвера
				driverClass = Class.forName(classPath);

				// пытаемся получить метод getSettingsView
				Method method = driverClass.getMethod("getSettingsView",
						new Class[] { PreferenceScreen.class });

				// передаём драйверу "чистый" экран
				drvSettingScr.removeAll();

				// пытаемся вызвать метод
				PreferenceScreen screen = (PreferenceScreen) method.invoke(
						null, drvSettingScr);

				// если драйвер не вернул экран, значит настроек для него нет
				if (screen != null) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}

	}

}
