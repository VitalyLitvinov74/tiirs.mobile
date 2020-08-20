package ru.toir.mobile.multi;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;
import com.roughike.bottombar.BottomBar;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import me.leolin.shortcutbadger.ShortcutBadger;
import ru.toir.mobile.multi.db.ToirRealm;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.fragments.ContragentsFragment;
import ru.toir.mobile.multi.fragments.DefectsFragment;
import ru.toir.mobile.multi.fragments.DocumentationFragment;
import ru.toir.mobile.multi.fragments.EquipmentsFragment;
import ru.toir.mobile.multi.fragments.GPSFragment;
import ru.toir.mobile.multi.fragments.ObjectFragment;
import ru.toir.mobile.multi.fragments.OrderFragment;
import ru.toir.mobile.multi.fragments.ReferenceFragment;
import ru.toir.mobile.multi.fragments.ServiceFragment;
import ru.toir.mobile.multi.fragments.SettingsFragment;
import ru.toir.mobile.multi.fragments.UserInfoFragment;
import ru.toir.mobile.multi.gps.GPSListener;
import ru.toir.mobile.multi.rest.ForegroundService;
import ru.toir.mobile.multi.rest.ToirKeyStoreFactory;
import ru.toir.mobile.multi.rfid.RfidDialog;
import ru.toir.mobile.multi.rfid.RfidDriverBase;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;
import ru.toir.mobile.multi.rfid.driver.RfidDriverNfc;
import ru.toir.mobile.multi.serverapi.GetToken;
import ru.toir.mobile.multi.serverapi.GetUser;
import ru.toir.mobile.multi.utils.AuthLocal;
import ru.toir.mobile.multi.utils.MainFunctions;

import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

public class MainActivity extends AppCompatActivity {
    //private static final int PROFILE_ADD = 1;
    //private static final int PROFILE_SETTINGS = 2;
    //private static final int FRAGMENT_CAMERA = 1;
    //private static final int FRAGMENT_OTHER = 10;
    //private static final int DRAWER_TASKS = 11;
    //private static final int DRAWER_ONLINE = 15;

    public static final String START_GET_TOKEN_TIMER = ToirApplication.packageName + ".startGetTokenTimer";
    private static final int MAX_USER_PROFILE = 10;
    private static final int NO_FRAGMENT = 0;
    private static final int FRAGMENT_CHARTS = 2;
    private static final int FRAGMENT_EQUIPMENT = 3;
    private static final int FRAGMENT_GPS = 4;
    private static final int FRAGMENT_TASKS = 5;
    private static final int FRAGMENT_REFERENCES = 6;
    private static final int FRAGMENT_USERS = 7;
    private static final int FRAGMENT_USER = 8;
    private static final int FRAGMENT_DOCS = 9;
    private static final int FRAGMENT_SETTINGS = 11;
    private static final int DRAWER_DOWNLOAD = 12;
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;
    private static final int FRAGMENT_SERVICE = 15;
    private static final int FRAGMENT_OBJECTS = 16;
    private static final int FRAGMENT_CONTRAGENTS = 17;
    private static final int FRAGMENT_DEFECTS = 18;
    private static final String TAG = "MainActivity";
    private static boolean isExitTimerStart = false;
    public int currentFragment = NO_FRAGMENT;
    Bundle savedInstance = null;
    int activeUserID = 0;
    BroadcastReceiver networkReceiver;
    BroadcastReceiver logInReceiver;
    BroadcastReceiver authLocalReceiver;
    BroadcastReceiver getTokenReceiver;
    BroadcastReceiver getUserReceiver;
    BroadcastReceiver checkGetTokenTimerReceiver;
    private RfidDialog rfidDialog;
    private AccountHeader headerResult = null;
    private ArrayList<IProfile> iprofilelist;
    private long users_id[];
    private int cnt = 0;
    private ProgressDialog authorizationDialog;
    private boolean splashShown = false;
    private Realm realmDB;
    private Drawer.OnDrawerItemClickListener onDrawerItemClickListener = (view, i, iDrawerItem) -> {
        Log.d(TAG, "onDrawerItemClick");
        return false;
    };
    private LocationManager _locationManager;
    private GPSListener _gpsListener;
    private Thread checkGPSThread;
    private String locationBestProvider = LocationManager.GPS_PROVIDER;
    private boolean GPSPresent = false;
    private Handler checkGetTokenTimer;

