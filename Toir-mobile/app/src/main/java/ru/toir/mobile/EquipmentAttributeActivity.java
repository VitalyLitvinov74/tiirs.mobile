package ru.toir.mobile;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.db.adapters.AttributeTypeAdapter;
import ru.toir.mobile.db.adapters.EquipmentAttributeAdapter;
import ru.toir.mobile.db.realm.AttributeType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentAttribute;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 18.03.19.
 */
public class EquipmentAttributeActivity extends AppCompatActivity {

    public static final String TAG = EquipmentAttributeActivity.class.getSimpleName();
    String equipmentUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null && b.getString("equipment_uuid") != null) {
            equipmentUuid = b.getString("equipment_uuid");
        } else {
            finish();
            return;
        }

        setMainLayout();

        ListView contentListView = findViewById(R.id.equipment_attribute_list);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EquipmentAttribute> data = realm.where(EquipmentAttribute.class).equalTo("equipment.uuid", equipmentUuid).findAll();
        final EquipmentAttributeAdapter adapter = new EquipmentAttributeAdapter(data);
        contentListView.setAdapter(adapter);
        realm.close();

        AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final EquipmentAttribute attribute = ((EquipmentAttribute) parent.getAdapter().getItem(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(EquipmentAttributeActivity.this);
                builder.setTitle("Изменение атрибута");
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                LayoutInflater inflater = EquipmentAttributeActivity.this.getLayoutInflater();
                View editForm = inflater.inflate(R.layout.edit_equipment_attribute, null, false);
                builder.setView(editForm);

                ((LinearLayout) editForm.findViewById(R.id.attribute_type_section)).removeAllViews();
                ((EditText) editForm.findViewById(R.id.attribute_value)).setText(attribute.getValue());

                final AlertDialog dialog = builder.create();
                dialog.show();

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = dialog.findViewById(R.id.attribute_value);
                        String value;
                        try {
                            value = editText.getText().toString();
                        } catch (Exception e) {
                            // какието проблемы с элементами интерфейса
                            Log.e(TAG, "Error getting attribute value. value: " + editText);
                            dialog.dismiss();
                            return;
                        }

                        if (!value.equals("")) {
                            Date updateDate = new Date();
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            attribute.setValue(value);
                            attribute.setDate(updateDate);
                            attribute.setChangedAt(updateDate);
                            attribute.setSent(false);
                            realm.commitTransaction();
                            realm.close();
                            dialog.dismiss();
                        }
                    }
                };
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(listener);

                return true;
            }
        };
        contentListView.setOnItemLongClickListener(longClickListener);
    }


    void setMainLayout() {
        setContentView(R.layout.equipment_attr_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar2attr);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle("Список атрибутов");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem newAttribute = menu.add("Добавить атрибут");
        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EquipmentAttributeActivity.this)
                        .setTitle("Добавление нового атрибута")
//                        .setMessage("")
                        .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info);
                LayoutInflater inflater = EquipmentAttributeActivity.this.getLayoutInflater();
                View editForm = inflater.inflate(R.layout.edit_equipment_attribute, null, false);
                Realm realm = Realm.getDefaultInstance();
                RealmResults<AttributeType> attributeTypes = realm.where(AttributeType.class).findAll();
                AttributeTypeAdapter adapter = new AttributeTypeAdapter(attributeTypes);
                Spinner attrTypeSpinner = editForm.findViewById(R.id.attribute_type_spinner);
                attrTypeSpinner.setAdapter(adapter);
                realm.close();
                builder.setView(editForm);

                final AlertDialog dialog = builder.create();
                dialog.show();
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Spinner attributeTypeSpinner = dialog.findViewById(R.id.attribute_type_spinner);
                        EditText attrValueEditText = dialog.findViewById(R.id.attribute_value);
                        AttributeType attributeType = null;
                        String valueValue = null;
                        boolean allOk = true;

                        try {
                            attributeType = (AttributeType) attributeTypeSpinner.getSelectedItem();
                            valueValue = attrValueEditText.getText().toString();
                        } catch (Exception e) {
                            allOk = false;
                        }

                        if (allOk) {
                            // проверка на добавление уже существующего атрибута
                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<EquipmentAttribute> existsAttribute = realm
                                    .where(EquipmentAttribute.class)
                                    .equalTo("equipment.uuid", equipmentUuid)
                                    .equalTo("attributeType.uuid", attributeType.getUuid()).findAll();

                            if (existsAttribute.size() > 0) {
                                Toast.makeText(getApplicationContext(), "Такой атрибут уже есть!", Toast.LENGTH_LONG).show();
                            }

                            if (!valueValue.equals("")) {
                                Equipment equipment = realm.where(Equipment.class).equalTo("uuid", equipmentUuid).findFirst();
                                EquipmentAttribute attribute = new EquipmentAttribute();
                                long next_id = EquipmentAttribute.getLastId() + 1;
                                Date createDate = new Date();
                                attribute.set_id(next_id);
                                attribute.setUuid(UUID.randomUUID().toString().toUpperCase());
                                attribute.setAttributeType(attributeType);
                                attribute.setEquipment(equipment);
                                attribute.setDate(createDate);
                                attribute.setValue(valueValue);
                                attribute.setChangedAt(createDate);
                                attribute.setCreatedAt(createDate);
                                realm.beginTransaction();
                                realm.copyToRealm(attribute);
                                realm.commitTransaction();

                                dialog.dismiss();
                            }

                            realm.close();
                        } else {
                            // какието проблемы с элементами интерфейса
                            Log.e(TAG, "Error getting attribute and value. spinner: " + attributeTypeSpinner + "value: " + attrValueEditText);
                            dialog.dismiss();
                        }

                    }
                };
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(listener);

                return true;
            }
        };
        newAttribute.setOnMenuItemClickListener(listener);

        return true;
    }
}
