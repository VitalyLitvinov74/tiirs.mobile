package ru.toir.mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.db.adapters.DefectAdapter;
import ru.toir.mobile.db.adapters.DefectTypeAdapter;
import ru.toir.mobile.db.adapters.DocumentationAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusAdapter;
import ru.toir.mobile.db.adapters.StageAdapter;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.rest.GetDocumentationAsyncTask;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.TagStructure;
import ru.toir.mobile.utils.DataUtils;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

public class EquipmentInfoActivity extends AppCompatActivity {
    private final static String TAG = "EquipmentInfoActivity";
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    //private static final int DIALOG_SET_DEFECT = 1;
    //private static final int DIALOG_SET_STATUS = 2;
    CoordinatorLayout rootLayout;

    //Spinner defectTypeSpinner;

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
    private ListView tv_equipment_defects;
    // диалог для работы с rfid считывателем
    private RfidDialog rfidDialog;
    private boolean FAB_Status = false;

    private Context context;

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

    public static Intent showDocument(File file, Context context) {
        MimeTypeMap mt = MimeTypeMap.getSingleton();
        String[] patternList = file.getName().split("\\.");
        String extension = patternList[patternList.length - 1];

        if (mt.hasExtension(extension)) {
            String mimeType = mt.getMimeTypeFromExtension(extension);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.setType(mimeType);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                target.setData(Uri.fromFile(file));
            } else {
                Uri doc = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                target.setData(doc);
                target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            return Intent.createChooser(target, "Open File");
        } else {
            return null;
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

        context = this;

        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString("equipment_uuid") != null) {
            equipment_uuid = b.getString("equipment_uuid");
        } else {
            finish();
            return;
        }

        //setContentView(R.layout.equipment_layout);
        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv_equipment_name = findViewById(R.id.equipment_text_name);
        tv_equipment_inventory = findViewById(R.id.equipment_text_inventory);
        tv_equipment_uuid = findViewById(R.id.equipment_text_uuid);
        tv_equipment_position = findViewById(R.id.equipment_text_location);
        tv_equipment_task_date = findViewById(R.id.equipment_text_date);
        tv_equipment_check_date = findViewById(R.id.equipment_text_date2);
        tv_equipment_image = findViewById(R.id.equipment_image);
        tv_equipment_listview = findViewById(R.id.list_view);
        tv_equipment_docslistview = findViewById(R.id.equipment_documentation_listView);
        tv_equipment_status = findViewById(R.id.equipment_text_status);
        tv_equipment_id = findViewById(R.id.equipment_text_id);
        tv_equipment_defects = findViewById(R.id.equipment_defects_listView);

        initView();
    }