    public static Locale getLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString("lang_key", "ru");
        return new Locale(lang);
    }

    public static void updateApk(Activity context) {
        if (ToirApplication.serverUrl.equals("")) {
            Toast.makeText(context,
                    context.getString(R.string.not_set_server_address), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(context.getString(R.string.sync_data));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setMax(100);
        final Downloader downloaderTask = new Downloader(dialog);
        dialog.setOnCancelListener(dialog1 -> downloaderTask.cancel(true));

        String fileName = "toir.apk";
        String updateUrl = ToirApplication.serverUrl + "/app/" + fileName;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File outputFile = new File(file, fileName);
        downloaderTask.execute(updateUrl, outputFile.toString());
        dialog.show();
    }

    static void initAcra(AuthorizedUser authorizedUser, Application application) {
        String token = authorizedUser.getToken();
        String login = authorizedUser.getLogin();
        if (token == null || login == null) {
            return;
        }

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(application)
                .setBuildConfigClass(BuildConfig.class)
                .setReportFormat(StringFormat.JSON);
        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
                .setKeyStoreFactoryClass(ToirKeyStoreFactory.class)
                .setUri(ToirApplication.serverUrl.concat("/crash?XDEBUG_SESSION_START=xdebug&token=").concat(authorizedUser.getToken()).concat("&apiuser=").concat(authorizedUser.getLogin()))
                .setHttpMethod(HttpSender.Method.POST)
                .setEnabled(true);
        ACRA.init(application, builder);
    }

    /**
     * Инициализация приложения при запуске
     */
    public void init() {

        // обнуляем текущего активного пользователя
        AuthorizedUser.getInstance().reset();

        // просто принудительно инициализируем базу, в которой ни чего не будет
        // база будет базой по умолчанию, пока не будет инициализирована рабочая база
        ToirRealm.initDb(this, "default");

        // запускаем сервис который будет в фоне заниматься получением/отправкой данных
        // TODO: пересмотреть работу сервиса. отказаться от постоянной работы в фоне.
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale();
        savedInstance = savedInstanceState;

        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conn = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conn == null) {
                    return;
                }

                boolean isConnected = false;
                NetworkInfo netInfo = conn.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isConnected = netInfo.isConnected();
                } else if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isConnected = netInfo.isConnected();
                }

                AuthorizedUser authUser = AuthorizedUser.getInstance();
                if (isConnected && authUser.isLocalLogged() && !authUser.isServerLogged()) {
                    sendBroadcast(new Intent(GetToken.GET_TOKEN_ACTION));
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        getTokenReceiver = new GetToken();
        registerReceiver(getTokenReceiver, new IntentFilter(GetToken.GET_TOKEN_ACTION));

        authLocalReceiver = new AuthLocal();
        registerReceiver(authLocalReceiver, new IntentFilter(AuthLocal.AUTH_LOCAL_ACTION));

        logInReceiver = new LogIn();
        registerReceiver(logInReceiver, new IntentFilter(AuthLocal.LOG_IN_ACTION));

        getUserReceiver = new GetUser();
        registerReceiver(getUserReceiver, new IntentFilter(GetUser.GET_USER_ACTION));

        checkGetTokenTimerReceiver = new GetTokenTimerReceiver();
        registerReceiver(checkGetTokenTimerReceiver, new IntentFilter(START_GET_TOKEN_TIMER));

        // инициализация приложения
        init();
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean isLogged = authUser.isLogged();
        Log.d(TAG, "onCreate:before read: isLogged=" + isLogged);
        if (savedInstanceState != null) {
            isLogged = savedInstanceState.getBoolean("isLogged");
            Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
            splashShown = savedInstanceState.getBoolean("splashShown");
            AuthorizedUser aUser = AuthorizedUser.getInstance();
            aUser.setTagId(savedInstanceState.getString("tagId"));
            aUser.setToken(savedInstanceState.getString("token"));
            aUser.setUuid(savedInstanceState.getString("userUuid"));
            aUser.setOrganizationUuid(savedInstanceState.getString("organizationUuid"));
            aUser.setLogin(savedInstanceState.getString("userLogin"));
            aUser.setLogged(isLogged);
            aUser.setIdentity(savedInstanceState.getString("identity"));
            aUser.setServerLogged(savedInstanceState.getBoolean("isServerLogged"));
            aUser.setLocalLogged(savedInstanceState.getBoolean("isLocalLogged"));
            aUser.setLoginType(savedInstanceState.getInt("loginType"));
            aUser.setPassword(savedInstanceState.getString("password"));
            aUser.setDbName(savedInstanceState.getString("dbName"));
        }

        Log.d(TAG, "onCreate");
        if (splashShown) {
            if (isLogged) {
                setMainLayout(savedInstanceState);
            } else {
                setContentView(R.layout.login_layout);
                ShowSettings();
            }
        }
    }

    /**
     *
     */
    public void startAuthorise() {
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        authUser.setLogged(false);

        final Handler handler = new Handler(message -> {
            Log.d(TAG, "Получили сообщение из драйвера.");

            if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                final RfidDriverMsg rfidData = (RfidDriverMsg) message.obj;
                AuthorizedUser authUser1 = AuthorizedUser.getInstance();
                authUser1.setLoginType(rfidData.getType());
                String identifier;
                String password;
                if (rfidData.isTag()) {
                    identifier = rfidData.getTagId();
                    authUser1.setTagId(identifier);
                } else {
                    identifier = rfidData.getLogin();
                    authUser1.setLogin(identifier);
                    password = rfidData.getPassword();
                    authUser1.setPassword(password);
                }

                authUser1.setIdentity(identifier);
                Log.d(TAG, identifier);

                // проверяем, есть соединение с инетом или нет
                if (!ToirApplication.isInternetOn(getApplicationContext())) {
                    // соединения с инетом нет, аутентифицируем в локальной базе
                    Intent result = new Intent(AuthLocal.AUTH_LOCAL_ACTION);
                    sendBroadcast(result);

                    Toast.makeText(getApplicationContext(), getText(R.string.no_internet),
                            Toast.LENGTH_LONG).show();
                } else {
                    // соединение с инетом есть, шлём запрос серверу
                    Intent result = new Intent(GetToken.GET_TOKEN_ACTION);
                    sendBroadcast(result);
                }

                // показываем диалог входа
                authorizationDialog = new ProgressDialog(MainActivity.this);
                authorizationDialog.setMessage(getString(R.string.toast_enter));
                authorizationDialog.setIndeterminate(true);
                authorizationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                authorizationDialog.setCancelable(false);
                authorizationDialog.show();
            } else {
                // по кодам из RFID можно показать более подробные сообщения
                Toast.makeText(getApplicationContext(),
                        getString(R.string.toast_error_operation_canceled),
                        Toast.LENGTH_SHORT).show();
            }

            // закрываем диалог
            rfidDialog.dismiss();
            return true;
        });

        rfidDialog = new RfidDialog();
        rfidDialog.setHandler(handler);
        Context context = getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String driverClass = sp.getString(context.getString(R.string.default_login_rfid_driver_key), "");
        rfidDialog.readTagId(driverClass);
        rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
    }

    /**
     * Включает / отключает кнопку "Войти" на экране входа.
     *
     * @param enable Режим.
     */
    private void setEnabledLoginButton(boolean enable) {
        Button loginButton = findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setEnabled(enable);
            loginButton.setClickable(enable);
        }
    }

    /**
     * Устанавливам основной экран приложения
     */
    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
        MainActivity.initAcra(authorizedUser, this.getApplication());
        setContentView(R.layout.activity_main);
