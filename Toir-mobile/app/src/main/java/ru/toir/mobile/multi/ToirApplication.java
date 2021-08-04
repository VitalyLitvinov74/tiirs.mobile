package ru.toir.mobile.multi;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.io.InputStream;

import io.realm.Realm;
import ru.toir.mobile.multi.rfid.driver.RfidDriverNull;
import ru.toir.mobile.multi.rfid.driver.RfidDriverQRcode;

/**
 * @author Oleg Ivanov
 *
 */
@SuppressWarnings("unused")
public class ToirApplication extends Application {

    public static String packageName = "ru.toir.mobile.multi";

    public static String serverUrl = "";
    public static InputStream qwvostokCA;
    public static InputStream sstalRootCA;
    public static InputStream sstalInternalCA;
    public static InputStream sstalDigicert;
    public static InputStream sstalDigicertRoot;
    public static InputStream digicertsha2CA;
    public static Context context;

    public static boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo niMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (niMobile != null) {
                if (niMobile.getState() == NetworkInfo.State.CONNECTED ||
                        niMobile.getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                }
            }

            NetworkInfo niWiFi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (niWiFi != null) {
                return niWiFi.getState() == NetworkInfo.State.CONNECTED ||
                        niWiFi.getState() == NetworkInfo.State.CONNECTING;
            }
        }

        return false;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		context = getBaseContext();
        qwvostokCA = getResources().openRawResource(R.raw.forqwvostok);
        sstalRootCA = getResources().openRawResource(R.raw.severstalroot);
        sstalInternalCA = getResources().openRawResource(R.raw.severstalinternal);
        sstalDigicert = getResources().openRawResource(R.raw.digicertca);
        sstalDigicertRoot = getResources().openRawResource(R.raw.digicertrootca);
        digicertsha2CA = getResources().openRawResource(R.raw.digicertsha2secureserverca);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = preferences.getString(getString(R.string.serverUrl), null);
        if (serverUrl == null) {
            String defaultUrl = "https://api.toir.qwvostok.ru:4443";
            serverUrl = defaultUrl;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sp.edit().putString(getString(R.string.serverUrl), defaultUrl).apply();
        }

        // инициализируем синглтон с данными о активном пользователе на уровне приложения
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();

        // инициализируем Realm
        Realm.init(this);

        // инициализируем интерфейс для отладки через Google Chrome
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        String rfidLoginDriver = preferences.getString(getString(R.string.default_login_rfid_driver_key), null);
        if (rfidLoginDriver == null || rfidLoginDriver.isEmpty()) {
            preferences.edit().putString(getString(R.string.default_login_rfid_driver_key),
                    RfidDriverQRcode.class.getCanonicalName()).apply();
        }

        String rfidDriver = preferences.getString(getString(R.string.default_rfid_driver_key), null);
        if (rfidDriver == null || rfidDriver.isEmpty()) {
            preferences.edit().putString(getString(R.string.default_rfid_driver_key),
                    RfidDriverQRcode.class.getCanonicalName()).apply();
        }

        String uhfDriver = preferences.getString(getString(R.string.default_uhf_driver_key), null);
        if (uhfDriver == null || uhfDriver.isEmpty()) {
            preferences.edit().putString(getString(R.string.default_uhf_driver_key),
                    RfidDriverNull.class.getCanonicalName()).apply();
        }
/*
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                handleUncaughtException(thread, ex);
            }
        });
*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

/*
    public void handleUncaughtException (Thread thread, Throwable e)
    {
        String stackTrace = Log.getStackTraceString(e);
        String message = e.getMessage();

        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra (Intent.EXTRA_EMAIL, new String[] {"olejek8@yandex.ru"});
        intent.putExtra (Intent.EXTRA_SUBJECT, "Toirus crash log file");
        intent.putExtra (Intent.EXTRA_TEXT, stackTrace);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);


    }
*/

}
