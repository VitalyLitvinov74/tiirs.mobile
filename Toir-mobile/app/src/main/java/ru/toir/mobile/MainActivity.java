package ru.toir.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.fragments.ContragentsFragment;
import ru.toir.mobile.fragments.DocumentationFragment;
import ru.toir.mobile.fragments.EquipmentsFragment;
import ru.toir.mobile.fragments.FragmentAddUser;
import ru.toir.mobile.fragments.FragmentEditUser;
import ru.toir.mobile.fragments.GPSFragment;
import ru.toir.mobile.fragments.ObjectFragment;
import ru.toir.mobile.fragments.OrderFragment;
import ru.toir.mobile.fragments.ReferenceFragment;
import ru.toir.mobile.fragments.ServiceFragment;
import ru.toir.mobile.fragments.UserInfoFragment;
import ru.toir.mobile.gps.GPSListener;
import ru.toir.mobile.rest.SendGPSnLogService;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.driver.RfidDriverNfc;
import ru.toir.mobile.serverapi.TokenSrv;
import ru.toir.mobile.utils.MainFunctions;

import static ru.toir.mobile.utils.MainFunctions.addToJournal;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_ADD = 1;
    private static final int PROFILE_SETTINGS = 2;
    private static final int MAX_USER_PROFILE = 10;

    private static final int NO_FRAGMENT = 0;
    //private static final int FRAGMENT_CAMERA = 1;
    private static final int FRAGMENT_CHARTS = 2;
    private static final int FRAGMENT_EQUIPMENT = 3;
    private static final int FRAGMENT_GPS = 4;
    private static final int FRAGMENT_TASKS = 5;
    private static final int FRAGMENT_REFERENCES = 6;
    private static final int FRAGMENT_USERS = 7;
    private static final int FRAGMENT_USER = 8;
    private static final int FRAGMENT_DOCS = 9;
    //private static final int FRAGMENT_OTHER = 10;

    //private static final int DRAWER_TASKS = 11;
    private static final int DRAWER_DOWNLOAD = 12;
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;
    private static final int FRAGMENT_SERVICE = 15;
    //private static final int DRAWER_ONLINE = 15;
    private static final int FRAGMENT_OBJECTS = 16;
    private static final int FRAGMENT_CONTRAGENTS = 17;

    private static final String TAG = "MainActivity";
    private static final long LOG_AND_GPS_SEND_INTERVAL = 60000;

    private static boolean isExitTimerStart = false;
    public int currentFragment = NO_FRAGMENT;
    Bundle savedInstance = null;
    int activeUserID = 0;
    ProgressDialog mProgressDialog;
    private boolean isLogged = false;
    private RfidDialog rfidDialog;
    private AccountHeader headerResult = null;
    private ArrayList<IProfile> iprofilelist;
    private RealmResults<User> profilesList;
    private long users_id[];
    private int cnt = 0;
    private ProgressDialog authorizationDialog;
    private boolean splashShown = false;
    private Realm realmDB;
    private Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
            Log.d(TAG, "onDrawerItemClick");
            return false;
        }
    };
    private Handler logNGpsHandler;
    private Runnable logNGpsRunnable;

    private LocationManager _locationManager;
    private GPSListener _gpsListener;
    private Thread checkGPSThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;

        // инициализация приложения
        init();
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Log.d(TAG, "onCreate:before read: isLogged=" + isLogged);
        if (savedInstanceState != null) {
            isLogged = savedInstanceState.getBoolean("isLogged");
            Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
            splashShown = savedInstanceState.getBoolean("splashShown");
            AuthorizedUser aUser = AuthorizedUser.getInstance();
            aUser.setTagId(savedInstanceState.getString("tagId"));
            aUser.setToken(savedInstanceState.getString("token"));
            aUser.setUuid(savedInstanceState.getString("userUuid"));
            aUser.setLogin(savedInstanceState.getString("userLogin"));
        }

        Log.d(TAG, "onCreate");

        if (!splashShown) {
            // показываем приветствие
            setContentView(R.layout.start_screen);

            // запускаем таймер для показа экрана входа
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    splashShown = true;

                    if (isLogged) {
                        setMainLayout(savedInstance);
                    } else {
                        setContentView(R.layout.login_layout);
                        ShowSettings();
                    }
                }
            }, 3000);
        } else {
            if (isLogged) {
                setMainLayout(savedInstanceState);
            } else {
                setContentView(R.layout.login_layout);
                ShowSettings();
            }
        }

    }

    /**
     * Инициализация приложения при запуске
     */
    public void init() {
        if (!initDB()) {
            // принудительное обновление приложения
            finish();
        }
    }

    public boolean initDB() {
        boolean success = false;
        try {
            //ToirRealm.init(this);
            // получаем базу realm
            realmDB = Realm.getDefaultInstance();
            //LoadTestData.LoadAllTestData();
            Log.d(TAG, "Realm DB schema version = " + realmDB.getVersion());
            Log.d(TAG, "db.version=" + realmDB.getVersion());
            if (realmDB.getVersion() == 0) {
                Toast toast = Toast.makeText(this, "База данных не актуальна!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                success = true;
            } else {
                Toast toast = Toast.makeText(this, "База данных актуальна!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                success = true;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(this,
                    "Не удалось открыть/обновить базу данных!",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }

        return success;
    }

    /**
     *
     */
    public void startAuthorise() {

        isLogged = false;

        final Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                Log.d(TAG, "Получили сообщение из драйвера.");

                if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                    String tagId = (String) msg.obj;
                    tagId = tagId.substring(4);
                    final String tag = tagId;
                    Log.d(TAG, tagId);

                    AuthorizedUser.getInstance().setTagId(tagId);

                    // показываем диалог входа
                    authorizationDialog = new ProgressDialog(MainActivity.this);
                    authorizationDialog.setMessage("Вход в систему");
                    authorizationDialog.setIndeterminate(true);
                    authorizationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    authorizationDialog.setCancelable(false);
                    authorizationDialog.show();

                    // запрашиваем токен
                    Call<TokenSrv> call = ToirAPIFactory.getTokenService().getByLabel(tagId, TokenSrv.Type.LABEL);
                    call.enqueue(new Callback<TokenSrv>() {
                        @Override
                        public void onResponse(Call<TokenSrv> tokenSrvCall, Response<TokenSrv> response) {
                            AuthorizedUser authUser = AuthorizedUser.getInstance();
                            TokenSrv token = response.body();
                            if (token != null) {

                                authUser.setToken(token.getAccessToken());
                                Toast.makeText(getApplicationContext(),
                                        "Токен получен.", Toast.LENGTH_SHORT).show();
                                // Сохраняем login в AuthorizedUser для дальнейших запросв статики
                                authUser.setLogin(token.getUserName());
                            } else {
                                // Токен не получили, пытаемся найти пользователя в локальной базе
                                Realm realm = Realm.getDefaultInstance();
                                RealmResults<User> user = realm.where(User.class).equalTo("tagId",
                                        authUser.getTagId()).findAll();
                                if (user.size() == 1) {
                                    authUser.setLogin(user.first().getLogin());
                                }

                                realm.close();
                            }

                            // TODO: Если не получили инфу с сервера и из локальной базы - что делать?
                            // ни запросы не сделать, ни логи отправить!

                            // запрашиваем актуальную информацию по пользователю
                            Call<User> call = ToirAPIFactory.getUserService().user();
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> userCall, Response<User> response) {
                                    User user = response.body();
                                    if (user != null) {
                                        final String fileName = user.getImage();
                                        Realm realm = Realm.getDefaultInstance();
                                        realm.beginTransaction();
                                        realm.copyToRealmOrUpdate(user);
                                        realm.commitTransaction();
                                        isLogged = true;
                                        // TODO: нужен метод для установки полей объекта из разных объектов (Token, User...)
                                        AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
                                        authorizedUser.setUuid(user.getUuid());
                                        authorizedUser.setLogin(user.getLogin());
                                        addToJournal("Пользователь " + user.getName() + " с uuid[" + user.getUuid() + "] зарегистрировался на клиенте и получил токен");

                                        // получаем изображение пользователя
                                        String url = ToirApplication.serverUrl + "/storage/" + user.getLogin() + "/" + user.getUuid() + "/" + user.getImage();
                                        Call<ResponseBody> callFile = ToirAPIFactory.getFileDownload().get(url);
                                        callFile.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> responseBodyCall, Response<ResponseBody> response) {
                                                ResponseBody fileBody = response.body();
                                                if (fileBody == null) {
                                                    return;
                                                }

                                                File filePath = getExternalFilesDir("/users");
                                                if (filePath == null) {
                                                    // нет доступа к внешнему накопителю
                                                    return;
                                                }

                                                File file = new File(filePath, fileName);
                                                if (!file.getParentFile().exists()) {
                                                    if (!file.getParentFile().mkdirs()) {
                                                        return;
                                                    }
                                                }

                                                try {
                                                    FileOutputStream fos = new FileOutputStream(file);
                                                    fos.write(fileBody.bytes());
                                                    fos.close();
                                                    // принудительно масштабируем изображение пользоваетеля
                                                    String path = filePath + File.separator;
                                                    Bitmap user_bitmap = getResizedBitmap(path, fileName, 0, 600, Long.MAX_VALUE);
                                                    if (user_bitmap == null) {
                                                        // По какой-то причине не смогли получить
                                                        // уменьшенное изображение
                                                        Log.e(TAG, "Не удалось получить масштабированное изображение.");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> responseBodyCall, Throwable t) {
                                                t.printStackTrace();
                                            }
                                        });
                                        setMainLayout(savedInstance);
                                        //user_changed = true;
                                        changeActiveProfile(user);

                                        realm.close();
                                    } else {
                                        String message = "Информация о пользователе не получена с сервера.";
                                        addToJournal("Информация о пользователе с ID " + tag + " не получена с сервера.");
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }

                                    authorizationDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<User> userCall, Throwable t) {
                                    // сообщаем описание неудачи
                                    // TODO нужен какой-то механизм уведомления о причине не успеха
//                                    String message = bundle.getString(IServiceProvider.MESSAGE);
                                    String message = "Просто не получили пользователя.";
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    authorizationDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<TokenSrv> tokenSrvCall, Throwable t) {
                            // TODO нужен какой-то механизм уведомления о причине не успеха
//                            String message = bundle.getString(IServiceProvider.MESSAGE);
                            String message = "Просто не получили токен.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            // TODO реализовать проверку на то что пользователя нет на сервере
                            // токен не получен, сервер не ответил...

                            // проверяем наличие пользователя в локальной базе
                            User user = realmDB.where(User.class)
                                    // !!!!!
                                    .equalTo("tagId", AuthorizedUser.getInstance().getTagId())
                                    .findFirst();

                            // в зависимости от результата либо дать работать, либо не дать
                            if (user != null && user.isActive()) {
                                isLogged = true;
                                //user_changed = true;
                                changeActiveProfile(user);

                                AuthorizedUser.getInstance().setUuid(user.getUuid());
                                addToJournal("Пользователь " + user.getName() + " с uuid[" + user.getUuid() + "] зарегистрировался на клиенте");
                                setMainLayout(savedInstance);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Нет доступа.", Toast.LENGTH_LONG).show();
                            }

                            authorizationDialog.dismiss();
                        }
                    });
                } else {
                    // по кодам из RFID можно показать более подробные сообщения
                    Toast.makeText(getApplicationContext(),
                            "Операция прервана", Toast.LENGTH_SHORT).show();
                }

                // закрываем диалог
                rfidDialog.dismiss();
                //setEnabledLoginButton(true);
                return true;
            }
        });

        rfidDialog = new RfidDialog();
        rfidDialog.setHandler(handler);
        rfidDialog.readTagId();
        rfidDialog.show(getFragmentManager(), RfidDialog.TAG);

    }

    /**
     * Включает / отключает кнопку "Войти" на экране входа.
     *
     * @param enable Режим.
     */
    private void setEnabledLoginButton(boolean enable) {
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setEnabled(enable);
        loginButton.setClickable(enable);
    }

    /**
     * Устанавливам основной экран приложения
     */
    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
