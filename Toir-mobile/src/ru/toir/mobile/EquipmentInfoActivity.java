package ru.toir.mobile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.*;
import ru.toir.mobile.db.tables.*;
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
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.ReferenceServiceHelper;
import ru.toir.mobile.rest.ReferenceServiceProvider;
import ru.toir.mobile.rfid.EquipmentTagStructure;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.TagRecordStructure;
import ru.toir.mobile.rfid.UserTagStructure;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EquipmentInfoActivity extends FragmentActivity {

	private final static String TAG = "EquipmentInfoActivity";

	public final static int READ_EQUIPMENT_LABLE = 1;
	public final static int WRITE_EQUIPMENT_LABLE = 2;
	public final static int WRITE_USER_LABLE = 4;

	private String equipment_uuid;
	private Spinner Spinner_operation;
	private byte regim;
	private ListView lv;
	private ArrayAdapter<String> spinner_operation_adapter;
	private ArrayList<String> list = new ArrayList<String>();

	TagRecordStructure tagrecord = new TagRecordStructure();
	TagRecordStructure tagrecord2 = new TagRecordStructure();
	private ArrayList<TagRecordStructure> tagrecords = new ArrayList<TagRecordStructure>();
	EquipmentTagStructure equipmenttag = new EquipmentTagStructure();
	UserTagStructure usertag = new UserTagStructure();

	/*
	 * android:id="@+id/equipment_image"
	 * android:id="@+id/equipment_listView_main"
	 */

	private TextView tv_equipment_name;
	private TextView tv_equipment_type;
	private TextView tv_equipment_position;
	// private TextView tv_equipment_date;
	private TextView tv_equipment_tasks;
	private TextView tv_equipment_critical;
	private ImageView tv_equipment_image;
	private TextView tv_equipment_status;
	// private TextView tv_equipment_text_date;
	// private TextView tv_equipment_text_tasks;
	private TextView tv_equipment_task_date;
	private TextView tv_equipment_documentation;
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
							EquipmentDocumentation item = documentationDBAdapter
									.getItem(uuids[0]);
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
		setContentView(R.layout.equipment_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// tv_equipment_id = (TextView) findViewById(R.id.equipment_text_name);
		tv_equipment_name = (TextView) findViewById(R.id.equipment_text_name);
		tv_equipment_type = (TextView) findViewById(R.id.equipment_text_type);
		tv_equipment_position = (TextView) findViewById(R.id.equipment_position);
		tv_equipment_status = (TextView) findViewById(R.id.equipment_status);
		tv_equipment_critical = (TextView) findViewById(R.id.equipment_critical);
		tv_equipment_task_date = (TextView) findViewById(R.id.equipment_text_date);
		tv_equipment_tasks = (TextView) findViewById(R.id.equipment_text_tasks);
		tv_equipment_image = (ImageView) findViewById(R.id.equipment_image);
		tv_equipment_documentation = (TextView) findViewById(R.id.equipment_text_documentation);
		lv = (ListView) findViewById(R.id.equipment_listView_main);
		FillListViewOperations();

		read_rfid_button = (Button) findViewById(R.id.button_read);
		write_rfid_button = (Button) findViewById(R.id.button_write);
		write_button = (Button) findViewById(R.id.button_write_user);
		// временная кнопка записи в метку пользователей
		write_button.setVisibility(View.GONE);

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
				rfidDialog = new RfidDialog(getApplicationContext(), handler);

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
				rfidDialog = new RfidDialog(getApplicationContext(), handler);
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

		spinner_operation_adapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_spinner_item,
				list);
		Spinner_operation = (Spinner) findViewById(R.id.equipment_spinner_operations);
		Spinner_operation.setAdapter(spinner_operation_adapter);

		// TODO: настоящие операции над оборудованием (возможные)
		spinner_operation_adapter.clear();
		spinner_operation_adapter.add("Ремонт задвижки");
		spinner_operation_adapter.add("Осмотр задвижки");

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
				equipment.getStart_date(), "dd-MM-yyyy hh:mm"));
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

		// заполняем структуру для записи в метку
		equipmenttag.set_equipment_uuid(equipment.getUuid());
		equipmenttag.set_status(equipment.getEquipment_status_uuid().substring(
				9, 13));
		equipmenttag.set_last("0001");

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
		EquipmentOperationResult equipmentOperationResult;
		ArrayList<EquipmentOperation> equipmentOperationList = eqOperationDBAdapter
				.getItemsByTaskAndEquipment("", equipment_uuid);
		int operation_type;
		String temp;
		int cnt = 0;
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		if (equipmentOperationList != null)
			while (cnt < equipmentOperationList.size()) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("name",
						"Операция: "
								+ operationTypeDBAdapter
										.getOperationTypeByUUID(equipmentOperationList
												.get(cnt)
												.getOperation_type_uuid())
								+ " ["
								+ DataUtils.getDate(
										equipmentOperationResultDBAdapter
												.getStartDateByUUID(equipmentOperationList
														.get(cnt)
														.getEquipment_uuid()),
										"dd-MM-yyyy hh:mm") + "]");
				hm.put("descr",
						"Критичность: "
								+ criticalTypeDBAdapter.getNameByUUID(eqDBAdapter
										.getCriticalByUUID(equipmentOperationList
												.get(cnt).getEquipment_uuid()))
								+ " Результат: ["
								+ equipmentOperationResultDBAdapter
										.getOperationResultByUUID(equipmentOperationList
												.get(cnt).getUuid()) + "]");
				// Creation row
				operation_type = equipmentOperationResultDBAdapter
						.getOperationResultTypeByUUID(equipmentOperationList
								.get(cnt).getOperation_status_uuid());
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

				// заполняем структуру для записи в метку
				// TODO должны записываться последние две записи об обслуживании
				tagrecord.operation_date = Long.parseLong(
						equipmentOperationResultDBAdapter.getStartDateByUUID(
								equipmentOperationList.get(cnt)
										.getEquipment_uuid()).toString(), 16);
				tagrecord.operation_length = Short.parseShort("2050", 16);
				tagrecord.operation_type = equipmentOperationList.get(cnt)
						.getOperation_type_uuid().substring(9, 13)
						.toLowerCase(Locale.ENGLISH);
				temp = equipmentOperationList.get(cnt).getUuid();
				if (!temp.equals("") && temp != null) {
					equipmentOperationResult = equipmentOperationResultDBAdapter
							.getItemByOperation(temp);
					if (equipmentOperationResult != null)
						tagrecord.operation_result = equipmentOperationResultDBAdapter
								.getItemByOperation(
										equipmentOperationList.get(cnt)
												.getUuid())
								.getOperation_result_uuid().substring(9, 13)
								.toLowerCase(Locale.ENGLISH);
					else
						tagrecord.operation_result = "8888";
				}
				// tagrecord.user =
				// AuthorizedUser.getInstance().getUuid().substring(9,
				// 13).toLowerCase(Locale.ENGLISH);
				tagrecord.user = "9bf0";
				if (cnt < 2)
					tagrecords.add(cnt, tagrecord);
				cnt++;
			}
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
				aList, R.layout.listview, from, to);
		// Setting the adapter to the listView
		lv.setAdapter(adapter);
	}

	// TODO наследие старой архитектуры драйверов rfid
	public void CallbackOnReadLable(String result) {
		if (result.length() >= 20) {
			if (regim == WRITE_EQUIPMENT_LABLE) {
				// driver.SetOperationType(RfidDriverC5.WRITE_EQUIPMENT_MEMORY);
				byte out_buffer[] = {};
				try {
					out_buffer = DataUtils.PackToSend(equipmenttag, tagrecords);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// driver.write(out_buffer);
			}
			if (regim == WRITE_USER_LABLE) {
				// driver.SetOperationType(RfidDriverC5.WRITE_USER_MEMORY);
				byte out_buffer[] = {};
				UsersDBAdapter users = new UsersDBAdapter(
						new ToirDatabaseContext(getApplicationContext()));
				Users user = users.getUserByTagId(AuthorizedUser.getInstance()
						.getTagId());
				usertag.set_user_uuid(user.getUuid());
				usertag.set_name(user.getName());
				usertag.set_whois(user.getWhois());
				try {
					out_buffer = DataUtils.PackToSendUserData(usertag);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// driver.write(out_buffer);
			}
			if (regim == READ_EQUIPMENT_LABLE) {
				// driver.readTagId(RfidDriverC5.READ_EQUIPMENT_MEMORY);
			}
		} else
			Toast.makeText(this, "Ответ некорректен: " + result,
					Toast.LENGTH_SHORT).show();
	}

	// TODO наследие старой архитектуры драйверов rfid
	public void CallbackOnWrite(String result) {
		if (result == null) {
			setResult(RfidDriverBase.RESULT_RFID_WRITE_ERROR);
		} else {
			Toast.makeText(this, "Запись успешно завершена", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	// TODO наследие старой архитектуры драйверов rfid
	public void Callback(String result) {
		// Intent data = null;
		if (result == null) {
			setResult(RfidDriverBase.RESULT_RFID_READ_ERROR);
		} else {
			if (result.length() < 100) {
				Toast.makeText(this, "Ответ слишком короткий",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// парсим ответ
			equipmenttag.set_equipment_uuid(DataUtils.StringToUUID(result
					.substring(0, 32)));
			equipmenttag.set_status(result.substring(32, 36).toLowerCase(
					Locale.ENGLISH));
			equipmenttag.set_last(result.substring(36, 40));
			tagrecord.operation_date = Long.parseLong(result.substring(40, 48),
					16);
			tagrecord.operation_length = Short.parseShort(
					result.substring(48, 50), 16);
			tagrecord.operation_type = result.substring(50, 54).toLowerCase(
					Locale.ENGLISH);
			tagrecord.operation_result = result.substring(54, 58).toLowerCase(
					Locale.ENGLISH);
			tagrecord.user = result.substring(58, 62).toLowerCase(
					Locale.ENGLISH);
			tagrecords.add(0, tagrecord);
			tagrecord2.operation_date = Long.parseLong(
					result.substring(62, 70), 16);
			tagrecord2.operation_length = Short.parseShort(
					result.substring(70, 72), 16);
			tagrecord2.operation_type = result.substring(72, 76).toLowerCase(
					Locale.ENGLISH);
			tagrecord2.operation_result = result.substring(76, 80).toLowerCase(
					Locale.ENGLISH);
			tagrecord2.user = result.substring(80, 84).toLowerCase(
					Locale.ENGLISH);
			tagrecords.add(1, tagrecord2);

			// вариант 2 с хранением данных в глобальной структуре
			EquipmentTagStructure.getInstance().set_equipment_uuid(
					DataUtils.StringToUUID(result.substring(0, 32)));
			EquipmentTagStructure.getInstance().set_status(
					result.substring(32, 36).toLowerCase(Locale.ENGLISH));
			EquipmentTagStructure.getInstance().set_last(
					result.substring(36, 40));

			EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
					new ToirDatabaseContext(getApplicationContext()));
			EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
					new ToirDatabaseContext(getApplicationContext()));
			OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
					new ToirDatabaseContext(getApplicationContext()));
			EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
					new ToirDatabaseContext(getApplicationContext()));
			EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(
					new ToirDatabaseContext(getApplicationContext()));

			tv_equipment_name.setText("Название: "
					+ eqDBAdapter.getEquipsNameByUUID(equipmenttag
							.get_equipment_uuid()));
			tv_equipment_type.setText("Тип: "
					+ eqTypeDBAdapter.getNameByUUID(eqDBAdapter
							.getEquipsTypeByUUID(equipmenttag
									.get_equipment_uuid())));
			tv_equipment_position.setText(""
					+ eqDBAdapter.getLocationCoordinatesByUUID(equipmenttag
							.get_equipment_uuid()));
			tv_equipment_status.setText(equipmentStatusDBAdapter
					.getNameByPartOfUUID(equipmenttag.get_status()));
			tv_equipment_documentation.setText("TAGID: "
					+ equipmenttag.get_equipment_uuid());

			int cnt = 0, operation_type = 0;
			List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
			String[] from = { "name", "descr", "img" };
			int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
			// пока две записи
			while (cnt < 2) {
				HashMap<String, String> hm = new HashMap<String, String>();

				hm.put("name",
						"Операция: "
								+ operationTypeDBAdapter
										.getOperationTypeByPartOfUUID(tagrecords
												.get(cnt).operation_type)
								+ " ["
								+ DataUtils.getDate(
										tagrecords.get(cnt).operation_date,
										"dd-MM-yyyy hh:mm") + "]");
				hm.put("descr",
						"Результат: ["
								+ equipmentOperationResultDBAdapter
										.getOperationResultByPartOfUUID(tagrecords
												.get(cnt).operation_result)
								+ "]");
				// Creation row
				operation_type = equipmentOperationResultDBAdapter
						.getOperationResultTypeByPartOfUUID(tagrecords.get(cnt).operation_result);
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
				cnt++;
			}
			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),
					aList, R.layout.listview, from, to);
			// Setting the adapter to the listView
			lv.setAdapter(adapter);

			// tv_equipment_id.setText("TAGID: " +
			// equipmenttag.get_equipment_uuid());
			if (tagrecords.get(0).operation_date > 0) {
				tv_equipment_task_date.setText(DataUtils.getDate(
						tagrecords.get(0).operation_date, "dd-MM-yyyy hh:mm"));
				tv_equipment_tasks
						.setText(equipmentOperationResultDBAdapter
								.getOperationResultByPartOfUUID(tagrecords
										.get(0).operation_result));
			} else
				tv_equipment_task_date.setText("еще не обслуживалось");
		}
	}
}