    private void initView() {
        final Equipment equipment = realmDB.where(Equipment.class).equalTo("uuid", equipment_uuid).findFirst();
        if (equipment == null) {
            Toast.makeText(getApplicationContext(), "Неизвестное оборудование!!", Toast.LENGTH_LONG).show();
            return;
        }

        EquipmentModel equipmentModel = equipment.getEquipmentModel();
        String textData;
        tv_equipment_name.setText(equipment.getTitle());
        if (equipmentModel != null) {
            textData = getString(R.string.model, equipment.getEquipmentModel().getTitle()) + " | " + equipment.getEquipmentModel().getEquipmentType().getTitle();
            tv_equipment_inventory.setText(textData);
        }

        tv_equipment_id.setText(getString(R.string.id, equipment.getInventoryNumber()));
        tv_equipment_uuid.setText(equipment.getUuid());
//        tv_equipment_type.setText("Модель: " + equipment.getEquipmentModel().getTitle());
        if (equipment.getLatitude() > 0) {
            textData = String.valueOf(equipment.getLatitude()) + " / "
                    + String.valueOf(equipment.getLongitude());
            tv_equipment_position.setText(textData);
        } else {
            if (equipment.getLocation() != null) {
                textData = String.valueOf(equipment.getLocation().getLatitude()) + " / "
                        + String.valueOf(equipment.getLocation().getLongitude());
                tv_equipment_position.setText(textData);
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
        RealmResults<Stage> stages = realmDB.where(Stage.class).equalTo("equipment.uuid", equipment.getUuid()).findAllSorted("endDate", Sort.DESCENDING);
        if (stages.size() > 2) {
            stages.subList(0, 2);
        }

        StageAdapter stageAdapter = new StageAdapter(stages);
        if (stageAdapter.getCount() > 0) {
            date = stages.get(0).getEndDate();
            if (date != null) {
                sDate = new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(date);
            } else {
                sDate = "не обслуживалось";
            }

            tv_equipment_check_date.setText(sDate);
        }

        tv_equipment_listview.setAdapter(stageAdapter);

        RealmResults<Defect> defects = realmDB.where(Defect.class).equalTo("equipment.uuid", equipment.getUuid()).findAllSorted("date", Sort.DESCENDING);
        if (defects.size() > 2) {
            defects.subList(0, 2);
        }

        DefectAdapter defectAdapter = new DefectAdapter(defects);
        tv_equipment_defects.setAdapter(defectAdapter);

        String imgPath = equipment.getAnyImageFilePath();
        String fileName = equipment.getAnyImage();
        if (imgPath != null && fileName != null) {
            File path = getExternalFilesDir(imgPath);
            if (path != null) {
                Bitmap tmpBitmap = getResizedBitmap(path + File.separator,
                        fileName, 300, 0, equipment.getChangedAt().getTime());
                if (tmpBitmap != null) {
                    tv_equipment_image.setImageBitmap(tmpBitmap);
                }
            }
        }

        RealmResults<Documentation> documentation;
        ListView documentationListView = findViewById(R.id.equipment_documentation_listView);
        documentation = realmDB.where(Documentation.class)
                .equalTo("equipment.uuid", equipment.getUuid()).or()
                .equalTo("equipmentModel.uuid", equipment.getEquipmentModel().getUuid())
                .findAll();
//        documentation = realmDB.where(Documentation.class).findAll();
        DocumentationAdapter documentationAdapter = new DocumentationAdapter(documentation);
        if (documentationListView != null) {
            documentationListView.setAdapter(documentationAdapter);
            documentationListView.setOnItemClickListener(new ListViewClickListener());
        }

        setListViewHeightBasedOnChildren(tv_equipment_docslistview);
        setListViewHeightBasedOnChildren(tv_equipment_listview);

        rootLayout = findViewById(R.id.coordinatorLayout);

        findViewById(R.id.chg_eq_tag_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);

                alert.setTitle("Изменение метки оборудования");
                alert.setMessage("Вы действительно хотите изменить метку?");
                DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Handler handler = new Handler(new Handler.Callback() {

                            @Override
                            public boolean handleMessage(Message msg) {
                                Log.d(TAG, "Получили сообщение из драйвера.");

                                if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                                    final String tagId = ((String) msg.obj).substring(4);
                                    Log.d(TAG, tagId);
                                    Toast.makeText(getApplicationContext(),
                                            "Чтение метки успешно.", Toast.LENGTH_SHORT)
                                            .show();

                                    Call<Boolean> callSetTagId = ToirAPIFactory.getEquipmentService()
                                            .setTagId(equipment.getUuid(), tagId);
                                    Callback<Boolean> callback = new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(Call<Boolean> responseBodyCall, Response<Boolean> response) {
                                            boolean result = response.body();
                                            if (result) {
                                                Realm realm = Realm.getDefaultInstance();
                                                realm.beginTransaction();
                                                equipment.setTagId(tagId);
                                                realm.commitTransaction();
                                                realm.close();
                                                Toast.makeText(context, "Метка изменена.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(context, "Не удалось изменить метку.", Toast.LENGTH_LONG).show();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<Boolean> responseBodyCall, Throwable t) {
                                            Toast.makeText(context, "Не удалось изменить метку.", Toast.LENGTH_LONG).show();
                                            t.printStackTrace();
                                        }
                                    };
                                    callSetTagId.enqueue(callback);

                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Не удалось считать метку.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // закрываем диалог
                                rfidDialog.dismiss();
                                return true;
                            }
                        });

                        rfidDialog = new RfidDialog();
                        rfidDialog.setHandler(handler);
                        rfidDialog.readTagId();
                        rfidDialog.show(getFragmentManager(), TAG);
                    }
                };
                alert.setPositiveButton("OK", clickListener);
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });

