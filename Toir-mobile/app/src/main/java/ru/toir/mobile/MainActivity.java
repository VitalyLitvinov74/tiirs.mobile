package ru.toir.mobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
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
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.fragments.ChartsFragment;
import ru.toir.mobile.fragments.DocumentationFragment;
import ru.toir.mobile.fragments.EquipmentsFragment;
import ru.toir.mobile.fragments.FragmentAddUser;
import ru.toir.mobile.fragments.FragmentEditUser;
import ru.toir.mobile.fragments.GPSFragment;
import ru.toir.mobile.fragments.NativeCameraFragment;
import ru.toir.mobile.fragments.OrderFragment;
import ru.toir.mobile.fragments.ReferenceFragment;
import ru.toir.mobile.fragments.UserInfoFragment;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.serverapi.TokenSrv;

public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_ADD = 1;
    private static final int PROFILE_SETTINGS = 2;
    private static final int MAX_USER_PROFILE = 10;

    private static final int NO_FRAGMENT = 0;
    private static final int FRAGMENT_CAMERA = 1;
    private static final int FRAGMENT_CHARTS = 2;
    private static final int FRAGMENT_EQUIPMENT = 3;
    private static final int FRAGMENT_GPS = 4;
    private static final int FRAGMENT_TASKS = 5;
    private static final int FRAGMENT_REFERENCES = 6;
    private static final int FRAGMENT_USERS = 7;
    private static final int FRAGMENT_USER = 8;
    private static final int FRAGMENT_DOCS = 9;
    //private static final int FRAGMENT_OTHER = 10;

    private static final int DRAWER_TASKS = 11;
    private static final int DRAWER_DOWNLOAD = 12;
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;
    //private static final int DRAWER_ONLINE = 15;

    private static final String TAG = "MainActivity";
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
            return false;
        }
    };

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
        //DatabaseHelper helper;
        // создаём базу данных, в качестве контекста передаём свой, с
        // переопределёнными путями к базе
        try {
            //ToirRealm.init(this);
            // получаем базу realm
            realmDB = Realm.getDefaultInstance();
            //LoadTestData.LoadAllTestData();
            Log.d(TAG, "Realm DB schema version = " + realmDB.getVersion());
            //helper = DatabaseHelper.getInstance(new ToirDatabaseContext(
            //		getApplicationContext()));
            Log.d(TAG, "db.version=" + realmDB.getVersion());
            if (Realm.getDefaultInstance().getVersion() == 0) {
                Toast toast = Toast.makeText(this, "База данных не актуальна!",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                success = true;
            } else {
                Toast toast = Toast.makeText(this, "База данных актуальна!",
                        Toast.LENGTH_SHORT);
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
        Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {

                Log.d(TAG, "Получили сообщение из драйвера.");

                if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                    String tagId = (String) msg.obj;
                    tagId = tagId.substring(4);
                    Log.d(TAG, tagId);

                    AuthorizedUser.getInstance().setTagId(tagId);

                    // показываем диалог входа
                    authorizationDialog = new ProgressDialog(MainActivity.this);
                    authorizationDialog.setMessage("Вход в систему");
                    authorizationDialog.setIndeterminate(true);
                    authorizationDialog
                            .setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    authorizationDialog.setCancelable(false);
                    authorizationDialog.show();

                    // запрашиваем токен
                    Call<TokenSrv> call = ToirAPIFactory.getTokenService().user(tagId);
                    call.enqueue(new Callback<TokenSrv>() {
                        @Override
                        public void onResponse(Response<TokenSrv> response, Retrofit retrofit) {
                            TokenSrv token = response.body();

                            AuthorizedUser.getInstance().setToken(token.getAccessToken());

                            Toast.makeText(getApplicationContext(),
                                    "Токен получен.", Toast.LENGTH_SHORT).show();

                            // запрашиваем актуальную информацию по пользователю
                            Call<User> call = ToirAPIFactory.getUserService()
                                    .user("bearer " + token.getAccessToken());
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Response<User> response, Retrofit retrofit) {
                                    User user = response.body();
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(user);
                                    realm.commitTransaction();
                                    isLogged = true;
                                    AuthorizedUser.getInstance().setUuid(user.getUuid());
                                    setMainLayout(savedInstance);
                                    authorizationDialog.dismiss();
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    // сообщаем описание неудачи
                                    // TODO нужен какой-то механизм уведомления о причине не успеха
//                                    String message = bundle.getString(IServiceProvider.MESSAGE);
                                    String message = "Просто не получили пользователя.";
                                    Toast.makeText(getApplicationContext(), message,
                                            Toast.LENGTH_LONG).show();
                                    authorizationDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            // TODO нужен какой-то механизм уведомления о причине не успеха
//                            String message = bundle.getString(IServiceProvider.MESSAGE);
                            String message = "Просто не получили токен.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                                    .show();
                            // TODO реализовать проверку на то что пользователя нет на сервере
                            // токен не получен, сервер не ответил...

                            // проверяем наличие пользователя в локальной базе
                            User user = realmDB.where(User.class)
                                    .equalTo("tagId", AuthorizedUser.getInstance().getTagId())
                                    .findFirst();

                            // в зависимости от результата либо дать работать, либо не дать
                            if (user != null && user.isActive()) {
                                isLogged = true;
                                AuthorizedUser.getInstance().setUuid(user.getUuid());
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

                return true;
            }
        });

        rfidDialog = new RfidDialog();
        rfidDialog.setHandler(handler);
        rfidDialog.readTagId();
        rfidDialog.show(getFragmentManager(), RfidDialog.TAG);

    }

    /**
     * Устанавливам основной экран приложения
     */
    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        assert bottomBar != null;
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.menu_user:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                        break;
                    case R.id.menu_orders:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, OrderFragment.newInstance()).commit();
                        break;
                    case R.id.menu_equipments:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, EquipmentsFragment.newInstance()).commit();
                        break;
                    case R.id.menu_maps:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, GPSFragment.newInstance()).commit();
                        break;
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);
        //toolbar.setBackgroundResource(R.drawable.header);
        toolbar.setBackgroundColor(getResources().getColor(R.color.larisaBlueColor));
        toolbar.setSubtitle("Обслуживание и ремонт");
        toolbar.setTitleTextColor(Color.WHITE);

        //toolbar.

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
                .addProfiles(
                        new ProfileSettingDrawerItem().withName("Добавить пользователя").withDescription("Добавить пользователя").withIcon(String.valueOf(GoogleMaterial.Icon.gmd_plus)).withIdentifier(PROFILE_ADD),
                        new ProfileSettingDrawerItem().withName("Редактировать пользователей").withIcon(String.valueOf(GoogleMaterial.Icon.gmd_settings)).withIdentifier(PROFILE_SETTINGS)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_ADD) {
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentAddUser.newInstance()).commit();
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTINGS) {
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentEditUser.newInstance("EditProfile")).commit();
                        }
                        if (profile instanceof IDrawerItem && profile.getIdentifier() > PROFILE_SETTINGS) {
                            int profileId = profile.getIdentifier() - 2;
                            int profile_pos;
                            for (profile_pos = 0; profile_pos < iprofilelist.size(); profile_pos++)
                                if (users_id[profile_pos] == profileId) break;

                            headerResult.setActiveProfile(iprofilelist.get(profile_pos));
                            realmDB.beginTransaction();
                            RealmResults<User> users = realmDB.where(User.class).findAll();
                            for (int i = 0; i < users.size(); i++)
                                users.get(i).setActive(false);
                            User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
                            if (user != null) user.setActive(true);
                            if (profilesList != null && profilesList.get(profile_pos)!=null)
                                profilesList.get(profile_pos).setActive(true);
                            else
                                Toast.makeText(getApplicationContext(),
                                        "Непредвиденная ошибка: нет такого пользователя", Toast.LENGTH_LONG).show();
                            realmDB.commitTransaction();
                            //profileDBAdapter.setActiveUser(profilesList.get(profile_pos).get_id());
                            currentFragment = FRAGMENT_USER;
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        fillProfileList();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_users).withDescription("Информация о пользователе").withIcon(GoogleMaterial.Icon.gmd_account_box).withIdentifier(FRAGMENT_USERS).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        //new PrimaryDrawerItem().withName(R.string.menu_camera).withDescription("Проверка камеры").withIcon(GoogleMaterial.Icon.gmd_camera).withIdentifier(FRAGMENT_CAMERA).withSelectable(false),
                        //new PrimaryDrawerItem().withName(R.string.menu_charts).withDescription("Графический пакет").withIcon(GoogleMaterial.Icon.gmd_chart).withIdentifier(FRAGMENT_CHARTS).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.menu_equipment).withDescription("Справочник оборудования").withIcon(GoogleMaterial.Icon.gmd_devices).withIdentifier(FRAGMENT_EQUIPMENT).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName(R.string.menu_gps).withDescription("Расположение оборудования").withIcon(GoogleMaterial.Icon.gmd_my_location).withIdentifier(FRAGMENT_GPS).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName(R.string.menu_tasks).withDescription("Текущие задания").withIcon(GoogleMaterial.Icon.gmd_calendar).withIdentifier(FRAGMENT_TASKS).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName(R.string.menu_references).withDescription("Дополнительно").withIcon(GoogleMaterial.Icon.gmd_book).withIdentifier(FRAGMENT_REFERENCES).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName("Документация").withDescription("на оборудование").withIcon(GoogleMaterial.Icon.gmd_collection_bookmark).withIdentifier(FRAGMENT_DOCS).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        //new DividerDrawerItem(),
                        //new PrimaryDrawerItem().withName("Новые задачи").withDescription("Скачать новые задачи").withIcon(FontAwesome.Icon.faw_plus).withIdentifier(DRAWER_TASKS).withSelectable(false).withSelectable(false).withIconColor(R.color.larisaBlueColor),
                        //new PrimaryDrawerItem().withName("Обновить с сервера").withDescription("Обновить справочники").withIcon(FontAwesome.Icon.faw_check).withIdentifier(DRAWER_DOWNLOAD).withSelectable(false).withSelectable(false).withIconColor(R.color.larisaBlueColor),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("О программе").withDescription("Информация о версии").withIcon(FontAwesome.Icon.faw_info).withIdentifier(DRAWER_INFO).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor)),
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false).withSelectable(false).withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == FRAGMENT_CAMERA) {
                                currentFragment = FRAGMENT_CAMERA;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, NativeCameraFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_CHARTS) {
                                currentFragment = FRAGMENT_CHARTS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, ChartsFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == DRAWER_DOWNLOAD) {
                                currentFragment = DRAWER_DOWNLOAD;
                                mProgressDialog = new ProgressDialog(MainActivity.this);
                                mProgressDialog.setMessage("Синхронизируем данные");
                                mProgressDialog.setIndeterminate(true);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                mProgressDialog.setCancelable(true);
                                final Downloader downloaderTask = new Downloader(MainActivity.this);
                                if (downloaderTask != null) {
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
                                }
                            } else if (drawerItem.getIdentifier() == FRAGMENT_EQUIPMENT) {
                                currentFragment = FRAGMENT_EQUIPMENT;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, EquipmentsFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_GPS) {
                                currentFragment = FRAGMENT_GPS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, GPSFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_TASKS) {
                                currentFragment = FRAGMENT_TASKS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, OrderFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_REFERENCES) {
                                currentFragment = FRAGMENT_REFERENCES;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, ReferenceFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_DOCS) {
                                currentFragment = FRAGMENT_DOCS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, DocumentationFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == FRAGMENT_USERS) {
                                currentFragment = FRAGMENT_USERS;
                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
                            } else if (drawerItem.getIdentifier() == DRAWER_INFO) {
                                startAboutDialog();
                                /*
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("О программе")
                                        .setMessage("ToiR Mobile v2.0.3\n ООО Технологии Энергосбережения (technosber.ru) (c) 2016")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();*/
                            } else if (drawerItem.getIdentifier() == DRAWER_EXIT) {
                                System.exit(0);
                            }
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

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();

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
        startAuthorise();
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
        Intent i = new Intent(MainActivity.this, ToirPreferences.class);
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

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			 return true;
//		}
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
        String target_filename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName() + File.separator + "img" + File.separator + item.getImage();
        File imgFile = new File(target_filename);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            // first two elements reserved
            new_profile = new ProfileDrawerItem().withName(item.getName()).withEmail(item.getLogin()).withIcon(myBitmap).withIdentifier((int) item.get_id() + 2).withOnDrawerItemClickListener(onDrawerItemClickListener);
        } else
            new_profile = new ProfileDrawerItem().withName(item.getName()).withEmail(item.getLogin()).withIcon(R.drawable.profile_default_small).withIdentifier((int) item.get_id() + 2).withOnDrawerItemClickListener(onDrawerItemClickListener);
        iprofilelist.add(new_profile);
        headerResult.addProfile(new_profile, headerResult.getProfiles().size());
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
        Intent i = new Intent(MainActivity.this, ToirPreferences.class);
        startActivity(i);
    }


    public void ShowSettings() {
        TextView driver,update_server,system_server;
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
        if (driver != null)
            driver.setText(driverName);
        if (update_server != null)
            update_server.setText(updateUrl);
        if (system_server != null)
            system_server.setText(serverUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowSettings();
    }
}