//        SharedPreferences sp = PreferenceManager
//                .getDefaultSharedPreferences(getApplicationContext());
        //service_mode = sp.getBoolean("pref_debug_mode_key", false);
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(this).attach(this).commit();

        final BottomBar bottomBar = findViewById(R.id.bottomBar);
        assert bottomBar != null;
        bottomBar.setOnTabSelectListener(tabId -> {
            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            switch (tabId) {
                case R.id.menu_user:
                    currentFragment = FRAGMENT_USERS;
                    tr.replace(R.id.frame_container, UserInfoFragment.newInstance());
                    break;
                case R.id.menu_orders:
                    currentFragment = FRAGMENT_TASKS;
                    tr.replace(R.id.frame_container, OrderFragment.newInstance());
                    break;
                case R.id.menu_equipments:
                    currentFragment = FRAGMENT_EQUIPMENT;
                    tr.replace(R.id.frame_container, EquipmentsFragment.newInstance());
                    break;
                case R.id.menu_defects:
                    currentFragment = FRAGMENT_DEFECTS;
                    tr.replace(R.id.frame_container, DefectsFragment.newInstance());
                    break;
            }

            tr.commit();
        });
        int new_orders = MainFunctions.getActiveOrdersCount();
        if (new_orders > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_orders);
            ShortcutBadger.applyCount(getApplicationContext(), new_orders);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getString(R.string.subtitle_repair));
        toolbar.setTitleTextColor(Color.WHITE);

        //set the back arrow in the toolbar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        iprofilelist = new ArrayList<>();
        users_id = new long[MAX_USER_PROFILE];

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.header)
                .withHeaderBackground(R.color.larisaBlueColor)
                .withTextColor(ContextCompat.getColor(this, R.color.white))
                .withOnAccountHeaderListener((view, profile, current) -> {
                    if (profile instanceof IDrawerItem) {
                        int ident = profile.getIdentifier();
                        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                        Fragment fr;
                        switch (ident) {
                            default:
                                int profileId = profile.getIdentifier(); //-2
                                int profile_pos;
                                for (profile_pos = 0; profile_pos < iprofilelist.size(); profile_pos++) {
                                    if (users_id[profile_pos] == profileId) {
                                        break;
                                    }
                                }

                                // инициализируем процесс авторизации при смене пользователя
                                startAuthorise();
                                currentFragment = FRAGMENT_USER;
                                fr = UserInfoFragment.newInstance();
                                tr.replace(R.id.frame_container, fr);
                                break;
                        }

                        tr.commit();
                    }

                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .build();

        fillProfileList();

        PrimaryDrawerItem taskPrimaryDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.menu_orders)
                .withDescription(getString(R.string.current_tasks))
                .withIcon(GoogleMaterial.Icon.gmd_calendar)
                .withIdentifier(FRAGMENT_TASKS)
                .withSelectable(false)
                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor));
        if (new_orders > 0) {
            taskPrimaryDrawerItem
                    .withBadge("" + new_orders)
                    .withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.red));
        }
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_users)
                                .withDescription(R.string.menu_user_description)
                                .withIcon(GoogleMaterial.Icon.gmd_account_box)
                                .withIdentifier(FRAGMENT_USERS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_equipment)
                                .withDescription(R.string.menu_equipment_description)
                                .withIcon(GoogleMaterial.Icon.gmd_devices)
                                .withIdentifier(FRAGMENT_EQUIPMENT)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_gps)
                                .withDescription(R.string.menu_equipment_map)
                                .withIcon(GoogleMaterial.Icon.gmd_my_location)
                                .withIdentifier(FRAGMENT_GPS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        taskPrimaryDrawerItem,
/*
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_references)
                                .withDescription(R.string.menu_additional)
                                .withIcon(GoogleMaterial.Icon.gmd_book)
                                .withIdentifier(FRAGMENT_REFERENCES)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_documentation)
                                .withDescription(R.string.menu_on_equipment)
                                .withIcon(GoogleMaterial.Icon.gmd_collection_bookmark)
                                .withIdentifier(FRAGMENT_DOCS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_objects)
                                .withDescription(R.string.menu_objects_description)
                                .withIcon(GoogleMaterial.Icon.gmd_home)
                                .withIdentifier(FRAGMENT_OBJECTS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.contragents)
                                .withDescription(R.string.menu_client_reference)
                                .withIcon(GoogleMaterial.Icon.gmd_accounts_alt)
                                .withIdentifier(FRAGMENT_CONTRAGENTS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
*/
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_defects)
                                .withDescription(R.string.menu_defects)
                                .withIcon(GoogleMaterial.Icon.gmd_wrench)
                                .withIdentifier(FRAGMENT_DEFECTS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.service)
                                .withDescription(R.string.menu_journal_track)
                                .withIcon(GoogleMaterial.Icon.gmd_gps)
                                .withIdentifier(FRAGMENT_SERVICE)
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_about_app)
                                .withDescription(R.string.menu_information)
                                .withIcon(FontAwesome.Icon.faw_info)
                                .withIdentifier(DRAWER_INFO)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_exit)
                                .withIcon(FontAwesome.Icon.faw_undo)
                                .withIdentifier(DRAWER_EXIT)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor))
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        int ident = drawerItem.getIdentifier();
                        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                        if (ident == FRAGMENT_CHARTS) {
                            currentFragment = FRAGMENT_CHARTS;
                            tr.replace(R.id.frame_container, ServiceFragment.newInstance());
                        } else if (ident == DRAWER_DOWNLOAD) {
                            currentFragment = DRAWER_DOWNLOAD;
                            updateApk(MainActivity.this);
                        } else if (ident == FRAGMENT_EQUIPMENT) {
                            currentFragment = FRAGMENT_EQUIPMENT;
                            tr.replace(R.id.frame_container, EquipmentsFragment.newInstance());
                        } else if (ident == FRAGMENT_GPS) {
                            currentFragment = FRAGMENT_GPS;
                            tr.replace(R.id.frame_container, GPSFragment.newInstance());
                        } else if (ident == FRAGMENT_TASKS) {
                            currentFragment = FRAGMENT_TASKS;
                            tr.replace(R.id.frame_container, OrderFragment.newInstance());
                        } else if (ident == FRAGMENT_REFERENCES) {
                            currentFragment = FRAGMENT_REFERENCES;
                            tr.replace(R.id.frame_container, ReferenceFragment.newInstance());
                        } else if (ident == FRAGMENT_DOCS) {
                            currentFragment = FRAGMENT_DOCS;
                            tr.replace(R.id.frame_container, DocumentationFragment.newInstance());
                        } else if (ident == FRAGMENT_USERS) {
                            currentFragment = FRAGMENT_USERS;
                            tr.replace(R.id.frame_container, UserInfoFragment.newInstance());
                        } else if (ident == FRAGMENT_SERVICE) {
                            currentFragment = FRAGMENT_SERVICE;
                            tr.replace(R.id.frame_container, ServiceFragment.newInstance());
                        } else if (ident == FRAGMENT_OBJECTS) {
                            currentFragment = FRAGMENT_OBJECTS;
                            tr.replace(R.id.frame_container, ObjectFragment.newInstance());
                        } else if (ident == FRAGMENT_DEFECTS) {
                            currentFragment = FRAGMENT_DEFECTS;
                            tr.replace(R.id.frame_container, DefectsFragment.newInstance());
                        } else if (ident == FRAGMENT_CONTRAGENTS) {
                            currentFragment = FRAGMENT_CONTRAGENTS;
                            tr.replace(R.id.frame_container, ContragentsFragment.newInstance());
                        } else if (ident == DRAWER_INFO) {
                            startAboutDialog();
                        } else if (ident == DRAWER_EXIT) {
                            System.exit(0);
                        }
                        tr.commit();
                    }

                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //if you have many different types of DrawerItems you can magically pre-cache those items to get a better scroll performance
        //make sure to init the cache after the DrawerBuilder was created as this will first clear the cache to make sure no old elements are in
        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);
            //set the active profile
            if (iprofilelist.size() > 0) {
                for (cnt = 0; cnt < iprofilelist.size(); cnt++) {
                    if (activeUserID > 0 && iprofilelist.get(cnt).getIdentifier() == activeUserID) // +2
                        headerResult.setActiveProfile(iprofilelist.get(cnt));
                }
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();

//        if (activeUserID <= 0) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.please_login), Toast.LENGTH_LONG).show();
//        }

        //((ViewGroup) findViewById(R.id.frame_container)).addView(result.getSlider());
        //pager = (ViewPager) findViewById(R.id.pager);
        //pager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        //PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //tabs.setViewPager(pager);
    }

