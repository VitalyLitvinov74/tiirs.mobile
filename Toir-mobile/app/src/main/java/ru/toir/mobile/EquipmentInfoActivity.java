package ru.toir.mobile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import org.w3c.dom.Text;

import io.realm.Sort;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;
import ru.toir.mobile.db.adapters.DefectAdapter;
import ru.toir.mobile.db.adapters.DefectTypeAdapter;
import ru.toir.mobile.db.adapters.DocumentationAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusAdapter;
import ru.toir.mobile.db.adapters.StageAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.fragments.AddDefectDialog;
import ru.toir.mobile.fragments.DocumentationFragment;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.TagStructure;
import ru.toir.mobile.utils.DataUtils;

import static ru.toir.mobile.utils.MainFunctions.getEquipmentImage;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.IntentFilter;
//import ru.toir.mobile.rest.IServiceProvider;
//import ru.toir.mobile.rest.ProcessorService;
//import ru.toir.mobile.rest.ReferenceServiceHelper;
//import ru.toir.mobile.rest.ReferenceServiceProvider;

public class EquipmentInfoActivity extends AppCompatActivity {
    private final static String TAG = "EquipmentInfoActivity";
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    //private static final int DIALOG_SET_DEFECT = 1;
    //private static final int DIALOG_SET_STATUS = 2;

    private Realm realmDB;
    private String equipment_uuid;
    private TextView tv_equipment_name;
    private TextView tv_equipment_inventory;
    private TextView tv_equipment_uuid;
    private TextView tv_equipment_id;
    private TextView tv_equipment_position;
    private ImageView tv_equipment_image;
    private TextView tv_equipment_task_date;
    private TextView tv_equipment_check_date;
    private ListView tv_equipment_listview;
    private ListView tv_equipment_docslistview;
    private TextView tv_equipment_status;

    Spinner defectTypeSpinner;

    // диалог для работы с rfid считывателем
    private RfidDialog rfidDialog;
    // диалог при загрузке файла документации
    private ProgressDialog loadDocumentationDialog;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;
    FloatingActionButton fab5;
    CoordinatorLayout rootLayout;

    private boolean FAB_Status = false;

    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;
    Animation show_fab_3;
    Animation hide_fab_3;
    Animation show_fab_4;
    Animation hide_fab_4;
    Animation show_fab_5;
    Animation hide_fab_5;


    // фильтр для получения сообщений при получении файлов документации с сервера
//	private IntentFilter mFilterGetDocumentationFile = new IntentFilter(
//			ReferenceServiceProvider.Actions.ACTION_GET_DOCUMENTATION_FILE);
//	private BroadcastReceiver mReceiverGetDocumentationFile = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			int provider = intent.getIntExtra(ProcessorService.Extras.PROVIDER_EXTRA, 0);
//			Log.d(TAG, "" + provider);
//			if (provider == ProcessorService.Providers.REFERENCE_PROVIDER) {
//				int method = intent.getIntExtra( ProcessorService.Extras.METHOD_EXTRA, 0);
//				Log.d(TAG, "" + method);
//				if (method == ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE) {
//					boolean result = intent.getBooleanExtra( ProcessorService.Extras.RESULT_EXTRA, false);
//					Bundle bundle = intent .getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
//					Log.d(TAG, "boolean result" + result);
//
//					if (result) {
//						Toast.makeText(getApplicationContext(),
//								"Файл загружен успешно и готов к просмотру.",
//								Toast.LENGTH_LONG).show();
//                        //Documentation<Documentation> documentation = realmDB.where(Documentation.class).equalTo("equipmentUuid",);
//						// показываем только первый файл, по идее он один и должен быть
//						String[] uuids = bundle
//								.getStringArray(ReferenceServiceProvider.Methods.RESULT_GET_DOCUMENTATION_FILE_UUID);
//						if (uuids != null) {
//							showDocument(uuids[0]);
//						}
//					} else {
//						// сообщаем описание неудачи
//						String message = bundle.getString(IServiceProvider.MESSAGE);
//						Toast.makeText(getApplicationContext(),
//								"Ошибка при файла. " + message,
//								Toast.LENGTH_LONG).show();
//					}
//
//					// закрываем диалог
//					loadDocumentationDialog.dismiss();
//					unregisterReceiver(mReceiverGetDocumentationFile);
//				}
//			}
//
//		}
//	};

