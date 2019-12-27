package ru.toir.mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.LoadTestData;
import ru.toir.mobile.utils.MainFunctions;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private static final String TAG = "ToirSettings";
    private static final String BOT = "bot489333537:AAFWzSpAuWl0v1KJ3sTQKYABpjY0ERgcIcY";
    private static final int ACTIVITY_TELEGRAM = 1;
    private PreferenceScreen basicSettingScr;
    private PreferenceScreen driverSettingScr;

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = (Toolbar) bar.getChildAt(0);

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        String appVersion;
        try {
            PackageManager pm = getPackageManager();
            String packageName = getPackageName();
            getApplicationContext().getClassLoader();

            PackageInfo pInfo = pm.getPackageInfo(packageName, 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }

        Log.d(TAG, "version:" + appVersion);

        setupSimplePreferencesScreen();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

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
        ListPreference drvList = (ListPreference) this.findPreference(getResources().getString(
                R.string.rfidDriverListPrefKey));

        basicSettingScr = (PreferenceScreen) this.findPreference("preferenceBasicScreen");
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
                //LoadTestData.LoadAllTestData2();
                return true;
            }
        });

        Preference button2 = this.findPreference(getString(R.string.delete_test_data));
        button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //LoadTestData.DeleteSomeData();
                return true;
            }
        });

        //https://api.telegram.org/bot<Bot_token>/sendMessage?chat_id=<chat_id>&text=Привет%20мир
        SharedPreferences sharedPref = getSharedPreferences("messendgers", Context.MODE_PRIVATE);
        String chat_id = sharedPref.getString(getString(R.string.telegram_chat_id), "0");
        Preference telegramChatId = findPreference(getString(R.string.telegram_chat_id));
        telegramChatId.setTitle("Идентификатор чата " + chat_id);

        Preference telegramPreference = findPreference(getString(R.string.receive_telegram));
        telegramPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getApplication(), "Пожалуйста отправьте любое сообщение боту toirus", Toast.LENGTH_SHORT).show();
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
    }

    void showRfidDriverScreen(String value) {
        // проверяем есть ли настройки у драйвера
        if (value != null && isDriverSettingsScreen(value, driverSettingScr)) {
            basicSettingScr.addPreference(driverSettingScr);
        } else {
            basicSettingScr.removePreference(driverSettingScr);
        }
    }

    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            Log.d("AA", "bb");
            return result;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            //toolbar.setBackgroundColor(getResources().getColor(R.color.larisaBlueColor));
            //toolbar.setSubtitle("Обслуживание и ремонт");
            toolbar.setTitleTextColor(Color.WHITE);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        if (preference != null) {
            if (preference instanceof PreferenceScreen) {
                Dialog dialog = ((PreferenceScreen) preference).getDialog();
                if (dialog != null) {
                    Window window = dialog.getWindow();
                    if (window != null) {
                        Drawable.ConstantState constantState = this.getWindow().getDecorView().getBackground().getConstantState();
                        if (constantState != null) {
                            window.getDecorView().setBackgroundDrawable(constantState.newDrawable());
                        }
                    }
                }

                setUpNestedScreen((PreferenceScreen) preference);
            }
        }

        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        AppBarLayout appBar;

        View listRoot = dialog.findViewById(android.R.id.list);
        ViewGroup mRootView = dialog.findViewById(android.R.id.content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FrameLayout root = (FrameLayout) listRoot.getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FrameLayout root = (FrameLayout) listRoot.getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) listRoot.getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else {
            ListView content = (ListView) mRootView.getChildAt(0);
            mRootView.removeAllViews();

            LinearLayout LL = new LinearLayout(this);
            LL.setOrientation(LinearLayout.VERTICAL);

            ViewGroup.LayoutParams LLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LL.setLayoutParams(LLParams);

            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, mRootView, false);

            LL.addView(appBar);
            LL.addView(content);

            mRootView.addView(LL);
        }

        if (listRoot != null) {
            listRoot.setPadding(0, listRoot.getPaddingTop(), 0, listRoot.getPaddingBottom());
        }

        Toolbar Tbar = (Toolbar) appBar.getChildAt(0);

        Tbar.setTitle(preferenceScreen.getTitle());
        Tbar.setTitleTextColor(Color.WHITE);
        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        this.findPreference(getResources().getString(R.string.serverUrl))
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
                                            //String tURL2 = tURL.toString().replaceAll("/+$", "");
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
            screen = driver.getSettingsScreen(rootScreen);
            if (screen == null) {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }

        return true;
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        // do what ever you want with this key
        if (key.equals(getString(R.string.serverUrl))) {
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
        }

        return true;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return super.isValidFragment(fragmentName);
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
                        SharedPreferences sharedPreferences = getSharedPreferences("messendgers",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getResources().getString(R.string.telegram_chat_id), chat_id);
                        editor.apply();

                        String msg = "Система Тоирус привествует Вас!"
                                + " Теперь Вы будете получать уведомления в этом чате"
                                + " " + chat_id;
                        new MainFunctions().sendMessageToTelegram(getApplicationContext(), msg);
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
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    static class AsyncRequest extends AsyncTask<String, Integer, String> {

        private Listener listener;

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

        void setListener(Listener listener) {
            this.listener = listener;
        }

        interface Listener {
            void onSuccess(String object);
        }
    }
}

