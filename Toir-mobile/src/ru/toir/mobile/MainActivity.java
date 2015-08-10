package ru.toir.mobile;

import java.io.File;

import com.astuetz.PagerSlidingTabStrip;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.fragments.OrdersFragment;
import ru.toir.mobile.fragments.PageAdapter;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.rest.TokenServiceHelper;
import ru.toir.mobile.rest.TokenServiceProvider;
import ru.toir.mobile.rest.UsersServiceHelper;
import ru.toir.mobile.rest.UsersServiceProvider;
import ru.toir.mobile.rfid.RFID;

public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";
	public static final int RETURN_CODE_READ_RFID = 1;
	private boolean isLogged = false;
	public ViewPager pager;
	// PageAdapter pageAdapter;

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
					if (result == true) {
						// TODO при получении пользователя, нужно запросить
						// токен
						TokenServiceHelper serviceHelper = new TokenServiceHelper(
								getApplicationContext(),
								TokenServiceProvider.Actions.ACTION_GET_TOKEN);
						// TODO сюда нужно передать либо имя пользователя либо
						// его метку
						serviceHelper.GetTokenByTag("");
					} else {
						// либо пользователя нет, либо произошла еще какая-то
						// ошибка
					}
				}
			}
		}
	};

	// фильтр для получения сообщений при получении нарядов с сервера
	private final IntentFilter mFilterGetTask = new IntentFilter(
			TaskServiceProvider.Actions.ACTION_GET_TASK);
	private BroadcastReceiver mReceiverGetTask = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int provider = intent.getIntExtra(
					ProcessorService.Extras.PROVIDER_EXTRA, 0);
			Log.d(TAG, "" + provider);
			if (provider == ProcessorService.Providers.TASK_PROVIDER) {
				int method = intent.getIntExtra(
						ProcessorService.Extras.METHOD_EXTRA, 0);
				Log.d(TAG, "" + method);
				if (method == TaskServiceProvider.Methods.GET_TASK) {
					boolean result = intent.getBooleanExtra(
							ProcessorService.Extras.RESULT_EXTRA, false);
					Log.d(TAG, "" + result);
					if (result == true) {
						/*
						 * нужно видимо что-то дёрнуть чтоб уведомить о том что
						 * наряд(ы) получены вероятно нужно сделать попытку
						 * отправить на сервер информацию о полученых нарядах
						 * (которые изменили свой статус на "В работе")
						 */

						Toast.makeText(getApplicationContext(),
								"Наряды получены.", Toast.LENGTH_SHORT).show();
					} else {
						// если наряды по какой-то причине не удалось получить,
						// видимо нужно вывести какое-то сообщение
						Toast.makeText(getApplicationContext(),
								"Ошибка при получении нарядов.",
								Toast.LENGTH_LONG).show();
					}

					// закрываем диалог получения наряда
					PageAdapter adapter = (PageAdapter) pager.getAdapter();
					OrdersFragment fragment = (OrdersFragment) adapter
							.getItem(PageAdapter.ORDER_FRAGMENT);
					fragment.cancelGetOrders();
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
			Log.d(TAG, "onCreate:after read: isLogged=" + isLogged);
		}

		Log.d(TAG, "onCreate");
		if (isLogged) {
			setMainLayout();
		} else {
			setContentView(R.layout.login_layout);
			// TODO необходимо реализовать механизм который бы позволял
			// подключаться к базе только после того как она полностью обновится
			/*
			 * TokenDBAdapter tokenDBAdapter = new TokenDBAdapter(new
			 * TOiRDatabaseContext(getApplicationContext())).open();
			 * tokenDBAdapter.deleteAll(); tokenDBAdapter.close();
			 */
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
			helper = DatabaseHelper.getInstance(new TOiRDatabaseContext(
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
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				success = true;
			}
		} catch (Exception e) {
			Toast toast = Toast.makeText(this,
					"Не удалось открыть/обновить базу данных!",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		return success;
	}

	/**
	 * 
	 */
	public void startAuthorise() {
		isLogged = false;
		Intent rfidRead = new Intent(this, RFIDActivity.class);
		rfidRead.putExtra("action", 1);
		startActivityForResult(rfidRead, RETURN_CODE_READ_RFID);
	}

	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		String msg = null;
		Uri tagData = null;
		String tagId = null;

		switch (requestCode) {
		case RETURN_CODE_READ_RFID:
			if (resultCode == RESULT_OK) {
				tagData = data.getData();
				if (tagData == null) {
					msg = "Данные не получены!";
				} else {
					tagId = tagData.toString();
					Log.d(TAG, "Прочитаны данные из метки: " + tagId);
				}
			} else if (resultCode == RESULT_CANCELED) {
				msg = "Чтение метки отменено пользователем!";
			} else if (resultCode == RFID.RESULT_RFID_READ_ERROR) {
				msg = "Не удалось прочитать содержимое метки!";
			} else if (resultCode == RFID.RESULT_RFID_INIT_ERROR) {
				msg = "Не удалось инициализировать драйвер считывателя!";
			} else if (resultCode == RFID.RESULT_RFID_CLASS_NOT_FOUND) {
				msg = "Не найден драйвер считывателя!";
			}
			break;

		default:
			msg = "Не известный код возврата.";
			break;
		}

		// что-то пошло не так при считывании метки
		if (msg != null) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
					.show();
		} else {
			// TODO необходимо реализовать рассылку уведомлений с результатом
			// чтения метки всем кто сейчас заинтересован в результате операции
			int action = data.getIntExtra("action", 0);
			switch (action) {
			// TODO заменить цифры на читаемые константы операций для которых
			// считывается метка
			case 1:
				// TODO запросить наличие/токен на сервере, по результату
				// запроса принимать решение что делать дальше
				TokenServiceHelper tokenServiceHelper = new TokenServiceHelper(
						getApplicationContext(),
						TokenServiceProvider.Actions.ACTION_GET_TOKEN);
				tokenServiceHelper.GetTokenByTag(tagId);

				// проверяем наличие пользователя в локальной базе
				UsersDBAdapter users = new UsersDBAdapter(
						new TOiRDatabaseContext(getApplicationContext()))
						.open();
				Users user = users.getUserByTagId(tagId);
				users.close();
				if (user == null) {
					UsersServiceHelper serviceHelper = new UsersServiceHelper(
							getApplicationContext(), "get_unknown_user");
					serviceHelper.getUser(tagId);
					// TODO реализовать уведомление и обработку получения
					// пользователя с сервера !!!
					/*
					 * Toast.makeText(this, "Нет такого пользователя!",
					 * Toast.LENGTH_SHORT).show();
					 */
				} else {
					Log.d(TAG, user.toString());
					AuthorizedUser aUser = AuthorizedUser.getInstance();
					aUser.setTagId(tagId);
					aUser.setUuid(user.getUuid());

					isLogged = true;
					setMainLayout();
				}
				break;
			case 2:
				Intent operationActivity = new Intent(this,
						OperationActivity.class);
				Bundle bundle = data.getExtras();
				if (bundle.getString("equipment_tag").equals(tagId)) {
					operationActivity.putExtras(bundle);
					startActivity(operationActivity);
				} else {
					Toast.makeText(getApplicationContext(),
							"Не верное оборудование!!!", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				break;
			}

		}
	}

	/**
	 * Устанавливам основной экран приложения
	 */
	void setMainLayout() {
		setContentView(R.layout.main_layout);
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
		Intent i = new Intent(MainActivity.this, TOiRPreferences.class);
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
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	/*
	 * @Override protected void onRestoreInstanceState(Bundle
	 * savedInstanceState) { super.onRestoreInstanceState(savedInstanceState);
	 * isLogged = savedInstanceState.getBoolean("isLogged"); Log.d(TAG,
	 * "onRestoreInstanceState: isLogged=" + isLogged); }
	 */
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mReceiverGetTask, mFilterGetTask);
		registerReceiver(mReceiverGetUser, mFilterGetUser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mReceiverGetTask);
		unregisterReceiver(mReceiverGetUser);
	}
}