    /**
     * Показываем документ из базы во внешнем приложении.
     *
     * @param file - файл
     */
    private void showDocument(File file) {
        MimeTypeMap mt = MimeTypeMap.getSingleton();
        String[] patternList = file.getName().split("\\.");
        String extension = patternList[patternList.length - 1];

        if (mt.hasExtension(extension)) {
            String mimeType = mt.getMimeTypeFromExtension(extension);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), mimeType);
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent viewFileIntent = Intent.createChooser(target, "Open File");
            try {
                startActivity(viewFileIntent);
            } catch (ActivityNotFoundException e) {
                // сообщить пользователю установить подходящее приложение
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

        tv_equipment_name = (TextView) findViewById(R.id.equipment_text_name);
        tv_equipment_inventory = (TextView) findViewById(R.id.equipment_text_inventory);
        tv_equipment_uuid = (TextView) findViewById(R.id.equipment_text_uuid);
        tv_equipment_position = (TextView) findViewById(R.id.equipment_text_location);
        tv_equipment_task_date = (TextView) findViewById(R.id.equipment_text_date);
        tv_equipment_check_date = (TextView) findViewById(R.id.equipment_text_date2);
        tv_equipment_image = (ImageView) findViewById(R.id.equipment_image);
        tv_equipment_listview = (ListView) findViewById(R.id.list_view);
        tv_equipment_docslistview = (ListView) findViewById(R.id.equipment_documentation_listView);
        tv_equipment_status = (TextView) findViewById(R.id.equipment_text_status);
        tv_equipment_id = (TextView) findViewById(R.id.equipment_text_id);

        initView();
    }

