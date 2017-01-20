package ru.toir.mobile;

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
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.DateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.db.adapters.DocumentationAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.ReferenceServiceHelper;
import ru.toir.mobile.rest.ReferenceServiceProvider;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.TagStructure;
import ru.toir.mobile.utils.DataUtils;

public class EquipmentInfoActivity extends AppCompatActivity {
    private Realm realmDB;

	private final static String TAG = "EquipmentInfoActivity";

	private String equipment_uuid;
	//private ListView lv;

    private AccountHeader headerResult = null;
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    private TextView tv_equipment_name;
    private TextView tv_equipment_inventory;
    private TextView tv_equipment_uuid;
	private TextView tv_equipment_type;
	private TextView tv_equipment_position;
	private TextView tv_equipment_tasks;
	private TextView tv_equipment_critical;
	private ImageView tv_equipment_image;
	private TextView tv_equipment_task_date;
    private ListView tv_equipment_listview;
    private TextView tv_equipment_status;

    private TaskAdapter taskAdapter;

    // диалог для работы с rfid считывателем
	private RfidDialog rfidDialog;
	// диалог при загрузке файла документации
	private ProgressDialog loadDocumentationDialog;

    static DocumentationAdapter documentationAdapter;

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

					if (result) {
						Toast.makeText(getApplicationContext(),
								"Файл загружен успешно и готов к просмотру.",
								Toast.LENGTH_LONG).show();
                        //Documentation<Documentation> documentation = realmDB.where(Documentation.class).equalTo("equipmentUuid",);
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
	 * @param uuid - идентификатор документа
	 */
	private void showDocument(String uuid) {

        RealmResults<Documentation> documentation;
        documentation = realmDB.where(Documentation.class).findAll();
        documentationAdapter = new DocumentationAdapter(getApplicationContext(), documentation);

		if (uuid != null) {
			Documentation item = realmDB.where(Documentation.class).equalTo("uuid",uuid).findFirst();
			MimeTypeMap mt = MimeTypeMap.getSingleton();
            File file = new File(item.getUuid());
			//File file = new File(item.getPath());
			String[] patternList = file.getName().split("\\.");
			String extension = patternList[patternList.length - 1];

			if (mt.hasExtension(extension)) {
				String mimeType = mt.getMimeTypeFromExtension(extension);
				Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(new File(item.getUuid())),
                		mimeType);
				//target.setDataAndType(Uri.fromFile(new File(item.getPath())),
				//		mimeType);
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
        realmDB = Realm.getDefaultInstance();
		Bundle b = getIntent().getExtras();
		equipment_uuid = b.getString("equipment_uuid");
		//setContentView(R.layout.equipment_layout);
        setMainLayout(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView read_rfid_button = (ImageView) findViewById(R.id.overlapImageReadTag);
        ImageView write_rfid_button = (ImageView) findViewById(R.id.overlapImageWriteTag);

		tv_equipment_name = (TextView) findViewById(R.id.equipment_text_name);
        tv_equipment_inventory = (TextView) findViewById(R.id.equipment_text_inventory);
        tv_equipment_uuid = (TextView) findViewById(R.id.equipment_text_uuid);
		tv_equipment_position = (TextView) findViewById(R.id.equipment_text_location);
		tv_equipment_task_date = (TextView) findViewById(R.id.equipment_text_date);
		//tv_equipment_tasks = (TextView) findViewById(R.id.equipment_text_status);
		tv_equipment_image = (ImageView) findViewById(R.id.equipment_image);
        tv_equipment_listview = (ListView) findViewById(R.id.list_view);
        tv_equipment_status = (TextView) findViewById(R.id.equipment_text_status);
        //tv_equipment_type = (TextView) findViewById(R.id.equipment_text_type);
		//lv = (ListView) findViewById(R.id.equipment_info_operation_list);
        //tv_equipment_critical = (TextView) findViewById(R.id.equipment_critical);

		//FillListViewOperations();
        //Button write_rfid_button = (Button) findViewById(R.id.button_write);
		// временная кнопка записи в метку пользователей
        //Button write_button = (Button) findViewById(R.id.button_write_user);

        if (read_rfid_button != null) {
            read_rfid_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Считываем память метки.");
                    Equipment equipment = realmDB.where(Equipment.class).equalTo("uuid", equipment_uuid).findFirst();
                    if (equipment != null) {
                        Log.d(TAG, "id метки оборудования: " + equipment.getTagId());

                        Handler handler = new Handler(new Handler.Callback() {

                            @Override
                            public boolean handleMessage(Message msg) {
                                Log.d(TAG, "Получили сообщение из драйвера.");

                                if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                                    String tagData = (String) msg.obj;
                                    Log.d(TAG, tagData);
                                    if (tagData.length() / 2 == 64) {
                                        Toast.makeText(getApplicationContext(),
                                                "Считывание метки успешно.\r\n" + tagData,
                                                Toast.LENGTH_SHORT).show();
                                        TagStructure tag = new TagStructure();
                                        tag.parse(DataUtils.hexStringToByteArray(tagData));
                                        Log.d(TAG, "uuid = " + tag.uuid);
                                        Log.d(TAG, "taskId = " + String.format("0x%08x", tag.taskId));
                                        Log.d(TAG, "taskTypeId = " + String.format("0x%08x", tag.taskTypeId));
                                        Log.d(TAG, "start = " + String.format("0x%08x", tag.start));
                                        Log.d(TAG, "end = " + String.format("0x%08x", tag.end));
                                        Log.d(TAG, "phone = " + tag.phone);
                                    }
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
                        // rfidDialog.readTagData("0000000000", "3000E2004000860902332580112D",
                        // RfidDriverBase.MEMORY_BANK_USER, 0, 64);

                        // читаем метку с id привязанным к оборудованию
                         rfidDialog.readTagData("00000000", equipment.getTagId(),
                                 RfidDriverBase.MEMORY_BANK_USER, 0, 64);

                        // читаем "произовольную" метку, ту которую найдём первой
//                        rfidDialog.readTagData("00000000", RfidDriverBase.MEMORY_BANK_USER, 0, 64);

                        rfidDialog.show(getFragmentManager(), TAG);
                    }
                }
            });
        }

        if (write_rfid_button != null) {
            write_rfid_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Пишем в метку оборудования.");
                    Log.d(TAG, "uuid оборудования = " + equipment_uuid);
                    Equipment equipment = realmDB.where(Equipment.class).equalTo("uuid", equipment_uuid).findFirst();
                    if (equipment != null) {
                        Log.d(TAG, "id метки оборудования: " + equipment.getTagId());
                        TagStructure tag = new TagStructure();
                        tag.uuid = equipment.getUuid();
                        tag.taskId = 0x10111213;
                        tag.taskTypeId = 0x14151617;
                        tag.start = 0x18191a1b;
                        tag.end = 0x1c1d1e1f;

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
                        //data = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
                        //data = "00000000000000000000000000000000";
                        // data = "FFFFFFFFFFFFFFFF";
                        data = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
                        byte[] byteData = tag.getBinary();
                        data = DataUtils.toHexString(byteData);
                        // пишем в метку с id привязанным к оборудованию
                        // rfidDialog.writeTagData("00000000", equipment.getTag_id(),
                        // RfidDriverBase.MEMORY_BANK_USER, 0, data);

                        // пишем в "известную" метку
                        // rfidDialog.writeTagData("00000000",
                        // "3000E2004000860902332580112D",
                        // RfidDriverBase.MEMORY_BANK_USER, 0, data);

                        // пишем в "произовольную" метку, ту которую найдём первой
//                        rfidDialog.writeTagData("00000000", RfidDriverBase.MEMORY_BANK_USER, 0, data);

                        // пишем в "известную" метку
                        data =  "000102030405060708090A0B0C0D0E0F";
                        data += "101112131415161718191A1B1C1D1E1F";
                        data += "202122232425262728292A2B2C2D2E2F";
                        data += "303132333435363738393A3B3C3D3E3F";
                        data = "FF050403020100FF";
//                        data = "EFFE";
//                        rfidDialog.writeTagData("00000000", equipment.getTagId(),
//                                RfidDriverBase.MEMORY_BANK_USER, 0, data);
                        rfidDialog.writeTagData("00000000", RfidDriverBase.MEMORY_BANK_USER, 0, data);

                        rfidDialog.show(getFragmentManager(), TAG);

                    }
                }
            });
        }
        /*
		write_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Пишем в метку пользователя.");
                // сюда нужно перенести код который отвечает за сохранение
                // структуры данных в пользовательскую метку
            }
        });
        */
		initView();
	}

