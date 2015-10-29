package ru.toir.mobile;

import java.io.File;
import com.astuetz.PagerSlidingTabStrip;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.fragments.PageAdapter;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.TokenServiceHelper;
import ru.toir.mobile.rest.TokenServiceProvider;
import ru.toir.mobile.rest.UsersServiceHelper;
import ru.toir.mobile.rest.UsersServiceProvider;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;

public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";
	public static final int RETURN_CODE_READ_RFID = 1;
	private boolean isLogged = false;
	public ViewPager pager;
	private RfidDialog rfidDialog;

	public static class RFIDReadAction {
		public static final int READ_USER_TAG_BEFORE_LOGIN = 1;
		public static final int READ_EQUIPMENT_TAG_BEFORE_OPERATION = 2;
	}

	private ProgressDialog authorizationDialog;

	private boolean splashShown = false;

	// фильтр для сообщений при получении пользователя с сервера
	private final IntentFilter mFilterGetUser = new IntentFilter(
			UsersServiceProvider.Actions.ACTION_GET_USER);
	private BroadcastReceiver mReceiverGetUser = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int provider = intent.getIntExtra(
					ProcessorService.Extras.PROVIDER_EXTRA, 0);
			if (provider == ProcessorService.Providers.USERS_PROVIDER) {
				int method = intent.getIntExtra(
						ProcessorService.Extras.METHOD_EXTRA, 0);
				if (method == UsersServiceProvider.Methods.GET_USER) {
					boolean result = intent.getBooleanExtra(
							ProcessorService.Extras.RESULT_EXTRA, false);
					Bundle bundle = intent
							.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
					if (result == true) {
						UsersDBAdapter users = new UsersDBAdapter(
								new ToirDatabaseContext(getApplicationContext()));
						Users user = users.getUserByTagId(AuthorizedUser
								.getInstance().getTagId());
						// в зависимости от результата либо дать работать, либо
						// не дать
						if (user != null && user.isActive()) {
							isLogged = true;
							AuthorizedUser.getInstance()
									.setUuid(user.getUuid());
							setMainLayout();
						} else {
							Toast.makeText(getApplicationContext(),
									"Нет доступа.", Toast.LENGTH_LONG).show();

						}
					} else {
						// сообщаем описание неудачи
						String message = bundle
								.getString(IServiceProvider.MESSAGE);
						Toast.makeText(getApplicationContext(), message,
								Toast.LENGTH_LONG).show();
					}
					authorizationDialog.dismiss();
				}
			}
		}
	};

	// фильтр для получения сообщений при получении токена с сервера
	private final IntentFilter mFilterGetToken = new IntentFilter(
			TokenServiceProvider.Actions.ACTION_GET_TOKEN);
	private BroadcastReceiver mReceiverGetToken = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int provider = intent.getIntExtra(
					ProcessorService.Extras.PROVIDER_EXTRA, 0);
			Log.d(TAG, "" + provider);
			if (provider == ProcessorService.Providers.TOKEN_PROVIDER) {
				int method = intent.getIntExtra(
						ProcessorService.Extras.METHOD_EXTRA, 0);
				Log.d(TAG, "" + method);
				if (method == TokenServiceProvider.Methods.GET_TOKEN_BY_TAG) {
					boolean result = intent.getBooleanExtra(
							ProcessorService.Extras.RESULT_EXTRA, false);
					Bundle bundle = intent
							.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
					Log.d(TAG, "" + result);
					if (result == true) {
						// запрашиваем актуальную информацию по пользователю
						UsersServiceHelper usersServiceHelper = new UsersServiceHelper(
								getApplicationContext(),
								UsersServiceProvider.Actions.ACTION_GET_USER);
						usersServiceHelper.getUser();

						Toast.makeText(getApplicationContext(),
								"Токен получен.", Toast.LENGTH_SHORT).show();
					} else {
						// TODO реализовать проверку на то что пользователя нет
						// на сервере
						// токен не получен, сервер не ответил...
						// проверяем наличие пользователя в локальной базе
						UsersDBAdapter users = new UsersDBAdapter(
								new ToirDatabaseContext(getApplicationContext()));
						Users user = users.getUserByTagId(AuthorizedUser
								.getInstance().getTagId());

						// в зависимости от результата либо дать работать, либо
						// не дать
						if (user != null && user.isActive()) {
							isLogged = true;
							AuthorizedUser.getInstance()
									.setUuid(user.getUuid());
							setMainLayout();
						} else {
							Toast.makeText(getApplicationContext(),
									"Нет доступа.", Toast.LENGTH_LONG).show();
						}

						String message = bundle
								.getString(IServiceProvider.MESSAGE);
						Toast.makeText(getApplicationContext(), message,
								Toast.LENGTH_LONG).show();
						authorizationDialog.dismiss();
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// инициализация приложения
		init();

		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Log.d(TAG, "onCreate:before read: isLogged=" + isLogged);
		if (savedInstanceState != null) {
			isLogged = savedInstanceState.getBoolean("isLogged");
			splashShown = savedInstanceState.getBoolean("splashShown");
			Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
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
						setMainLayout();
					} else {
						setContentView(R.layout.login_layout);
					}
				}
			}, 3000);
		} else {
			if (isLogged) {
				setMainLayout();
			} else {
				setContentView(R.layout.login_layout);
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
		DatabaseHelper helper = null;
		// создаём базу данных, в качестве контекста передаём свой, с
		// переопределёнными путями к базе
		try {
			helper = DatabaseHelper.getInstance(new ToirDatabaseContext(
					getApplicationContext()));
			Log.d(TAG, "db.version=" + helper.getVersion());
			if (!helper.isDBActual()) {
				Toast toast = Toast.makeText(this, "База данных не актуальна!",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
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

				Log.d(TAG, "Получили сообщение из драйвра.");

				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					String tagId = (String) msg.obj;
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
					TokenServiceHelper tokenServiceHelper = new TokenServiceHelper(
							getApplicationContext(),
							TokenServiceProvider.Actions.ACTION_GET_TOKEN);
					tokenServiceHelper.GetTokenByTag(tagId);
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

		rfidDialog = new RfidDialog(getApplicationContext(), handler);
		rfidDialog.readTagId();
		rfidDialog.show(getFragmentManager(), RfidDialog.TAG);

	}

	/**
	 * Устанавливам основной экран приложения
	 */
	void setMainLayout() {
		setContentView(R.layout.main_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new PageAdapter(getSupportFragmentManager()));
		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(pager);
	}

	/**
	 * Обработчик клика кнопки "Войти"
	 * 
	 * @param view
	 */
	public void onClickLogin(View view) {
		startAuthorise();
	}

	/**
	 * Обработчик клика меню обновления приложения
	 * 
	 * @param view
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
		file.mkdirs();
		File outputFile = new File(path, "Toir-mobile.apk");

		Downloader d = new Downloader(MainActivity.this);
		d.execute(updateUrl, outputFile.toString());
	}

	public void onActionSettings(MenuItem menuItem) {
		Log.d(TAG, "onActionSettings");
		Intent i = new Intent(MainActivity.this, ToirPreferences.class);
		startActivity(i);
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mReceiverGetUser, mFilterGetUser);
		registerReceiver(mReceiverGetToken, mFilterGetToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mReceiverGetUser);
		unregisterReceiver(mReceiverGetToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
