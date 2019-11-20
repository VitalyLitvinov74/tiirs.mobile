package ru.toir.mobile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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

import java.text.DateFormat;

import io.realm.Realm;
import ru.toir.mobile.db.realm.Defect;

public class DefectInfoActivity extends AppCompatActivity {
    private final static String TAG = "DefectInfoActivity";
    private static final int DRAWER_EXIT = 14;
    private static String defect_uuid;
    private static String equipment_uuid;
    private Realm realmDB;
    private ImageView tv_defect_image;
    private TextView tv_defect_text_name;
    private TextView tv_defect_text_type;
    private TextView tv_equipment_text_uuid;
    private TextView tv_defect_user_name;
    private TextView tv_defect_text_date;
    private TextView tv_defect_text_status;
    private TextView tv_defect_comment;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString("defect_uuid") != null) {
            defect_uuid = b.getString("defect_uuid");
        } else {
            finish();
            return;
        }

        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv_defect_image = findViewById(R.id.defect_image);
        tv_defect_text_name = findViewById(R.id.defect_text_name);
        tv_defect_text_type = findViewById(R.id.defect_text_type);
        tv_equipment_text_uuid = findViewById(R.id.equipment_text_uuid);
        tv_defect_user_name = findViewById(R.id.defect_user_name);
        tv_defect_text_date = findViewById(R.id.defect_text_date);
        tv_defect_text_status = findViewById(R.id.defect_text_status);
        tv_defect_comment = findViewById(R.id.defect_comment);
        //fab_goto_equipment = findViewById(R.id.fab_goto_equipment);

        initView();
    }

    private void initView() {
        final Defect defect = realmDB.where(Defect.class).equalTo("uuid", defect_uuid).findFirst();
        if (defect == null) {
            Toast.makeText(getApplicationContext(), "Дефект не найден", Toast.LENGTH_LONG).show();
            return;
        }
        equipment_uuid = defect.getEquipment().getUuid();

        // TODO когда будет сущность с изображениями - вставить сюда
        //tv_defect_image.setImageResource();

        tv_defect_text_name.setText("Дефект #".concat(String.valueOf(defect.get_id())));
        tv_defect_text_type.setText(defect.getDefectType().getTitle());
        tv_equipment_text_uuid.setText(defect.getEquipment().getTitle());
        tv_defect_user_name.setText(defect.getUser().getName());
        if (defect.getDate() != null) {
            tv_defect_text_date.setText(DateFormat.getDateTimeInstance().format(defect.getDate()));
        } else {
            tv_defect_text_date.setText("-");
        }
        if (defect.isProcess()) {
            tv_defect_text_status.setText(R.string.defect_status_processed);
        } else {
            tv_defect_text_status.setText(R.string.defect_status_non_processed);
        }
        tv_defect_comment.setText(defect.getComment());

        findViewById(R.id.fab_goto_equipment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent equipmentInfo = new Intent(context, EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("equipment_uuid", equipment_uuid);
                equipmentInfo.putExtras(bundle);
                context.startActivity(equipmentInfo);
            }
        });
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.defect_layout);

        AccountHeader headerResult;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle(R.string.subtitle_repair);

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
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            System.exit(0);
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        if (savedInstanceState == null) {
            result.setSelection(21, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }
}