	private class ListViewClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

            Documentation documentation = (Documentation)parent.getItemAtPosition(position);
			// TODO как все таки пути к файлу формируем
            File file = new File(documentation.getEquipment().getUuid());
			if (file.exists()) {
				showDocument(documentation.getUuid());
			} else {
				// либо сказать что файла нет, либо предложить скачать с сервера
				Log.d(TAG, "Получаем файл документации.");
				ReferenceServiceHelper rsh = new ReferenceServiceHelper(
						getApplicationContext(),
						ReferenceServiceProvider.Actions.ACTION_GET_DOCUMENTATION_FILE);

				registerReceiver(mReceiverGetDocumentationFile,
						mFilterGetDocumentationFile);

				rsh.getDocumentationFile(new String[] { documentation.getUuid() });

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
        Equipment equipment = realmDB.where(Equipment.class).equalTo("uuid", equipment_uuid).findFirst();

        tv_equipment_name.setText(equipment.getTitle());
        tv_equipment_inventory.setText("ИД: " + equipment.getInventoryNumber());
        tv_equipment_uuid.setText(equipment.getUuid());
		/*
        tv_equipment_type.setText("Модель: "
				+ equipment.getEquipmentModel().getTitle()); */
        tv_equipment_position.setText(""
                + String.valueOf(equipment.getLatitude()) + " / "
                + String.valueOf(equipment.getLongitude()));

        Date date = equipment.getStartDate();
        String startDate;
        if (date != null) {
            startDate = DateFormat.getDateTimeInstance().format(date);
        } else {
            startDate = "none";
        }

        tv_equipment_task_date.setText(startDate);
        /*
		tv_equipment_critical.setText("Критичность: "
				+ equipment.getCriticalType().getTitle());
*/

        if (equipment.getEquipmentStatus()!=null) {
                 tv_equipment_status.setText(equipment.getEquipmentStatus().getTitle());
        } else {
            tv_equipment_status.setText("неизвестен");
        }

        RealmResults<Tasks> tasks = realmDB.where(Tasks.class).equalTo("equipment.uuid", equipment.getUuid()).findAll();
        taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
        tv_equipment_listview.setAdapter(taskAdapter);

        // TODO временно пока изображения не загружены
        if (equipment.get_id() == 1) tv_equipment_image.setImageResource(R.drawable.kotel);
        if (equipment.get_id() == 2) tv_equipment_image.setImageResource(R.drawable.kotel);
        if (equipment.get_id() == 3) tv_equipment_image.setImageResource(R.drawable.pressure);
        if (equipment.get_id() == 4) tv_equipment_image.setImageResource(R.drawable.gas_counter);

        File imgFile = new File(equipment.getImage());
        if (imgFile.exists() && imgFile.isFile()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
                    .getAbsolutePath());
            //tv_equipment_image.setImageBitmap(myBitmap);
        }