//        SharedPreferences sp = PreferenceManager
//                .getDefaultSharedPreferences(getApplicationContext());
        //service_mode = sp.getBoolean("pref_debug_mode_key", false);
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(this).attach(this).commit();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        assert bottomBar != null;
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
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
                    case R.id.menu_maps:
                        currentFragment = FRAGMENT_GPS;
                        tr.replace(R.id.frame_container, GPSFragment.newInstance());
                        break;
                }

                tr.commit();
            }
        });
        int new_orders = MainFunctions.getActiveOrdersCount();
        if (new_orders > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_orders);
            ShortcutBadger.applyCount(getApplicationContext(), new_orders);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Обслуживание и ремонт");
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
                .addProfiles(
                        new ProfileSettingDrawerItem()
                                .withName("Добавить пользователя")
                                .withDescription("Добавить пользователя")
                                .withIcon(String.valueOf(GoogleMaterial.Icon.gmd_plus))
                                .withIdentifier(PROFILE_ADD),
                        new ProfileSettingDrawerItem()
                                .withName("Редактировать пользователей")
                                .withIcon(String.valueOf(GoogleMaterial.Icon.gmd_settings))
                                .withIdentifier(PROFILE_SETTINGS)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

                        if (profile instanceof IDrawerItem) {
                            int ident = profile.getIdentifier();
                            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                            Fragment fr;
                            switch (ident) {
                                case PROFILE_ADD:
                                    currentFragment = FRAGMENT_USER;
                                    fr = FragmentAddUser.newInstance();
                                    tr.replace(R.id.frame_container, fr);
                                    break;
                                case PROFILE_SETTINGS:
                                    currentFragment = FRAGMENT_USER;
                                    fr = FragmentEditUser.newInstance("EditProfile");
                                    tr.replace(R.id.frame_container, fr);
                                    break;
                                default:
                                    int profileId = profile.getIdentifier() - 2;
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
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        fillProfileList();

        PrimaryDrawerItem taskPrimaryDrawerItem = new PrimaryDrawerItem()
                .withName(R.string.menu_tasks)
                .withDescription("Текущие задания")
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
                                .withDescription("Информация о пользователе")
                                .withIcon(GoogleMaterial.Icon.gmd_account_box)
                                .withIdentifier(FRAGMENT_USERS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
//                        new PrimaryDrawerItem()
//                                .withName(R.string.menu_camera)
//                                .withDescription("Проверка камеры")
//                                .withIcon(GoogleMaterial.Icon.gmd_camera)
//                                .withIdentifier(FRAGMENT_CAMERA)
//                                .withSelectable(false),
//                        new PrimaryDrawerItem()
//                                .withName(R.string.menu_charts)
//                                .withDescription("Графический пакет")
//                                .withIcon(GoogleMaterial.Icon.gmd_chart)
//                                .withIdentifier(FRAGMENT_CHARTS)
//                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_equipment)
                                .withDescription("Справочник оборудования")
                                .withIcon(GoogleMaterial.Icon.gmd_devices)
                                .withIdentifier(FRAGMENT_EQUIPMENT)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_gps)
                                .withDescription("Расположение оборудования")
                                .withIcon(GoogleMaterial.Icon.gmd_my_location)
                                .withIdentifier(FRAGMENT_GPS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        taskPrimaryDrawerItem,
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_references)
                                .withDescription("Дополнительно")
                                .withIcon(GoogleMaterial.Icon.gmd_book)
                                .withIdentifier(FRAGMENT_REFERENCES)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName("Документация")
                                .withDescription("на оборудование")
                                .withIcon(GoogleMaterial.Icon.gmd_collection_bookmark)
                                .withIdentifier(FRAGMENT_DOCS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName("Объекты")
                                .withDescription("Здания и сооружения")
                                .withIcon(GoogleMaterial.Icon.gmd_home)
                                .withIdentifier(FRAGMENT_OBJECTS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName(R.string.contragents)
                                .withDescription("Справочник контрагентов")
                                .withIcon(GoogleMaterial.Icon.gmd_accounts_alt)
                                .withIdentifier(FRAGMENT_CONTRAGENTS)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
//                        new DividerDrawerItem(),
//                        new PrimaryDrawerItem()
//                                .withName("Новые задачи")
//                                .withDescription("Скачать новые задачи")
//                                .withIcon(FontAwesome.Icon.faw_plus)
//                                .withIdentifier(DRAWER_TASKS)
//                                .withSelectable(false)
//                                .withSelectable(false)
//                                .withIconColor(R.color.larisaBlueColor),
//                        new PrimaryDrawerItem()
//                                .withName("Обновить с сервера")
//                                .withDescription("Обновить справочники")
//                                .withIcon(FontAwesome.Icon.faw_check)
//                                .withIdentifier(DRAWER_DOWNLOAD)
//                                .withSelectable(false)
//                                .withSelectable(false)
//                                .withIconColor(R.color.larisaBlueColor),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.service)
                                .withDescription("Журнал и gps трек")
                                .withIcon(GoogleMaterial.Icon.gmd_gps)
                                .withIdentifier(FRAGMENT_SERVICE)
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withName("О программе")
                                .withDescription("Информация о версии")
                                .withIcon(FontAwesome.Icon.faw_info)
                                .withIdentifier(DRAWER_INFO)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor)),
                        new PrimaryDrawerItem()
                                .withName("Выход")
                                .withIcon(FontAwesome.Icon.faw_undo)
                                .withIdentifier(DRAWER_EXIT)
                                .withSelectable(false)
                                .withSelectable(false)
                                .withIconColor(ContextCompat.getColor(this, R.color.larisaBlueColor))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            int ident = drawerItem.getIdentifier();
                            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                            if (ident == FRAGMENT_CHARTS) {
                                currentFragment = FRAGMENT_CHARTS;
                                tr.replace(R.id.frame_container, ServiceFragment.newInstance());
                            } else if (ident == DRAWER_DOWNLOAD) {
                                currentFragment = DRAWER_DOWNLOAD;
                                mProgressDialog = new ProgressDialog(MainActivity.this);
                                mProgressDialog.setMessage("Синхронизируем данные");
                                mProgressDialog.setIndeterminate(true);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.setCancelable(true);
                                final Downloader downloaderTask = new Downloader(MainActivity.this);
                                SharedPreferences sp = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext());
                                String updateUrl = sp.getString(getString(R.string.updateUrl), "");

                                if (!updateUrl.equals("")) {
                                    String path = Environment.getExternalStorageDirectory() + "/Download/";
                                    File file = new File(path);
                                    if (file.mkdirs()) {
                                        File outputFile = new File(path, "Toir-mobile.apk");
                                        downloaderTask.execute(updateUrl, outputFile.toString());
                                        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                downloaderTask.cancel(true);
                                            }
                                        });
                                    }
                                }
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
                    }
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
                    if (activeUserID > 0 && iprofilelist.get(cnt).getIdentifier() == activeUserID + 2)
                        headerResult.setActiveProfile(iprofilelist.get(cnt));
                }
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();

        if (activeUserID <= 0) {
            Toast.makeText(getApplicationContext(),
                    "Пожалуйста зарегистрируйтесь", Toast.LENGTH_LONG).show();
        }

        //((ViewGroup) findViewById(R.id.frame_container)).addView(result.getSlider());
        //pager = (ViewPager) findViewById(R.id.pager);
        //pager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        //PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //tabs.setViewPager(pager);
    }

    /**
     * Обработчик клика кнопки "Войти"
     *
     * @param view Event's view
     */
    public void onClickLogin(View view) {
        if (!isSkipGPS() && !isGpsOn()) {
            Toast.makeText(getApplicationContext(), "GPS must be enabled.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        setEnabledLoginButton(false);
        startAuthorise();
        setEnabledLoginButton(true);
    }

    /**
     * Обработчик клика меню запуска блютус сервера
     *
     * @param menuItem - пункт меню
     */
    public void onActionBTServer(MenuItem menuItem) {
        Log.d(TAG, "onActionBTServer");
        Intent i = new Intent(MainActivity.this, BTServerActivity.class);
        startActivity(i);
    }

    /**
     * Обработчик клика меню обновления приложения
     *
     * @param menuItem Элемент меню
     */
    public void onActionUpdate(MenuItem menuItem) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        // урл по которому забираем файл обновления
        String updateUrl = sp.getString(getString(R.string.updateUrl), "");

        if (updateUrl.equals("")) {
            Toast toast = Toast.makeText(this, "Не указан URL для обновления!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        addToJournal("Запущено обновление с сервера " + updateUrl);
        String path = Environment.getExternalStorageDirectory() + "/Download/";
        File file = new File(path);
        if (file.mkdirs()) {
            File outputFile = new File(path, "Toir-mobile.apk");
            Downloader d = new Downloader(MainActivity.this);
            d.execute(updateUrl, outputFile.toString());
        }
    }

    public void onActionSettings(MenuItem menuItem) {
        Log.d(TAG, "onActionSettings");
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void onActionAbout(MenuItem menuItem) {
        Log.d(TAG, "onActionAbout");
        startAboutDialog();
    }

    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle("О программе");
        about.show();
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
        Log.d(TAG, "onSaveInstanceState: isLogged=" + isLogged);
        outState.putBoolean("isLogged", isLogged);
        outState.putBoolean("splashShown", splashShown);
        outState.putString("tagId", AuthorizedUser.getInstance().getTagId());
        outState.putString("token", AuthorizedUser.getInstance().getToken());
        outState.putString("userUuid", AuthorizedUser.getInstance().getUuid());
        outState.putString("userLogin", AuthorizedUser.getInstance().getLogin());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExitTimerStart) {
                return super.onKeyDown(keyCode, event);
            } else if (currentFragment == FRAGMENT_USERS) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        isExitTimerStart = false;
                    }
                };
                handler.postDelayed(runnable, 5000);
                Toast.makeText(this, "Нажмите \"назад\" ещё раз для выхода.", Toast.LENGTH_LONG)
                        .show();
                isExitTimerStart = true;
            } else if (findViewById(R.id.loginButton) != null) {
                return super.onKeyDown(keyCode, event);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /* функция заполняет массив профилей - пользователей */

    public void fillProfileList() {
        //UsersDBAdapter users = new UsersDBAdapter(
        //        new ToirDatabaseContext(getApplicationContext()));
        //User users = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findAll();
        profilesList = realmDB.where(User.class).findAll();
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
        String path = getExternalFilesDir("/users") + File.separator;
        if (item.getChangedAt() != null) {
            Bitmap myBitmap = getResizedBitmap(path, item.getImage(), 0, 600, item.getChangedAt().getTime());
            new_profile = new ProfileDrawerItem()
                    .withName(item.getName())
                    .withEmail(item.getLogin())
                    // first two elements reserved
                    .withIdentifier((int) item.get_id() + 2)
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

    public void refreshProfileList() {
        //UsersDBAdapter users = new UsersDBAdapter(
        //        new ToirDatabaseContext(getApplicationContext()));
        profilesList = realmDB.where(User.class).findAll();
        cnt = 0;
        for (User item : profilesList) {
            users_id[cnt] = item.get_id();
            cnt = cnt + 1;
            if (cnt > MAX_USER_PROFILE) break;
        }
    }

    public void deleteProfile(int id) {
        int id_remove;
        for (cnt = 0; cnt < iprofilelist.size(); cnt++) {
            if (users_id[cnt] == id) {
                iprofilelist.remove(cnt);
                //headerResult.removeProfile(cnt);
                id_remove = (int) (users_id[cnt] + 2);
                headerResult.removeProfileByIdentifier(id_remove);
            }
        }
        refreshProfileList();
    }

    public void mOnClickMethod(View view) {
        //openOptionsMenu();
        //Intent i = new Intent(MainActivity.this, ToirPreferences.class);
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }


    public void changeActiveProfile(User user) {
        if (iprofilelist != null) {
            if (iprofilelist.size() > 0) {
                for (cnt = 0; cnt < iprofilelist.size(); cnt++) {
                    if (users_id[cnt] == user.get_id()) {
                        headerResult.setActiveProfile(iprofilelist.get(cnt));

                        realmDB.beginTransaction();
                        RealmResults<User> users = realmDB.where(User.class).findAll();
                        for (int i = 0; i < users.size(); i++) {
                            users.get(i).setActive(false);
                        }

                        if (profilesList != null && profilesList.get(cnt) != null) {
                            profilesList.get(cnt).setActive(true);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Непредвиденная ошибка: нет такого пользователя", Toast.LENGTH_LONG).show();
                        }

                        realmDB.commitTransaction();
                        user.setActive(true);
                    }
                }
            }
        }
    }

    public void ShowSettings() {
        TextView driver, update_server, system_server;
        driver = (TextView) findViewById(R.id.login_current_driver);
        update_server = (TextView) findViewById(R.id.login_current_update_server);
        system_server = (TextView) findViewById(R.id.login_current_system_server);

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String updateUrl = sp.getString(getString(R.string.updateUrl), "");
        String serverUrl = sp.getString(getString(R.string.serverUrl), "");
        String classPath = sp.getString(getString(R.string.rfidDriverListPrefKey), "");
        String driverName = RfidDriverBase.getDriverName(classPath);
        if (driverName == null) {
            driverName = "драйвер не выбран";
        }
        //sp.getString(getString(R.string.updateUrl), "");
        // указываем названия и значения для элементов списка
        if (driver != null) {
            driver.setText(driverName);
        }

        if (update_server != null) {
            update_server.setText(updateUrl);
        }

        if (system_server != null) {
            system_server.setText(serverUrl);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        if (checkGPSThread != null) {
            checkGPSThread.interrupt();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        AuthorizedUser user = AuthorizedUser.getInstance();
        if (user.getTagId() == null) {
            // пользователь не вошел в программу, либо потеряна по каким-то причинам информация
            // о текущем пользователе, показываем экран входа
            setContentView(R.layout.login_layout);
        }

        ShowSettings();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                boolean isRun = true;
                while (isRun) {
                    try {
                        Thread.sleep(5000);
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (!isSkipGPS()) {
                            if (!gpsEnabled) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "GPS must be enabled.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        isRun = false;
                    }
                }
            }
        };
        checkGPSThread = new Thread(run);
        checkGPSThread.start();

        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (_locationManager != null) {
            _gpsListener = new GPSListener();
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, _gpsListener);
        }

        startLogSend();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stopLogSend();
        if (realmDB != null) {
            realmDB.close();
        }

        if (_locationManager != null) {
            _locationManager.removeUpdates(_gpsListener);
            _locationManager = null;
            _gpsListener = null;
        }

        // обнуляем пользователя
        AuthorizedUser user = AuthorizedUser.getInstance();
        user.setLogin(null);
        user.setTagId(null);
        user.setToken(null);
        user.setUuid(null);
    }

    /**
     * Стартуем периодический отложенный запуск сервиса отправки логов и координат GPS.
     */
    private void startLogSend() {
        Log.d(TAG, "startLogSend()");
        logNGpsRunnable = new Runnable() {
            @Override
            public void run() {
                // проверяем наличие данных о пользователе для отправки данных
                AuthorizedUser user = AuthorizedUser.getInstance();
                if (user.getToken() == null || user.getLogin() == null) {
                    setTimer();
                    return;
                }

                // получаем данные для отправки
                Realm realm = Realm.getDefaultInstance();

                RealmResults<GpsTrack> gpsItems = realm.where(GpsTrack.class)
                        .equalTo("sent", false)
                        .equalTo("userUuid", user.getUuid())
                        .findAll();
                long[] gpsIds = new long[gpsItems.size()];
                for (int i = 0; i < gpsItems.size(); i++) {
                    gpsIds[i] = gpsItems.get(i).get_id();
                }

                RealmResults<Journal> logItems = realm.where(Journal.class)
                        .equalTo("sent", false)
                        .equalTo("userUuid", user.getUuid())
                        .findAll();
                long[] logIds = new long[logItems.size()];
                for (int i = 0; i < logItems.size(); i++) {
                    logIds[i] = logItems.get(i).get_id();
                }

                // стартуем сервис отправки данных на сервер
                Intent serviceIntent = new Intent(getApplicationContext(), SendGPSnLogService.class);
                serviceIntent.setAction(SendGPSnLogService.ACTION);
                Bundle bundle = new Bundle();
                bundle.putLongArray(SendGPSnLogService.GPS_IDS, gpsIds);
                bundle.putLongArray(SendGPSnLogService.LOG_IDS, logIds);
                serviceIntent.putExtras(bundle);
                getApplicationContext().startService(serviceIntent);
                realm.close();

                setTimer();
            }

            private void setTimer() {
                // "взводим" отложенный запуск отправки
                logNGpsHandler.postDelayed(this, LOG_AND_GPS_SEND_INTERVAL);
            }
        };
        logNGpsHandler = new Handler();
        logNGpsHandler.postDelayed(logNGpsRunnable, LOG_AND_GPS_SEND_INTERVAL);
    }

    /**
     * Останавливаем периодический процесс отправки логов и координат GPS.
     */
    private void stopLogSend() {
        Log.d(TAG, "stopLogSend()");
        if (logNGpsHandler != null) {
            logNGpsHandler.removeCallbacks(logNGpsRunnable);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String hexId = "";
            for (byte b : id) {
                hexId += String.format("%2X", b);
            }

            Intent result = new Intent(RfidDriverNfc.ACTION_NFC);
            result.putExtra("tagId", hexId);
            sendBroadcast(result);
        }
    }

    /**
     * Проверка включен ли GPS.
     *
     * @return boolean
     */
    private boolean isGpsOn() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Проверка на необходимость GPS
     */
    private boolean isSkipGPS() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        return sp.getBoolean(getString(R.string.debug_nocheck_gps), false);
    }
}