        //Floating Action Buttons
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.fab_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDefect(equipment, (ViewGroup) v.getParent());
            }
        });

        findViewById(R.id.fab_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogStatus(equipment, (ViewGroup) v.getParent());
            }
        });

        findViewById(R.id.fab_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.fab_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readRFIDTag(equipment);
            }
        });

        findViewById(R.id.fab_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeRFIDTag(equipment);
            }
        });

    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.equipment_layout);
        AccountHeader headerResult;
        Toolbar toolbar = findViewById(R.id.toolbar);
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

    public void startAboutDialog() {
        AboutDialog about = new AboutDialog(this);
        about.setTitle("О программе");
        about.show();
    }

    private void expandFAB() {
        showFloatingActionButton(R.id.fab_1, R.anim.fab1_show, 2.3, 0.05);
        showFloatingActionButton(R.id.fab_2, R.anim.fab2_show, 2, 2);
        showFloatingActionButton(R.id.fab_3, R.anim.fab3_show, 0.05, 2.3);
        showFloatingActionButton(R.id.fab_4, R.anim.fab4_show, 1.7, 0.9);
        showFloatingActionButton(R.id.fab_5, R.anim.fab5_show, 0.9, 1.7);
        showFloatingActionButton(R.id.chg_eq_tag_id, R.anim.chg_eq_tag_id_show, 0.05, 1.5);
    }

    private void showFloatingActionButton(int buttonId, int animationId, double kw, double kh) {
        FloatingActionButton fab = findViewById(buttonId);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
        lp.rightMargin += (int) (fab.getWidth() * kw);
        lp.bottomMargin += (int) (fab.getHeight() * kh);
        fab.setLayoutParams(lp);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), animationId);
        fab.startAnimation(animation);
        fab.setClickable(true);
    }

    private void hideFAB() {
        hideFloatingActionButton(R.id.fab_1, R.anim.fab1_hide, 2.3, 0.05);
        hideFloatingActionButton(R.id.fab_2, R.anim.fab2_hide, 2, 2);
        hideFloatingActionButton(R.id.fab_3, R.anim.fab3_hide, 0.05, 2.3);
        hideFloatingActionButton(R.id.fab_4, R.anim.fab4_hide, 1.7, 0.9);
        hideFloatingActionButton(R.id.fab_5, R.anim.fab5_hide, 0.9, 1.7);
        hideFloatingActionButton(R.id.chg_eq_tag_id, R.anim.chg_eq_tag_id_hide, 0.05, 1.5);
    }

    private void hideFloatingActionButton(int buttonId, int animationId, double kw, double kh) {
        FloatingActionButton fab = findViewById(buttonId);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
        lp.rightMargin -= (int) (fab.getWidth() * kw);
        lp.bottomMargin -= (int) (fab.getHeight() * kh);
        fab.setLayoutParams(lp);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), animationId);
        fab.startAnimation(animation);
        fab.setClickable(false);
    }

    public void showDialogDefect(final Equipment equipment, ViewGroup parent) {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.add_defect_dialog, parent, false);

        RealmResults<DefectType> defectType = realmDB.where(DefectType.class).findAll();
        final Spinner defectTypeSpinner = alertLayout.findViewById(R.id.spinner_defects);
        final DefectTypeAdapter typeSpinnerAdapter = new DefectTypeAdapter(defectType);
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
                        Spinner defectSpinner = alertLayout.findViewById(R.id.spinner_defects);
                        DefectAdapter defectAdapter = new DefectAdapter(defects);
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
        alert.setIcon(R.drawable.ic_icon_warnings);
        alert.setCancelable(false);
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView newDefect = alertLayout.findViewById(R.id.add_new_comment);
                DefectType currentDefectType = null;
                if (defectTypeSpinner.getSelectedItemPosition() >= 0) {
                    currentDefectType = typeSpinnerAdapter.getItem(defectTypeSpinner.getSelectedItemPosition());
                }

                if (newDefect != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    final Defect defect = realm.createObject(Defect.class);
                    UUID uuid = UUID.randomUUID();
                    long next_id = Defect.getLastId() + 1;
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
                    } else {
                        defect.setDefectType(null);
                    }

                    AuthorizedUser authUser = AuthorizedUser.getInstance();
                    User user = realm.where(User.class).equalTo("tagId", authUser.getTagId()).findFirst();
                    if (user != null) {
                        defect.setUser(user);
                    } else {
                        defect.setUser(null);
                    }

                    defect.setTask(null);
                    realm.commitTransaction();
                    realm.close();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.add_defect_dialog);
        dialog.show();
    }

    public void showDialogStatus(final Equipment equipment, ViewGroup parent) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.change_status_dialog, parent, false);
        RealmResults<EquipmentStatus> equipmentStatus = realmDB.where(EquipmentStatus.class).findAll();
        final Spinner statusSpinner = alertLayout.findViewById(R.id.spinner_status);
        final EquipmentStatusAdapter equipmentStatusAdapter = new EquipmentStatusAdapter(equipmentStatus);
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
                realm.close();
            }
        });

        TextView statusCurrent = alertLayout.findViewById(R.id.current_status);
        statusCurrent.setText(equipment.getEquipmentStatus().getTitle());

        AlertDialog dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        //dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_icon_tools);
        dialog.setContentView(R.layout.add_defect_dialog);
        dialog.show();
    }

    public void writeRFIDTag(Equipment equipment) {
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
            //data = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
            //byte[] byteData = tag.getBinary();
            //data = DataUtils.toHexString(byteData);
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

    public void readRFIDTag(Equipment equipment) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // добавляем элемент меню для обновления справочников
        MenuItem attributes = menu.add("Атрибуты");
        MenuItem.OnMenuItemClickListener clickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                Activity activity = getActivity();
//                if (activity == null) {
//                    return false;
//                }

                Intent equipmentInfo = new Intent(getApplicationContext(), EquipmentAttributeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("equipment_uuid", equipment_uuid);
                equipmentInfo.putExtras(bundle);
                startActivity(equipmentInfo);
                return true;
            }
        };
        attributes.setOnMenuItemClickListener(clickListener);

        return true;
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Documentation documentation = (Documentation) parent.getItemAtPosition(position);

            final File file = new File(getExternalFilesDir(documentation.getImageFilePath()),
                    documentation.getPath());
            if (file.exists()) {
                Intent intent = showDocument(file, getApplicationContext());
                if (intent != null) {
                    startActivity(intent);
                }
            } else {
                // либо сказать что файла нет, либо предложить скачать с сервера
                Log.d(TAG, "Получаем файл документации.");

                // диалог при загрузке файла документации
                ProgressDialog dialog;
                dialog = new ProgressDialog(EquipmentInfoActivity.this);
                dialog.setMessage("Получаем файл документации");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),
                                        "Получение файла отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // открываем загруженный документ (если он загрузился)
                        if (file.exists()) {
                            showDocument(file, getApplicationContext());
                        } else {
                            Toast.makeText(getBaseContext(), "Файл не удалось загрузить.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();

                // запускаем поток получения файла с сервера
                GetDocumentationAsyncTask task = new GetDocumentationAsyncTask(dialog,
                        getBaseContext().getExternalFilesDir(""));
                String userName = AuthorizedUser.getInstance().getLogin();
                task.execute(documentation.getPath(), documentation.getImageFilePath(), documentation.getImageFileUrl(userName));
            }
        }
    }
}
