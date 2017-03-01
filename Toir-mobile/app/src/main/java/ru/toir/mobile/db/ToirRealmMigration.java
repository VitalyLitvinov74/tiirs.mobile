package ru.toir.mobile.db;

import android.util.Log;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmException;
import ru.toir.mobile.db.realm.User;

/**
 * @author Dmitriy Logachev
 *
 */
public class ToirRealmMigration implements RealmMigration {
    private final String TAG = this.getClass().getName();

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d(TAG, "oldVersion = " + oldVersion);
        Log.d(TAG, "newVersion = " + newVersion);

        if (oldVersion == newVersion) {
            if (!testPropsFields(realm)) {
                throw new RealmException("Классы и схема не идентичны!!!");
            }

            return;
        }

        if (oldVersion == 0) {
            Log.d(TAG, "from version 0");
            schema.create("RepairPartType")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addRealmObjectField("parentRepairType", schema.get("RepairPartType"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("RepairPart")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addRealmObjectField("equipmentModel", schema.get("EquipmentModel"))
                    .addRealmObjectField("repairPartType", schema.get("RepairPartType"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("Tool")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("title", String.class)
                    .addRealmObjectField("toolType", schema.get("ToolType"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.create("MeasuredValue")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addRealmObjectField("equipment", schema.get("Equipment"))
                    .addRealmObjectField("operation", schema.get("Operation"))
                    .addRealmObjectField("measureType", schema.get("MeasureType"))
                    .addField("date", Date.class)
                    .addField("value", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            oldVersion++;
        }

        if (oldVersion == 1) {
            Log.d(TAG, "from version 1");
            schema.create("Clients")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addField("name", String.class)
                    .addField("description", String.class)
                    .addField("phone", String.class)
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.get("Documentation").renameField("filename","path");
            schema.get("Equipment").removeField("equipmentModelUuid");
            schema.get("Equipment").removeField("equipmentStatusUuid");
            schema.get("Equipment").removeField("criticalTypeUuid");
            schema.get("Equipment").removeField("userUuid");
            schema.get("Equipment").addRealmObjectField("parentEquipment",schema.get("Equipment"));

            schema.get("EquipmentModel").removeField("equipmentTypeUuid");

            schema.create("GpsTrack")
                    .addField("_id", long.class)
                    .addField("userUuid", String.class)
                    .addField("date", Date.class)
                    .addField("longitude", double.class)
                    .addField("latitude", double.class)
                    .addPrimaryKey("_id");

            schema.create("Journal")
                    .addField("_id", long.class)
                    .addField("description", String.class)
                    .addField("userUuid", String.class)
                    .addField("date", Date.class)
                    .addPrimaryKey("_id");

            schema.get("Operation")
                    .removeField("taskStageUuid")
                    .removeField("operationVerdictUuid")
                    .removeField("operationStatusUuid")
                    .removeField("operationTemplateUuid");

            schema.get("OperationTemplate").removeField("equipmentModelUuid");
            schema.get("OperationTemplate").removeField("operationTypeUuid");

            schema.create("OperationTool")
                    .addField("_id", long.class)
                    .addField("uuid", String.class)
                    .addRealmObjectField("operationTemplate",schema.get("OperationTemplate"))
                    .addRealmObjectField("tool",schema.get("Tool"))
                    .addField("createdAt", Date.class)
                    .addField("changedAt", Date.class)
                    .addPrimaryKey("_id");

            schema.get("Orders")
                    .removeField("orderStatusUuid")
                    .removeField("orderVerdictUuid");

            schema.get("RepairPart")
                    .addField("commonRepairPartFlag", int.class);

            schema.get("TaskStageTemplate")
                    .removeField("equipmentModelUuid")
                    .removeField("taskStageTypeUuid");

            schema.get("TaskTemplate")
                    .removeField("equipmentModelUuid")
                    .removeField("taskTypeUuid");

            schema.get("TaskType")
                    .removeField("icon");

            schema.get("TaskVerdict")
                    .addRealmObjectField("taskType",schema.get("TaskType"));

            schema.get("Tasks")
                    .removeField("orderUuid")
                    .removeField("equipmentUuid")
                    .removeField("taskVerdictUuid")
                    .removeField("taskStatusUuid")
                    .removeField("taskTemplateUuid");

            oldVersion++;
        }

        if (oldVersion == 2) {
            Log.d(TAG, "from version 2");
            schema.create("ReferenceUpdate")
                    .addField("referenceName", String.class)
                    .addField("updateDate", Date.class)
                    .addPrimaryKey("referenceName");

            oldVersion++;
        }

        if (oldVersion == 3) {
            Log.d(TAG, "from version 3");
            schema.get("Clients")
                    .renameField("photo", "phone");

            oldVersion++;
        }

        if (oldVersion == 4) {
            Log.d(TAG, "from version 4");
            schema.get("Documentation")
                    .removeField("documentationTypeUuid")
                    .removeField("equipmentUuid");
            schema.get("Operation")
                    .addRealmObjectField("taskStage", schema.get("TaskStages"));

            oldVersion++;
        }

        if (oldVersion == 5) {
            Log.d(TAG, "from version 5");
            schema.get("Journal").addField("sent", boolean.class);
            schema.get("GpsTrack").addField("sent", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 6) {
            Log.d(TAG, "from version 6");
            schema.get("Orders").addField("sent", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 7) {
            Log.d(TAG, "from version 7");
            schema.get("TaskStages")
                    .removeField("equipmentUuid")
                    .removeField("taskStageVerdictUuid")
                    .removeField("taskStageStatusUuid")
                    .removeField("taskStageTemplateUuid");
            oldVersion++;
        }

        if (oldVersion == 8) {
            Log.d(TAG, "from version 8");
            schema.get("Orders")
                    .removeField("userUuid")
                    .removeField("authorUuid")
                    .addRealmObjectField("user", schema.get("User"))
                    .addRealmObjectField("author", schema.get("User"));
            oldVersion++;
        }

        testPropsFields(realm);
    }

    private boolean testPropsFields(DynamicRealm realm) {
        boolean result = true;
        RealmSchema schema = realm.getSchema();
        // проверяем соответствие схемы базы со свойствами классов
        Set<RealmObjectSchema> realmObjects = schema.getAll();
        for (RealmObjectSchema realmObject: realmObjects) {
            Log.d(TAG, "Class name = " + realmObject.getClassName());
            Field[] classProps = null;
            Set<String> props= new HashSet<>();
            Map<String, String> propsType = new HashMap<>();
            try {
                Class<?> c = Class.forName("ru.toir.mobile.db.realm." + realmObject.getClassName());
                classProps = c.getDeclaredFields();
                for (Field prop: classProps) {
                    props.add(prop.getName());
                    propsType.put(prop.getName(), prop.getType().getName());
//                    propsType.put(prop.getName(), prop.getGenericType().toString());
                }
            } catch(Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            // проверяем количество и названия полей и свойств
            Set<String> fieldNames = realmObject.getFieldNames();
            if (fieldNames.containsAll(props)) {
                Log.d(TAG, "Status: Идентичны!!!");
            } else {
                Log.d(TAG, "Status: Отличаются!!!");
                result = false;
                // TODO: реализовать поиск различий в списках
            }

            // сравниваем типы свойств и полей
            for (String fieldName: fieldNames) {
                String realmType = realmObject.getFieldType(fieldName).name();
                String propType = propsType.get(fieldName);
                if (!realmType.equals(getType(propType))) {
                    Log.e(TAG, "Type not same (fName = " + fieldName + "): fType = " + realmType + ", pType = " + propType);
                    result = false;
                }
            }
        }

        return result;
    }

    private String getType(String type) {
        String[] array = type.split("\\.");
        String result = array[array.length - 1].toUpperCase();

        switch (result) {
            case "INT":
            case "LONG":
                result = "INTEGER";
                break;
            case "STRING":
            case "DOUBLE":
            case "DATE":
            case "FLOAT":
            case "BOOLEAN":
                break;
            case "REALMLIST":
                result = "LIST";
                break;
            default:
                result = "OBJECT";
        }

        return result;
    }

}
