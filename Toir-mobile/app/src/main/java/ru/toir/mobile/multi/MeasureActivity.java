package ru.toir.mobile.multi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.multi.db.adapters.MeasureTypeAdapter;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.MeasureType;
import ru.toir.mobile.multi.db.realm.MeasuredValue;
import ru.toir.mobile.multi.db.realm.Operation;

public class MeasureActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;
    protected BarChart mChart;
    AccountHeader headerResult = null;
    private Realm realmDB;
    private EditText meas_value;
    private Spinner meas_typeSpinner;
    private Typeface mTf;
    private Equipment currentEquipment;
    private Operation currentOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String operationUuid;
        String equipmentUuid;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            operationUuid = bundle.getString("operationUuid", null);
            equipmentUuid = bundle.getString("equipmentUuid", null);
            if (operationUuid == null || equipmentUuid == null) {
                return;
            }
        } else {
            return;
        }

        realmDB = Realm.getDefaultInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMainLayout(savedInstanceState);

        final RealmResults<Equipment> equipment = realmDB.where(Equipment.class)
                .equalTo("uuid", equipmentUuid)
                .findAll();
        if (equipment.size() > 0) {
            currentEquipment = equipment.first();
        } else {
            return;
        }

        RealmResults<Operation> operations = realmDB.where(Operation.class)
                .equalTo("uuid", operationUuid)
                .findAll();
        if (operations.size() > 0) {
            currentOperation = operations.first();
        } else {
            return;
        }

        //meas_header = (TextView) findViewById(R.id.meas_header);
        meas_value = findViewById(R.id.meas_value);
        meas_typeSpinner = findViewById(R.id.simple_spinner);

        RealmResults<MeasureType> measureType = realmDB.where(MeasureType.class).findAll();
        MeasureTypeAdapter typeSpinnerAdapter = new MeasureTypeAdapter(measureType);
        typeSpinnerAdapter.notifyDataSetChanged();
        meas_typeSpinner.setAdapter(typeSpinnerAdapter);

        findViewById(R.id.meas_Button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        MeasuredValue measuredValue = new MeasuredValue();
                        UUID uuid = UUID.randomUUID();
                        long next_id = MeasuredValue.getLastId() + 1;
                        MeasureType measureType = (MeasureType) meas_typeSpinner.getSelectedItem();
                        measuredValue.set_id(next_id);
                        measuredValue.setUuid(uuid.toString().toUpperCase());
                        measuredValue.setMeasureType(measureType);
                        measuredValue.setEquipment(currentEquipment);
                        measuredValue.setOperation(currentOperation);
                        measuredValue.setDate(new Date());
                        measuredValue.setChangedAt(new Date());
                        measuredValue.setCreatedAt(new Date());
                        if (meas_value.getText().toString().equals("")) {
                            measuredValue.setValue("0");
                        } else {
                            measuredValue.setValue(meas_value.getText().toString());
                        }

                        realm.copyToRealm(measuredValue);
                    }
                });
                setData();
                Intent data = new Intent();
                data.putExtra("value", meas_value.getText().toString());
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }

                finish();
            }
        });


        mChart = findViewById(R.id.chart1);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(30);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);
        // mChart.setDrawXLabels(false);
        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8);
        //leftAxis.setValueFormatter();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(8);
        rightAxis.setTextColor(Color.WHITE);
        //rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);

        setData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }


    private void setData() {
        int count;
        ArrayList<String> xVals = new ArrayList<>();
        // TODO сделать выбор нужных значений по шаблону операции
        RealmResults<MeasuredValue> measuredValues = realmDB.where(MeasuredValue.class)
                .equalTo("equipment.uuid", currentEquipment.getUuid())
                .findAll();

        count = measuredValues.size();
        for (int i = 0; i < count; i++) {
            // add measured value
            MeasuredValue val = measuredValues.get(i);
            if (val != null) {
                Date dateVal = val.getDate();
                if (dateVal != null) {
                    xVals.add(dateVal.toString());
                } else {
                    xVals.add("0000-00-00 00:00:01");
                }
            }
        }
        //RealmResults<Operation> operations = realmDB.where(Operation.class).findAll();
        //for (Operation operation : operations) {
        //if (operation.getOperationTemplate().getUuid().equals(currentOperation.getOperationTemplateUuid()))
        //}

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (measuredValues.get(i).getValue() != null) {
                yVals1.add(new BarEntry(Float.parseFloat(measuredValues.get(i).getValue()), i));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        // data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);

        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
    }

    public void onNothingSelected() {
    }

    /**
     * Устанавливам основной экран приложения
     */
    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.measure);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.larisaBlueColor));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle(getString(R.string.measure_values));

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
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(getString(R.string.menu_about_app))
                                .withDescription(getString(R.string.menu_information))
                                .withIcon(FontAwesome.Icon.faw_info)
                                .withIdentifier(DRAWER_INFO)
                                .withSelectable(false),
                        new PrimaryDrawerItem().withName(getString(R.string.menu_exit))
                                .withIcon(FontAwesome.Icon.faw_undo)
                                .withIdentifier(DRAWER_EXIT)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_INFO) {
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle(getString(R.string.about_message))
                                        .setMessage(R.string.program_version)
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