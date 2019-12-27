package ru.toir.mobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.toir.mobile.MainActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.LoadTestData;
import ru.toir.mobile.utils.MainFunctions;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "ToirSettings";
    private static final String BOT = "bot489333537:AAFWzSpAuWl0v1KJ3sTQKYABpjY0ERgcIcY";
    private static final int ACTIVITY_TELEGRAM = 1;
    private PreferenceCategory basicSettingScr;
    private PreferenceScreen driverSettingScr;
    private Activity mainActivityConnector = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        mainActivityConnector = getActivity();
        if (mainActivityConnector == null)
            return;
/*
        LinearLayout root = (LinearLayout) mainActivityConnector.findViewById(android.R.id.list).getParent().getParent().getParent();
        bar = (AppBarLayout) LayoutInflater.from(mainActivityConnector).inflate(R.layout.toolbar_settings, root, false);
        root.addView(bar, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LinearLayout root = (LinearLayout) mainActivityConnector.findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(mainActivityConnector).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = mainActivityConnector.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(mainActivityConnector).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (mainActivityConnector.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Tbar = (Toolbar) bar.getChildAt(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Tbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mainActivityConnector.finish();
                }
            });
        }
*/

        String appVersion;
        try {
            PackageManager pm = mainActivityConnector.getPackageManager();
            String packageName = mainActivityConnector.getPackageName();
            mainActivityConnector.getApplicationContext().getClassLoader();

            PackageInfo pInfo = pm.getPackageInfo(packageName, 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }

        Log.d(TAG, "version:" + appVersion);

        //setupSimplePreferencesScreen();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mainActivityConnector.getApplicationContext());

        // получаем список драйверов
        String[] driverClassList = RfidDriverBase.getDriverClassList();
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

        // элемент интерфейса со списком драйверов считывателей
        ListPreference drvList = (ListPreference) findPreference(getResources().getString(R.string.rfidDriverListPrefKey));

        basicSettingScr = (PreferenceCategory) this.findPreference("preferenceBasicScreen");
        driverSettingScr = (PreferenceScreen) this.findPreference(getResources()
                .getString(R.string.rfidDrvSettingKey));

        // указываем названия и значения для элементов списка
        drvList.setEntries(drvNames.toArray(new String[]{""}));
        drvList.setEntryValues(drvKeys.toArray(new String[]{""}));

        // при изменении драйвера, включаем дополнительный экран с настройками драйвера
        drvList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                showRfidDriverScreen(value);
                return true;
            }
        });

        // проверяем есть ли настройки у драйвера
        String currentDrv = preferences.getString(
                getResources().getString(R.string.rfidDriverListPrefKey), null);
        showRfidDriverScreen(currentDrv);

        Preference button = this.findPreference(getString(R.string.load_test_data));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LoadTestData.LoadAllTestData2();
                return true;
            }
        });

        Preference button2 = this.findPreference(getString(R.string.delete_test_data));
        button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LoadTestData.DeleteSomeData();
                return true;
            }
        });

        //https://api.telegram.org/bot<Bot_token>/sendMessage?chat_id=<chat_id>&text=Привет%20мир
        SharedPreferences sharedPref = mainActivityConnector.getSharedPreferences("messengers", Context.MODE_PRIVATE);
        String chat_id = sharedPref.getString(getString(R.string.telegram_chat_id), "0");
        Preference telegramChatId = (Preference) findPreference(getString(R.string.telegram_chat_id));
        telegramChatId.setTitle("Идентификатор чата " + chat_id);

        Preference telegramPreference = findPreference(getString(R.string.receive_telegram));
        telegramPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(mainActivityConnector, "Пожалуйста отправьте любое сообщение боту toirus", Toast.LENGTH_SHORT).show();
                Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
                telegramIntent.setData(Uri.parse("http://telegram.me/toirus_bot"));
                //startActivity(telegramIntent);
                startActivityForResult(telegramIntent, ACTIVITY_TELEGRAM);
                return true;
            }
        });

        // элемент интерфейса со списком драйверов считывателей
        ListPreference langList = (ListPreference) this.findPreference(getResources().getString(
                R.string.langListKey));
        langList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String) newValue;
                setLocale(value);
                return true;
            }
        });

        Preference changeUrl = findPreference(getString(R.string.serverUrl));
/*
        changeUrl.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
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
                return false;
            }
        });
*/
    }

    void showRfidDriverScreen(String value) {
        // проверяем есть ли настройки у драйвера
        if (value != null && isDriverSettingsScreen(value, driverSettingScr)) {
            basicSettingScr.addPreference(driverSettingScr);
        } else {
            basicSettingScr.removePreference(driverSettingScr);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean isDriverSettingsScreen(String classPath, PreferenceScreen rootScreen) {

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
            // TODO Олег временно убрал 14.11.2019
            //screen = driver.getSettingsScreen(rootScreen);
            //if (screen == null) {
            //    return false;
            //}
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_TELEGRAM:
                // https://api.telegram.org/bot489333537:AAFWzSpAuWl0v1KJ3sTQKYABpjY0ERgcIcY/getUpdates
                AsyncRequest ar = new AsyncRequest();
                ar.setListener(new AsyncRequest.Listener() {
                    @Override
                    public void onSuccess(String chat_id) {
                        SharedPreferences sharedPreferences =
                                mainActivityConnector.getSharedPreferences("messendgers",
                                        Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getResources().getString(R.string.telegram_chat_id), chat_id);
                        editor.apply();

                        String msg = "Система Тоирус привествует Вас!"
                                + " Теперь Вы будете получать уведомления в этом чате"
                                + " " + chat_id;
                        new MainFunctions().sendMessageToTelegram(mainActivityConnector, msg);
                    }
                });
                ar.execute();
                break;
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mainActivityConnector.getBaseContext().getResources().updateConfiguration(config,
                mainActivityConnector.getBaseContext().getResources().getDisplayMetrics());
        mainActivityConnector.getResources().updateConfiguration(config,
                mainActivityConnector.getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(mainActivityConnector, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity mainActivityConnector = getActivity();
        if (mainActivityConnector == null) {
            onDestroyView();
        }
    }

    static class AsyncRequest extends AsyncTask<String, Integer, String> {

        private SettingsFragment.AsyncRequest.Listener listener;

        @Override
        protected String doInBackground(String... arg) {
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://api.telegram.org/" + BOT + "/getUpdates");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                String jsonString = result.toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray res = jsonObject.getJSONArray("result");
                if (res.length() > 0) {
                    JSONObject res0 = res.getJSONObject(0);
                    JSONObject message = res0.getJSONObject("message");
                    JSONObject chat = message.getJSONObject("chat");
                    int chat_id = chat.getInt("id");
                    if (chat_id > 0) {
                        // store chat id
                        return String.valueOf(chat_id);
                    }

                    return "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (listener != null) {
                listener.onSuccess(s);
            }
        }

        void setListener(SettingsFragment.AsyncRequest.Listener listener) {
            this.listener = listener;
        }

        interface Listener {
            void onSuccess(String object);
        }
    }
}
