/**
 * 
 */
package ru.toir.mobile.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import dalvik.system.DexFile;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.rfid.RfidDriverBase;

/**
 * @author koputo
 * 
 */
public class ToirPreferenceFragment extends PreferenceFragment {

	private static final String TAG = "ToirPreferenceFragment";

	private PreferenceScreen drvSettingScr;
	private PreferenceCategory drvSettingCategory;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());

        findPreference(getString(R.string.serverUrl))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final EditTextPreference URLPreference = (EditTextPreference) findPreference(getString(R.string.serverUrl));
                        final AlertDialog dialog = (AlertDialog) URLPreference.getDialog();
                        URLPreference.getEditText().setError(null);
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String errorMessage;
                                        String text = URLPreference.getEditText().getText().toString();

                                        try {
                                            URL tURL = new URL(text);
                                            URLPreference.getEditText().setText(tURL.toString().replaceAll("/+$", ""));
                                            errorMessage = null;
                                        } catch (MalformedURLException e) {
                                            if (!text.isEmpty()) {
                                                errorMessage = "Не верный URL!";
                                            } else {
                                                errorMessage = null;
                                            }
                                        }

                                        EditText edit = URLPreference.getEditText();
                                        if (errorMessage == null) {
                                            edit.setError(null);
                                            URLPreference.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                                            dialog.dismiss();
                                            ToirApplication.serverUrl = edit.getText().toString();
                                        } else {
                                            edit.setError(errorMessage);
                                        }
                                    }
                                });

                        return true;
                    }
                });

		// получаем список драйверов по имени класса
		List<String> driverClassList = new ArrayList<>();
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
        List<String> drvNames = new ArrayList<>();
        List<String> drvKeys = new ArrayList<>();
        String name;
        for (String classPath : driverClassList) {
            name = RfidDriverBase.getDriverName(classPath);
            if (name != null) {
                drvNames.add(name);
                drvKeys.add(classPath);
            }
        }

		// категория с экраном настроек драйвера
		drvSettingCategory = (PreferenceCategory) findPreference("drvSettingsCategory");

		// элемент интерфейса со списком драйверов считывателей
        ListPreference drvList = (ListPreference) findPreference(getResources().getString(
                R.string.rfidDriverListPrefKey));

		// указываем названия и значения для элементов списка
        drvList.setEntries(drvNames.toArray(new String[]{""}));
        drvList.setEntryValues(drvKeys.toArray(new String[]{""}));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