        RealmResults<Documentation> documentation;
        ListView documentationListView = (ListView) findViewById(R.id.equipment_documentation_listView);
        //documentation = realmDB.where(Documentation.class).equalTo("equipment.uuid", equipment.getUuid()).findAll();
        documentation = realmDB.where(Documentation.class).findAll();
        documentationAdapter = new DocumentationAdapter(getApplicationContext(), documentation);
        if (documentationListView != null) {
             documentationListView.setAdapter(documentationAdapter);
             documentationListView
                    .setOnItemClickListener(new ListViewClickListener());
            }
        }

    /*
	private void FillListViewOperations() {
        TaskAdapter taskAdapter;
        RealmResults<Tasks> tasks;
        tasks = realmDB.where(Tasks.class).equalTo("equipmentUuid", equipment_uuid).findAllSorted("startDate");
        taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
        lv.setAdapter(taskAdapter);
	}*/

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.equipment_layout);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        //toolbar.setBackgroundResource(R.drawable.header);
        toolbar.setBackgroundColor(getResources().getColor(R.color.larisaBlueColor));
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

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("О программе").withDescription("Информация о версии").withIcon(FontAwesome.Icon.faw_info).withIdentifier(DRAWER_INFO).withSelectable(false),
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_INFO) {
                                startAboutDialog();
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

        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);
        }
    }

    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle("О программе");
        about.show();
    }
}
