package ru.toir.mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.OperationResult;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.ReferenceServiceHelper;
import ru.toir.mobile.rest.ReferenceServiceProvider;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.DataUtils;

public class EquipmentInfoActivity extends AppCompatActivity {

	private final static String TAG = "EquipmentInfoActivity";

	private String equipment_uuid;
	private ListView lv;

    private AccountHeader headerResult = null;
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;
    private Drawer result = null;

    private TextView tv_equipment_name;
	private TextView tv_equipment_type;
	private TextView tv_equipment_position;
	private TextView tv_equipment_tasks;
	private TextView tv_equipment_critical;
	private ImageView tv_equipment_image;
	private TextView tv_equipment_task_date;
	private Button read_rfid_button;
	private Button write_rfid_button;
	private Button write_button;

	// диалог для работы с rfid считывателем
	private RfidDialog rfidDialog;
	// адаптер для listview с документацией
	private ArrayAdapter<EquipmentDocumentation> documentationArrayAdapter;
	// диалог при загрузке файла документации
	private ProgressDialog loadDocumentationDialog;

	// фильтр для получения сообщений при получении файлов документации с
	// сервера
	private IntentFilter mFilterGetDocumentationFile = new IntentFilter(
			ReferenceServiceProvider.Actions.ACTION_GET_DOCUMENTATION_FILE);
	private BroadcastReceiver mReceiverGetDocumentationFile = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int provider = intent.getIntExtra(
					ProcessorService.Extras.PROVIDER_EXTRA, 0);
			Log.d(TAG, "" + provider);
			if (provider == ProcessorService.Providers.REFERENCE_PROVIDER) {
				int method = intent.getIntExtra(
						ProcessorService.Extras.METHOD_EXTRA, 0);
				Log.d(TAG, "" + method);
				if (method == ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE) {
					boolean result = intent.getBooleanExtra(
							ProcessorService.Extras.RESULT_EXTRA, false);
					Bundle bundle = intent
							.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
					Log.d(TAG, "boolean result" + result);

					if (result == true) {
						Toast.makeText(getApplicationContext(),
								"Файл загружен успешно и готов к просмотру.",
								Toast.LENGTH_LONG).show();
						EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
								new ToirDatabaseContext(getApplicationContext()));
						documentationArrayAdapter.clear();
						documentationArrayAdapter.addAll(documentationDBAdapter
								.getItems(equipment_uuid));

						// показываем только первый файл, по идее он один и
						// должен быть
						String[] uuids = bundle
								.getStringArray(ReferenceServiceProvider.Methods.RESULT_GET_DOCUMENTATION_FILE_UUID);
						if (uuids != null) {
							showDocument(uuids[0]);
						}
					} else {
						// сообщаем описание неудачи
						String message = bundle
								.getString(IServiceProvider.MESSAGE);
						Toast.makeText(getApplicationContext(),
								"Ошибка при файла. " + message,
								Toast.LENGTH_LONG).show();
					}

					// закрываем диалог
					loadDocumentationDialog.dismiss();
					unregisterReceiver(mReceiverGetDocumentationFile);
				}
			}

		}
	};

	/**
	 * Показываем документ из базы во внешнем приложении.
	 * 
	 * @param uuid
	 */
	private void showDocument(String uuid) {

		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));

		if (uuid != null) {
			EquipmentDocumentation item = documentationDBAdapter.getItem(uuid);
			MimeTypeMap mt = MimeTypeMap.getSingleton();
			File file = new File(item.getPath());
			String[] patternList = file.getName().split("\\.");
			String extension = patternList[patternList.length - 1];

			if (mt.hasExtension(extension)) {
				String mimeType = mt.getMimeTypeFromExtension(extension);
				Intent target = new Intent(Intent.ACTION_VIEW);
				target.setDataAndType(Uri.fromFile(new File(item.getPath())),
						mimeType);
				target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

				Intent viewFileIntent = Intent.createChooser(target,
						"Open File");
				try {
					startActivity(viewFileIntent);
				} catch (ActivityNotFoundException e) {
					// сообщить пользователю установить подходящее
					// приложение
				}
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		equipment_uuid = b.getString("equipment_uuid");
		//setContentView(R.layout.equipment_layout);
        setMainLayout(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// tv_equipment_id = (TextView) findViewById(R.id.equipment_text_name);
		tv_equipment_name = (TextView) findViewById(R.id.equipment_text_name);
		tv_equipment_type = (TextView) findViewById(R.id.equipment_text_type);
		tv_equipment_position = (TextView) findViewById(R.id.equipment_position);
		tv_equipment_critical = (TextView) findViewById(R.id.equipment_critical);
		tv_equipment_task_date = (TextView) findViewById(R.id.equipment_text_date);
		tv_equipment_tasks = (TextView) findViewById(R.id.equipment_text_tasks);
		tv_equipment_image = (ImageView) findViewById(R.id.equipment_image);
		lv = (ListView) findViewById(R.id.equipment_info_operation_list);
		FillListViewOperations();

		read_rfid_button = (Button) findViewById(R.id.button_read);
		write_rfid_button = (Button) findViewById(R.id.button_write);
		// временная кнопка записи в метку пользователей
		write_button = (Button) findViewById(R.id.button_write_user);

		read_rfid_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "Считываем память метки.");
				EquipmentDBAdapter adapter = new EquipmentDBAdapter(
						new ToirDatabaseContext(getApplicationContext()));
				Equipment equipment = adapter.getItem(equipment_uuid);
				Log.d(TAG, "id метки оборудования: " + equipment.getTag_id());

				Handler handler = new Handler(new Handler.Callback() {

					@Override
					public boolean handleMessage(Message msg) {

						Log.d(TAG, "Получили сообщение из драйвера.");

						if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
							String tagData = (String) msg.obj;
							Log.d(TAG, tagData);
							Toast.makeText(getApplicationContext(),
									"Считывание метки успешно.\r\n" + tagData,
									Toast.LENGTH_SHORT).show();
						} else {
							Log.d(TAG, "Ошибка чтения метки!");
							Toast.makeText(getApplicationContext(),
									"Ошибка чтения метки.", Toast.LENGTH_SHORT)
									.show();
						}

						// закрываем диалог
						rfidDialog.dismiss();
						return true;
					}
				});
				rfidDialog = new RfidDialog();
				rfidDialog.setHandler(handler);

				// читаем метку с конкретным id для теста
				// rfidDialog.readTagData("0000000000",
				// "3000E2004000860902332580112D",
				// RfidDriverBase.MEMORY_BANK_USER, 0, 64);

				// читаем метку с id привязанным к оборудованию
				// rfidDialog.readTagData("0000000000", equipment.getTag_id(),
				// RfidDriverBase.MEMORY_BANK_USER, 0, 8);

				// читаем "произовольную" метку, ту которую найдём первой
				rfidDialog.readTagData("0000000000",
						RfidDriverBase.MEMORY_BANK_USER, 0, 64);

				rfidDialog.show(getFragmentManager(), TAG);
			}
		});

		write_rfid_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "Пишем в метку оборудования.");
				EquipmentDBAdapter adapter = new EquipmentDBAdapter(
						new ToirDatabaseContext(getApplicationContext()));
				Equipment equipment = adapter.getItem(equipment_uuid);
				Log.d(TAG, "id метки оборудования: " + equipment.getTag_id());

				Handler handler = new Handler(new Handler.Callback() {

					@Override
					public boolean handleMessage(Message msg) {

						Log.d(TAG, "Получили сообщение из драйвера.");

						if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
							Toast.makeText(getApplicationContext(),
									"Запись метки удалась.", Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(getApplicationContext(),
									"Не удалось записать данные.",
									Toast.LENGTH_SHORT).show();
						}

						// закрываем диалог
						rfidDialog.dismiss();
						return true;
					}
				});
				rfidDialog = new RfidDialog();
				rfidDialog.setHandler(handler);
				// тестовые данные для примера
				String data = "0a0a0a0a";
				data = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
				data = "00000000000000000000000000000000";
				// data = "FFFFFFFFFFFFFFFF";
				data = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

				// пишем в метку с id привязанным к оборудованию
				// rfidDialog.writeTagData("0000000000", equipment.getTag_id(),
				// RfidDriverBase.MEMORY_BANK_USER, 0, data);

				// пишем в "известную" метку
				// rfidDialog.writeTagData("0000000000",
				// "3000E2004000860902332580112D",
				// RfidDriverBase.MEMORY_BANK_USER, 0, data);

				// пишем в "произовольную" метку, ту которую найдём первой
				rfidDialog.writeTagData("0000000000",
						RfidDriverBase.MEMORY_BANK_USER, 0, data);

				rfidDialog.show(getFragmentManager(), TAG);

			}
		});

		write_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "Пишем в метку пользователя.");
				// сюда нужно перенести код который отвечает за сохранение
				// структуры данных в пользовательскую метку
			}
		});

		initView();
	}

	private class ListViewClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			EquipmentDocumentation item = documentationArrayAdapter
					.getItem(position);

			File file = new File(item.getPath());
			if (file.exists()) {
				showDocument(item.getUuid());
			} else {
				// либо сказать что файла нет, либо предложить скачать с сервера
				Log.d(TAG, "Получаем файл документации.");
				ReferenceServiceHelper rsh = new ReferenceServiceHelper(
						getApplicationContext(),
						ReferenceServiceProvider.Actions.ACTION_GET_DOCUMENTATION_FILE);

				registerReceiver(mReceiverGetDocumentationFile,
						mFilterGetDocumentationFile);

				rsh.getDocumentationFile(new String[] { item.getUuid() });

				// показываем диалог получения наряда
				loadDocumentationDialog = new ProgressDialog(
						EquipmentInfoActivity.this);
				loadDocumentationDialog
						.setMessage("Получаем файл документации");
				loadDocumentationDialog.setIndeterminate(true);
				loadDocumentationDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				loadDocumentationDialog.setCancelable(false);
				loadDocumentationDialog.setButton(
						DialogInterface.BUTTON_NEGATIVE, "Отмена",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								unregisterReceiver(mReceiverGetDocumentationFile);
								Toast.makeText(getApplicationContext(),
										"Получение файла отменено",
										Toast.LENGTH_SHORT).show();
							}
						});
				loadDocumentationDialog.show();
			}
		}

	}

	private void initView() {

		// TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new
		// TOiRDatabaseContext(getApplicationContext()));
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentOperationResultDBAdapter eqOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));

		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		Equipment equipment = equipmentDBAdapter.getItem(equipment_uuid);
		ArrayList<EquipmentOperation> equipmentOperationList = eqOperationDBAdapter
				.getItemsByTaskAndEquipment("", equipment.getUuid());
		// tv_equipment_id.setText("TAGID: " + equipment.getTag_id());
		tv_equipment_name.setText("Название: " + equipment.getTitle());
		tv_equipment_type.setText("Тип: "
				+ eqTypeDBAdapter.getNameByUUID(equipment
						.getEquipment_type_uuid()));

		tv_equipment_position.setText(""
				+ String.valueOf(equipment.getLatitude()) + " / "
				+ String.valueOf(equipment.getLongitude()));
		tv_equipment_task_date.setText(DataUtils.getDate(
				equipment.getStart_date(), "dd.MM.yyyy HH:mm"));
		tv_equipment_critical.setText("Критичность: "
				+ criticalTypeDBAdapter.getNameByUUID(equipment
						.getCritical_type_uuid()));

		// if (equipmentOperationList != null &&
		// equipmentOperationList.size()>0)
		// tv_equipment_task_date.setText("" +
		// taskDBAdapter.getCompleteTimeByUUID(equipmentOperationList.get(0).getTask_uuid()));
		// else tv_equipment_task_date.setText("еще не обслуживалось");
		if (equipmentOperationList != null && equipmentOperationList.size() > 0) {
			tv_equipment_tasks.setText(""
					+ eqOperationResultDBAdapter
							.getOperationResultByUUID(equipmentOperationList
									.get(0).getOperation_status_uuid()));
		} else {
			tv_equipment_tasks.setText("еще не обслуживалось");
		}

		File imgFile = new File(equipment.getImage());
		if (imgFile.exists() && imgFile.isFile()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());
			tv_equipment_image.setImageBitmap(myBitmap);
		}

		// адаптер для listview с документацией
		ListView documentationListView = (ListView) findViewById(R.id.e_l_documentation_listView);
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		documentationArrayAdapter = new ArrayAdapter<EquipmentDocumentation>(
				getApplicationContext(), R.layout.equipment_documentation_item,
				R.id.documentation_item_title,
				documentationDBAdapter.getItems(equipment_uuid));

		documentationListView.setAdapter(documentationArrayAdapter);
		documentationListView
				.setOnItemClickListener(new ListViewClickListener());
	}

	private void FillListViewOperations() {

		EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		OperationResultDBAdapter operationResultDBAdapter = new OperationResultDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));

		ArrayList<EquipmentOperation> operationList = eqOperationDBAdapter
				.getItemsByTaskAndEquipment("", equipment_uuid);
		EquipmentOperationResult equipmentOperationResult;
		OperationResult operationResult;
		int operation_type;

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };

		if (operationList != null)
			for (EquipmentOperation operation : operationList) {

				HashMap<String, String> hm = new HashMap<String, String>();
				equipmentOperationResult = equipmentOperationResultDBAdapter
						.getItemByOperation(operation.getUuid());
				String resultTitle;
				String startDate;

				if (equipmentOperationResult != null) {
					startDate = DataUtils.getDate(
							equipmentOperationResult.getStart_date(),
							"dd.MM.yyyy HH:mm");
				} else {
					startDate = "не проводилась";
				}

				hm.put("name",
						"Операция: "
								+ operationTypeDBAdapter.getItem(
										operation.getOperation_type_uuid())
										.getTitle() + " [" + startDate + "]");

				if (equipmentOperationResult != null) {
					operationResult = operationResultDBAdapter
							.getItem(equipmentOperationResult
									.getOperation_result_uuid());
					if (operationResult != null) {
						resultTitle = operationResult.getTitle();
					} else {
						resultTitle = "---";
					}
				} else {
					resultTitle = "---";
				}

				hm.put("descr",
						"Критичность: "
								+ criticalTypeDBAdapter.getItem(
										eqDBAdapter.getItem(
												operation.getEquipment_uuid())
												.getCritical_type_uuid())
										.getType() + " Результат: ["
								+ resultTitle + "]");
				// Creation row
				operation_type = equipmentOperationResultDBAdapter
						.getOperationResultTypeByUUID(operation
								.getOperation_status_uuid());
				switch (operation_type) {
				case 1:
					hm.put("img", Integer.toString(R.drawable.img_status_4));
					break;
				case 2:
					hm.put("img", Integer.toString(R.drawable.img_status_3));
					break;
				case 3:
					hm.put("img", Integer.toString(R.drawable.img_status_1));
					break;
				default:
					hm.put("img", Integer.toString(R.drawable.img_status_1));
				}
				aList.add(hm);
			}
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				aList, R.layout.listview, from, to);
		// Setting the adapter to the listView
		lv.setAdapter(adapter);
	}

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.equipment_layout);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.drawable.header);
        toolbar.setSubtitle("Обслуживание и ремонт");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .build();

        //iprofilelist = new ArrayList<>();
        //users_id = new long[MAX_USER_PROFILE];
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("О программе").withDescription("Информация о версии").withIcon(FontAwesome.Icon.faw_info).withIdentifier(DRAWER_INFO).withSelectable(false),
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_INFO) {
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("Информация о программе")
                                        .setMessage("TOiR Mobile v1.0.1\n ООО Технологии Энергосбережения (technosber.ru) (c) 2016")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
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
        }
        //getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
    }
}