    private void initView() {
        final Equipment equipment = realmDB.where(Equipment.class).equalTo("uuid", equipment_uuid).findFirst();
        if (equipment == null) {
            Toast.makeText(getApplicationContext(),"Неизвестное оборудование!!",Toast.LENGTH_LONG).show();
            return;
        }
        EquipmentModel equipmentModel = equipment.getEquipmentModel();
        tv_equipment_name.setText(equipment.getTitle());
        if (equipmentModel != null) {
            tv_equipment_inventory.setText(getString(R.string.model, equipment.getEquipmentModel().getTitle()) + " | " + equipment.getEquipmentModel().getEquipmentType().getTitle());
        }
        tv_equipment_id.setText(getString(R.string.id,equipment.getInventoryNumber()));
        tv_equipment_uuid.setText(equipment.getUuid());
//        tv_equipment_type.setText("Модель: " + equipment.getEquipmentModel().getTitle());
        if (equipment.getLatitude()>0) {
            tv_equipment_position.setText(""
                    + String.valueOf(equipment.getLatitude()) + " / "
                    + String.valueOf(equipment.getLongitude()));
        }
        else {
            if (equipment.getLocation() != null) {
                tv_equipment_position.setText(""
                        + String.valueOf(equipment.getLocation().getLatitude()) + " / "
                        + String.valueOf(equipment.getLocation().getLongitude()));
            }
        }
        Date date = equipment.getStartDate();
        String startDate;
        if (date != null) {
            startDate = DateFormat.getDateTimeInstance().format(date);
        } else {
            startDate = "none";
        }

        tv_equipment_task_date.setText(startDate);
        /* tv_equipment_critical.setText("Критичность: "
                + equipment.getCriticalType().getTitle());*/

        if (equipment.getEquipmentStatus() != null) {
            tv_equipment_status.setText(equipment.getEquipmentStatus().getTitle());
        } else {
            tv_equipment_status.setText("неизвестен");
        }

        String sDate;
        RealmResults<TaskStages> stages = realmDB.where(TaskStages.class).equalTo("equipment.uuid", equipment.getUuid()).findAllSorted("endDate", Sort.DESCENDING);
        StageAdapter stageAdapter = new StageAdapter(getApplicationContext(), stages);
        if (stageAdapter.getCount()>0) {
            date = stages.get(0).getEndDate();
            if (date != null && date.after(new Date(100000))) {
                sDate = new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(date);
            } else {
                sDate = "не обслуживалось";
            }
            tv_equipment_check_date.setText(sDate);
        }
        tv_equipment_listview.setAdapter(stageAdapter);

        String path = getExternalFilesDir("/equipment") + File.separator;
        Bitmap image_bitmap = getResizedBitmap(path, getEquipmentImage(equipment.getImage(),equipment), 0, 300, equipment.getChangedAt().getTime());
        if (image_bitmap != null) {
            tv_equipment_image.setImageBitmap(image_bitmap);
        } else {
//            image_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
        }

        RealmResults<Documentation> documentation;
        ListView documentationListView = (ListView) findViewById(R.id.equipment_documentation_listView);
        documentation = realmDB.where(Documentation.class)
                .equalTo("equipment.uuid", equipment.getUuid()).or()
                .equalTo("equipmentModel.uuid", equipment.getEquipmentModel().getUuid())
                .findAll();
//        documentation = realmDB.where(Documentation.class).findAll();
        DocumentationAdapter documentationAdapter = new DocumentationAdapter(getApplicationContext(), documentation);
        if (documentationListView != null) {
            documentationListView.setAdapter(documentationAdapter);
            documentationListView.setOnItemClickListener(new ListViewClickListener());
        }
        setListViewHeightBasedOnChildren(tv_equipment_docslistview);
        setListViewHeightBasedOnChildren(tv_equipment_listview);

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //Floating Action Buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab_4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab_5);

        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);
        show_fab_4 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab4_show);
        hide_fab_4 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab4_hide);
        show_fab_5 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab5_show);
        hide_fab_5 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab5_hide);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!FAB_Status) {
                    expandFAB();
                    FAB_Status = true;
                } else {
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDefect (equipment);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogStatus (equipment);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readRFIDTag (equipment);
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeRFIDTag (equipment);
            }
        });

    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.equipment_layout);
        AccountHeader headerResult;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle("Обслуживание и ремонт");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.login_header)
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

