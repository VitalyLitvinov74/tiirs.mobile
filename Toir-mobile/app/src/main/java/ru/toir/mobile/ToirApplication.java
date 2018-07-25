package ru.toir.mobile;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.toir.mobile.db.ToirRealm;

/**
 * @author Oleg Ivanov
 *
 */
@SuppressWarnings("unused")
public class ToirApplication extends Application {

	public static String serverUrl = "";

	@Override
	public void onCreate() {
		super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = preferences.getString(getString(R.string.serverUrl), null);
        if (serverUrl == null) {
            String defaultUrl = "https://api.toir.tehnosber.ru";
            serverUrl = defaultUrl;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sp.edit().putString(getString(R.string.serverUrl), defaultUrl).apply();
        }

        // инициализируем синглтон с данными о активном пользователе на уровне приложения
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();

        // инициализируем базу данных Realm
        ToirRealm.init(this);
    }
}
