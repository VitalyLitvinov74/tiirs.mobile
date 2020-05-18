package ru.toir.mobile;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.Stage;

public class StageInfoActivity extends AppCompatActivity {
    private final static String TAG = "StageInfo";
    private static String stage_uuid;
    CoordinatorLayout rootLayout;
    private Realm realmDB;
    private TextView tv_stage_name;
    private TextView tv_no_operation;
    private ListView tv_stage_listview;
    private Context context;
    private OperationAdapter operationAdapter;
    private ListViewClickListener mainListViewClickListener = new ListViewClickListener();

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
        if (b != null && b.getString("stage_uuid") != null) {
            stage_uuid = b.getString("stage_uuid");
        } else {
            finish();
            return;
        }

        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tv_stage_listview = findViewById(R.id.list_view);
        tv_stage_name = findViewById(R.id.tl_Header);
        tv_no_operation = findViewById(R.id.tl_info);
        initView();
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.operation_info_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle(R.string.menu_order_info);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }
    }

    private void initView() {
        final Stage stage = realmDB.where(Stage.class).equalTo("uuid", stage_uuid).findFirst();
        if (stage == null) {
            return;
        }
        tv_stage_name.setText(stage.getStageTemplate().getTitle());

        RealmResults<Operation> operations = stage.getOperations().sort("_id");
        operationAdapter = new OperationAdapter(operations, context, null);
        tv_stage_listview.setAdapter(operationAdapter);
        tv_stage_listview.setOnItemClickListener(mainListViewClickListener);
        if (operations.size() == 0) {
            tv_no_operation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, View selectedItemView, final int position, long id) {
            operationAdapter.setItemVisibility(position,
                    !operationAdapter.getItemVisibility(position));
            operationAdapter.notifyDataSetChanged();
        }
    }
}