//	private void FillListViewOperations() {
//        TaskAdapter taskAdapter;
//        RealmResults<Tasks> tasks;
//        tasks = realmDB.where(Tasks.class).equalTo("equipmentUuid", equipment_uuid).findAllSorted("startDate");
//        taskAdapter = new TaskAdapter(getApplicationContext(), tasks);
//        lv.setAdapter(taskAdapter);
//	}

    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle("О программе");
        about.show();
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String path;
            String objUuid;
            Documentation documentation = (Documentation) parent.getItemAtPosition(position);
            // TODO: как все таки пути к файлу формируем
            if (documentation.getEquipment() != null && documentation.getEquipmentModel() == null) {
                objUuid = documentation.getEquipment().getUuid();
            } else if (documentation.getEquipment() == null && documentation.getEquipmentModel() != null) {
                objUuid = documentation.getEquipmentModel().getUuid();
            } else if (documentation.getEquipment() != null && documentation.getEquipmentModel() != null) {
                objUuid = documentation.getEquipment().getUuid();
            } else {
                return;
            }

            path = "/documentation/" + objUuid + "/";
            File file = new File(getExternalFilesDir(path), documentation.getPath());
            if (file.exists()) {
                showDocument(file);
            } else {
                // либо сказать что файла нет, либо предложить скачать с сервера
                Log.d(TAG, "Получаем файл документации.");
//				ReferenceServiceHelper rsh = new ReferenceServiceHelper(getApplicationContext(),
//						ReferenceServiceProvider.Actions.ACTION_GET_DOCUMENTATION_FILE);
//				registerReceiver(mReceiverGetDocumentationFile, mFilterGetDocumentationFile);
//				rsh.getDocumentationFile(new String[] { documentation.getUuid() });

                // запускаем поток получения файла с сервера
                AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String fileElements[] = params[0].split("/");
                        String url = ToirApplication.serverUrl + "/storage/" + params[0];
                        Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().getFile(url);
                        try {
                            Response<ResponseBody> r = call1.execute();
                            ResponseBody trueImgBody = r.body();
                            if (trueImgBody == null) {
                                return null;
                            }

                            File file = new File(getApplicationContext().getExternalFilesDir("/documentation/" + fileElements[0]), fileElements[1]);
                            if (!file.getParentFile().exists()) {
                                if (!file.getParentFile().mkdirs()) {
                                    Log.e(TAG, "Не удалось создать папку " +
                                            file.getParentFile().toString() +
                                            " для сохранения файла изображения!");
                                    return null;
                                }
                            }

                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(trueImgBody.bytes());
                            fos.close();
                            return file.getAbsolutePath();
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String filePath) {
                        super.onPostExecute(filePath);
                        loadDocumentationDialog.dismiss();
                        if (filePath != null) {
                            Toast.makeText(getApplicationContext(),
                                    "Файл загружен успешно и готов к просмотру.",
                                    Toast.LENGTH_LONG).show();
                            showDocument(new File(filePath));
                        } else {
                            // сообщаем описание неудачи
                            Toast.makeText(getApplicationContext(), "Ошибка при получении файла.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                task.execute(objUuid + "/" + documentation.getPath());

                // показываем диалог получения наряда
                loadDocumentationDialog = new ProgressDialog(EquipmentInfoActivity.this);
                loadDocumentationDialog.setMessage("Получаем файл документации");
                loadDocumentationDialog.setIndeterminate(true);
                loadDocumentationDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                loadDocumentationDialog.setCancelable(false);
                loadDocumentationDialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//								unregisterReceiver(mReceiverGetDocumentationFile);
                                Toast.makeText(getApplicationContext(),
                                        "Получение файла отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                loadDocumentationDialog.show();
            }
        }

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 5;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void expandFAB() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 2.3);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.05);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 2);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 2);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.05);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 2.3);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);

        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) fab4.getLayoutParams();
        layoutParams4.rightMargin += (int) (fab4.getWidth() * 1.7);
        layoutParams4.bottomMargin += (int) (fab4.getHeight() * 0.9);
        fab4.setLayoutParams(layoutParams4);
        fab4.startAnimation(show_fab_4);
        fab4.setClickable(true);

        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) fab5.getLayoutParams();
        layoutParams5.rightMargin += (int) (fab5.getWidth() * 0.9);
        layoutParams5.bottomMargin += (int) (fab5.getHeight() * 1.7);
        fab5.setLayoutParams(layoutParams5);
        fab5.startAnimation(show_fab_5);
        fab5.setClickable(true);
    }


    private void hideFAB() {

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 2.3);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.05);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 2);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 2);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.05);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 2.3);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);

        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) fab4.getLayoutParams();
        layoutParams4.rightMargin -= (int) (fab4.getWidth() * 1.7);
        layoutParams4.bottomMargin -= (int) (fab4.getHeight() * 0.9);
        fab4.setLayoutParams(layoutParams4);
        fab4.startAnimation(hide_fab_4);
        fab4.setClickable(false);

        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) fab5.getLayoutParams();
        layoutParams5.rightMargin -= (int) (fab5.getWidth() * 0.9);
        layoutParams5.bottomMargin -= (int) (fab5.getHeight() * 1.7);
        fab5.setLayoutParams(layoutParams5);
        fab5.startAnimation(hide_fab_5);
        fab5.setClickable(false);

    }

    public void showDialogDefect(final Equipment equipment) {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.add_defect_dialog, null);

        realmDB = Realm.getDefaultInstance();
        RealmResults<DefectType> defectType = realmDB.where(DefectType.class).findAll();
        final Spinner defectTypeSpinner = (Spinner) alertLayout.findViewById(R.id.spinner_defects_category);
        final DefectTypeAdapter typeSpinnerAdapter = new DefectTypeAdapter(this, defectType);
        defectTypeSpinner.setAdapter(typeSpinnerAdapter);

        defectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DefectType typeSelected = (DefectType) defectTypeSpinner
                        .getSelectedItem();
                if (typeSelected != null) {
                    DefectType currentDefectType = typeSpinnerAdapter.getItem(defectTypeSpinner.getSelectedItemPosition());
                    if (currentDefectType != null) {
                        RealmResults<Defect> defects = realmDB.where(Defect.class).equalTo("DefectType.uuid", currentDefectType.getUuid()).findAll();
                        Spinner defectSpinner = (Spinner) alertLayout.findViewById(R.id.spinner_defects);
                        DefectAdapter defectAdapter = new DefectAdapter(getApplicationContext(), defects);
                        defectSpinner.setAdapter(defectAdapter);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Укажите дефект");
        alert.setView(alertLayout);
        alert.setIcon(R.drawable.ic_icon_tools);
        alert.setCancelable(false);
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView newDefect = (TextView) alertLayout.findViewById(R.id.add_new_comment);
                DefectType currentDefectType = null;
                if (defectTypeSpinner.getSelectedItemPosition() >= 0) {
                    currentDefectType = typeSpinnerAdapter.getItem(defectTypeSpinner.getSelectedItemPosition());
                }
                if (newDefect != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    final Defect defect = realmDB.createObject(Defect.class);
                    UUID uuid = UUID.randomUUID();
                    long next_id = realm.where(Defect.class).max("_id").intValue() + 1;
                    defect.set_id(next_id);
                    defect.setUuid(uuid.toString().toUpperCase());
                    defect.setDate(new Date());
                    defect.setComment(newDefect.getText().toString());
                    defect.setProcess(false);
                    defect.setCreatedAt(new Date());
                    defect.setChangedAt(new Date());
                    defect.setEquipment(equipment);
                    if (currentDefectType != null) {
                        defect.setDefectType(currentDefectType);
                    }
                    defect.setDefectType(null);
                    defect.setContragent(null);
                    defect.setTask(null);
                    realm.commitTransaction();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.add_defect_dialog);
        dialog.show();
    }


    public void showDialogStatus(final Equipment equipment) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.change_status_dialog, null);
        realmDB = Realm.getDefaultInstance();
        RealmResults<EquipmentStatus> equipmentStatus = realmDB.where(EquipmentStatus.class).findAll();
        final Spinner statusSpinner = (Spinner) alertLayout.findViewById(R.id.spinner_status);
        final EquipmentStatusAdapter equipmentStatusAdapter = new EquipmentStatusAdapter(this, R.id.spinner_status, equipmentStatus);
        statusSpinner.setAdapter(equipmentStatusAdapter);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Статус оборудования");
        alert.setView(alertLayout);
        alert.setIcon(R.drawable.ic_icon_tools);
        alert.setCancelable(false);
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                equipment.setEquipmentStatus(equipmentStatusAdapter.getItem(statusSpinner.getSelectedItemPosition()));
                realm.commitTransaction();
            }
        });

        TextView statusCurrent = (TextView) alertLayout.findViewById(R.id.current_status);
        statusCurrent.setText(equipment.getEquipmentStatus().getTitle());

        AlertDialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        //dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_icon_tools);
        dialog.setContentView(R.layout.add_defect_dialog);
        dialog.show();
    }

    public void writeRFIDTag (Equipment equipment) {
        Log.d(TAG, "Пишем в метку пользователя.");
        // сюда нужно перенести код который отвечает за сохранение
        // структуры данных в пользовательскую метку

        Intent intent = getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
        if (intent != null) {
            startActivity(intent);
        }
        //Log.d(TAG, "Пишем в метку оборудования.");
        //Log.d(TAG, "uuid оборудования = " + equipment_uuid);
        //equipment = realmDB.where(Equipment.class).equalTo("uuid", equipment_uuid).findFirst();
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
            data = "000102030405060708090A0B0C0D0E0F";
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

    public void readRFIDTag (Equipment equipment) {
        Log.d(TAG, "Считываем память метки.");
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
}