//    /**
//     * Обработчик клика меню запуска блютус сервера
//     *
//     * @param menuItem - пункт меню
//     */
//    public void onActionBTServer(MenuItem menuItem) {
//        Log.d(TAG, "onActionBTServer");
//        Intent i = new Intent(MainActivity.this, BTServerActivity.class);
//        startActivity(i);
//    }

    /**
     * Обработчик клика кнопки "Войти"
     *
     * @param view Event's view
     */
    public void onClickLogin(View view) {
        if (!isSkipGPS() && !isGpsOn()) {
            Toast.makeText(getApplicationContext(), getString(R.string.gps_must_enabled), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        setEnabledLoginButton(false);
        startAuthorise();
        setEnabledLoginButton(true);
    }

    /**
     * Обработчик клика меню обновления приложения
     *
     * @param menuItem Элемент меню
     */
    public void onActionUpdate(MenuItem menuItem) {
        if (!ToirApplication.isInternetOn(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Нет соединения с сетью", Toast.LENGTH_LONG).show();
            return;
        }

        updateApk(MainActivity.this);
    }

    public void onActionAbout(MenuItem menuItem) {
        Log.d(TAG, "onActionAbout");
        startAboutDialog();
    }

    public void onActionSettings(MenuItem menuItem) {
        Log.d(TAG, "onActionSettings");
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        currentFragment = FRAGMENT_SETTINGS;
        SettingsFragment fragment = new SettingsFragment();
        tr.replace(R.id.frame_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_update) {
            return true;
        } else if (id == R.id.action_about) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        boolean isLogged = authUser.isLogged();
        Log.d(TAG, "onSaveInstanceState: isLogged=" + isLogged);
        outState.putBoolean("isLogged", isLogged);
        outState.putBoolean("splashShown", splashShown);
        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
        outState.putString("tagId", authorizedUser.getTagId());
        outState.putString("token", authorizedUser.getToken());
        outState.putString("userUuid", authorizedUser.getUuid());
        outState.putString("organizationUuid", authorizedUser.getOrganizationUuid());
        outState.putString("userLogin", authorizedUser.getLogin());
        outState.putString("identity", authorizedUser.getIdentity());
        outState.putBoolean("isServerLogged", authorizedUser.isServerLogged());
        outState.putBoolean("isLocalLogged", authorizedUser.isLocalLogged());
        outState.putInt("loginType", authorizedUser.loginType());
        outState.putString("password", authorizedUser.getPassword());
        outState.putString("dbName", authorizedUser.getDbName());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /* функция заполняет массив профилей - пользователей */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExitTimerStart) {
                return super.onKeyDown(keyCode, event);
            } else if (currentFragment == FRAGMENT_USERS) {
                Handler handler = new Handler();
                Runnable runnable = () -> isExitTimerStart = false;
                handler.postDelayed(runnable, 5000);
                Toast.makeText(this, "Нажмите \"назад\" ещё раз для выхода.", Toast.LENGTH_LONG)
                        .show();
                isExitTimerStart = true;
            } else if (currentFragment == FRAGMENT_SETTINGS) {
                FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                currentFragment = FRAGMENT_USER;
                tr.replace(R.id.frame_container, UserInfoFragment.newInstance());
                tr.commit();
            } else if (findViewById(R.id.loginButton) != null) {
                return super.onKeyDown(keyCode, event);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void fillProfileList() {
        //UsersDBAdapter users = new UsersDBAdapter(
        //        new ToirDatabaseContext(getApplicationContext()));
        //User users = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findAll();
        RealmResults<User> profilesList = realmDB.where(User.class).findAll();
        cnt = 0;
        for (User item : profilesList) {
            addProfile(item);
            users_id[cnt] = item.get_id();
            cnt = cnt + 1;
            if (cnt > MAX_USER_PROFILE) break;
        }
    }

    public void addProfile(User item) {
        IProfile new_profile;
        String path = getExternalFilesDir("/"
                + (new User()).getImageFilePath(AuthorizedUser.getInstance().getDbName()))
                + File.separator;
        if (item.getChangedAt() != null) {
            Bitmap myBitmap = getResizedBitmap(path, item.getImageFileName(),
                    0, 600, item.getChangedAt().getTime());
            new_profile = new ProfileDrawerItem()
                    .withName(item.getName())
                    .withEmail(item.getLogin())
                    // first two elements reserved
                    .withIdentifier((int) item.get_id()) // +2
                    .withOnDrawerItemClickListener(onDrawerItemClickListener);
            if (myBitmap != null) {
                new_profile.withIcon(myBitmap);
            } else {
                new_profile.withIcon(R.drawable.profile_default_small);
            }

            iprofilelist.add(new_profile);
            headerResult.addProfile(new_profile, headerResult.getProfiles().size());
        }
    }

    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle(getString(R.string.about));
        about.show();
    }

    public void mOnClickMethod(View view) {
        Intent i = new Intent(MainActivity.this, PrefsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        if (checkGPSThread != null) {
            checkGPSThread.interrupt();
        }
    }

    public void ShowSettings() {
        TextView driver, system_server;
        driver = findViewById(R.id.login_current_driver);
        system_server = findViewById(R.id.login_current_system_server);

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String serverUrl = sp.getString(getString(R.string.serverUrl), "");
        String classPath = sp.getString(getString(R.string.default_rfid_driver_key), "");
        String driverName = RfidDriverBase.getDriverName(classPath);
        if (driverName == null) {
            driverName = getString(R.string.no_driver_select);
        } else if (ru.toir.mobile.multi.rfid.driver.RfidDriverQRcode.class.getName().equals(classPath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 111);
                }
            }
        }

        //sp.getString(getString(R.string.updateUrl), "");
        // указываем названия и значения для элементов списка
        if (driver != null) {
            driver.setText(driverName);
        }

        if (system_server != null) {
            system_server.setText(serverUrl);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        if (realmDB != null) {
            realmDB.close();
        }

        if (_locationManager != null) {
            if (_gpsListener != null) {
                _locationManager.removeUpdates(_gpsListener);
            }

            _locationManager = null;
            _gpsListener = null;
        }

        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }

        if (logInReceiver != null) {
            unregisterReceiver(logInReceiver);
        }

        if (authLocalReceiver != null) {
            unregisterReceiver(authLocalReceiver);
        }

        if (getTokenReceiver != null) {
            unregisterReceiver(getTokenReceiver);
        }

        if (getUserReceiver != null) {
            unregisterReceiver(getUserReceiver);
        }

        if (checkGetTokenTimerReceiver != null) {
            unregisterReceiver(checkGetTokenTimerReceiver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            final BottomBar bottomBar = findViewById(R.id.bottomBar);
            assert bottomBar != null;
            int count = intent.getIntExtra("count", 0);
            if (extras.containsKey("action")) {
                String fragment = intent.getStringExtra("action");
                if (fragment.equals("defectFragment")) {
                    if (count > 0) {
                        bottomBar.getTabAtPosition(3).setBadgeCount(count);
                    }
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.replace(R.id.frame_container, DefectsFragment.newInstance());
                    tr.commit();
                }
                if (fragment.equals("orderFragment")) {
                    if (count > 0) {
                        bottomBar.getTabAtPosition(1).setBadgeCount(count);
                    }
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.replace(R.id.frame_container, OrderFragment.newInstance());
                    tr.commit();
                }
            }
        }

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            StringBuilder hexId = new StringBuilder();
            for (byte b : id) {
                hexId.append(String.format("%02X", b));
            }

            Intent result = new Intent(RfidDriverNfc.ACTION_NFC);
            result.putExtra("tagId", hexId.toString());
            sendBroadcast(result);
        }
    }

    /**
     * Проверка включен ли GPS.
     *
     * @return boolean
     */
    private boolean isGpsOn() {
        if (GPSPresent) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return lm != null && lm.isProviderEnabled(locationBestProvider);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
                }
            }

            return true;
        }
    }

    /**
     * Проверка на необходимость GPS
     */
    private boolean isSkipGPS() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        return sp.getBoolean(getString(R.string.debug_nocheck_gps), false);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        AuthorizedUser authUser = AuthorizedUser.getInstance();
        boolean isLogged = authUser.isLogged();
        if (!splashShown) {
            // показываем приветствие
            setContentView(R.layout.start_screen);

            // запускаем таймер для показа экрана входа
            Handler h = new Handler();
            h.postDelayed(() -> {
                splashShown = true;

                if (isLogged) {
                    setMainLayout(savedInstance);
                } else {
                    setContentView(R.layout.login_layout);
                    ImageView iW = findViewById(R.id.login_header);
                    if (iW != null) {
                        SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        String serverUrl = sp.getString(getString(R.string.serverUrl), "");
                        if (serverUrl.contains("qwvostok"))
                            iW.setImageResource(R.drawable.quarzwerke_logo_kt);
                    }
                    ShowSettings();
                }
            }, 3000);
        }

        if (splashShown && !isLogged) {
            ShowSettings();
        }

        /*
        AuthorizedUser user = AuthorizedUser.getInstance();
        if (user.getTagId() == null) {
            // пользователь не вошел в программу, либо потеряна по каким-то причинам информация
            // о текущем пользователе, показываем экран входа
            setContentView(R.layout.login_layout);
        }
        ShowSettings();*/

        Runnable run = () -> {
            boolean isRun = true;
            while (isRun) {
                try {
                    Thread.sleep(5000);
                    if (!isSkipGPS()) {
                        if (!isGpsOn()) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), getString(R.string.gps_must_enabled),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    isRun = false;
                }
            }
        };
        checkGPSThread = new Thread(run);
        checkGPSThread.start();

        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (_locationManager != null && permission == PackageManager.PERMISSION_GRANTED) {
            _gpsListener = new GPSListener();
            try {
                _locationManager.requestLocationUpdates(locationBestProvider, 10000, 10, _gpsListener);
                GPSPresent = true;
            } catch (Exception e) {
                e.printStackTrace();
                GPSPresent = false;
            }

            if (!GPSPresent) {
                Toast.makeText(this, getString(R.string.device_has_no_gps), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_main);
    }

    private void setLocale() {
        final Resources resources = getResources();
        final Configuration configuration = resources.getConfiguration();
        final Locale locale = getLocale(this);
        if (!configuration.locale.equals(locale)) {
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, null);
        }
    }

    private class LogIn extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String extraAction = intent.getStringExtra(AuthLocal.EXTRA_ACTION_NAME);
            AuthorizedUser authUser = AuthorizedUser.getInstance();

            switch (extraAction) {
                case AuthLocal.ACCESS_ALLOWED:
                    authUser.setLogged(true);
                    authorizationDialog.dismiss();
                    realmDB = Realm.getDefaultInstance();
                    setMainLayout(savedInstance);
                    // сохраняем логин по умолчанию только для варианта с пинкодом
                    if (authUser.loginType() == RfidDriverMsg.TYPE_LOGIN) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                        sp.edit().putString("defaultLogin", authUser.getLogin()).apply();
                    }

                    break;
                case AuthLocal.ACCESS_DENIED:
                    authUser.reset();
                    authorizationDialog.dismiss();
                    setContentView(R.layout.login_layout);
                    ShowSettings();
                    break;
            }
        }
    }

    private class GetTokenTimerReceiver extends BroadcastReceiver {
        private static final int timeout = 60000;

        @Override
        public void onReceive(Context context, Intent intent) {
            // запускаем таймер для проверки наличия токена в ситуации когда связь есть а токен не получен
            if (checkGetTokenTimer == null) {
                new Handler().postDelayed(this::startCheckGetToken, timeout);
            }
        }

        private void startCheckGetToken() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "star check token");
                    AuthorizedUser authUser = AuthorizedUser.getInstance();
                    if (authUser.getToken() != null) {
                        // токен уже есть, останавливаем проверку
                        return;
                    }

                    if (!ToirApplication.isInternetOn(MainActivity.this)) {
                        // связи нет, ни чего не делаем
                        // взводим следующий запуск
                        checkGetTokenTimer.postDelayed(this, timeout);
                        return;
                    }

                    if (authUser.isLogged() && !authUser.isServerLogged()) {
                        sendBroadcast(new Intent(GetToken.GET_TOKEN_ACTION));
                    }

                    // взводим следующий запуск
                    checkGetTokenTimer.postDelayed(this, timeout);
                }
            };

            checkGetTokenTimer = new Handler();
            checkGetTokenTimer.postDelayed(runnable, timeout);
        }
    }
}
