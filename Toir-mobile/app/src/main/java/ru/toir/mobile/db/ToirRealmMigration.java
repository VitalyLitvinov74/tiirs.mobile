package ru.toir.mobile.db;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmException;
import ru.toir.mobile.db.realm.Contragent;

/**
 * @author Dmitriy Logachev
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

            schema.get("Documentation").renameField("filename", "path");
            schema.get("Equipment").removeField("equipmentModelUuid");
            schema.get("Equipment").removeField("equipmentStatusUuid");
            schema.get("Equipment").removeField("criticalTypeUuid");
            schema.get("Equipment").removeField("userUuid");
            schema.get("Equipment").addRealmObjectField("parentEquipment", schema.get("Equipment"));

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
                    .addRealmObjectField("operationTemplate", schema.get("OperationTemplate"))
                    .addRealmObjectField("tool", schema.get("Tool"))
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
                    .addRealmObjectField("taskType", schema.get("TaskType"));

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

        if (oldVersion == 9) {
            toVersion10(realm);
            oldVersion++;
        }

        if (oldVersion == 10) {
            toVersion11(realm);
            oldVersion++;
        }

        if (oldVersion == 11) {
            toVersion12(realm);
            oldVersion++;
        }

        if (oldVersion == 12) {
            toVersion13(realm);
            oldVersion++;
        }

        if (oldVersion == 13) {
            toVersion14(realm);
            oldVersion++;
        }

        if (oldVersion == 14) {
            toVersion15(realm);
            oldVersion++;
        }

        if (oldVersion == 15) {
            toVersion16(realm);
            oldVersion++;
        }

        if (oldVersion == 16) {
            toVersion17(realm);
            oldVersion++;
        }

        if (oldVersion == 17) {
            toVersion18(realm);
            oldVersion++;
        }

        if (oldVersion == 18) {
            toVersion19(realm);
            oldVersion++;
        }

        testPropsFields(realm);
    }

    /**
     * Переход на версию 10
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion10(DynamicRealm realm) {
        Log.d(TAG, "from version 9");
        RealmSchema schema = realm.getSchema();
        schema.get("Documentation").addField("required", boolean.class);

    }

    /**
     * Переход на версию 11
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion11(DynamicRealm realm) {
        Log.d(TAG, "from version 10");
        RealmSchema schema = realm.getSchema();
        schema.get("MeasuredValue")
                .removePrimaryKey()
                .addPrimaryKey("uuid")
                .addIndex("_id");
    }

    /**
     * Переход на версию 12
     *
     * @param realm экземпляр realmDB
     */
    private void toVersion12(DynamicRealm realm) {
        Log.d(TAG, "from version 11");
        RealmSchema schema = realm.getSchema();
        schema.create("ObjectType")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addField("descr", String.class)
                .addField("icon", String.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
        schema.create("Objects")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addField("descr", String.class)
                .addField("photo", String.class)
                .addField("longitude", double.class)
                .addField("latitude", double.class)
                .addRealmObjectField("objectType", schema.get("ObjectType"))
                .addRealmObjectField("parentObject", schema.get("Objects"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
    }

    /**
     * Переход на версию 13
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion13(DynamicRealm realm) {
        Log.d(TAG, "from version 12");
    }

    /**
     * Переход на версию 14
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion14(DynamicRealm realm) {
        Log.d(TAG, "from version 13");
        RealmSchema schema = realm.getSchema();
        schema.get("EquipmentModel").addField("image", String.class);

    }

    /**
     * Переход на версию 15
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion15(DynamicRealm realm) {
        Log.d(TAG, "from version 14");
        RealmSchema schema = realm.getSchema();
        schema.rename("TaskStageVerdict", "StageVerdict");
        schema.rename("TaskStageType", "StageType");
        schema.rename("TaskStageStatus", "StageStatus");
        schema.rename("TaskStageTemplate", "StageTemplate");

        RealmObjectSchema objSchema = schema.get("TaskStages");
        objSchema.addRealmObjectField("stageVerdict", schema.get("StageVerdict"))
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set("stageVerdict", obj.get("taskStageVerdict"));
                    }
                })
                .removeField("taskStageVerdict")
                .renameField("stageVerdict", "taskStageVerdict");

        objSchema.addRealmObjectField("stageStatus", schema.get("StageStatus"))
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set("stageStatus", obj.get("taskStageStatus"));
                    }
                })
                .removeField("taskStageStatus")
                .renameField("stageStatus", "taskStageStatus");

        objSchema.addRealmObjectField("stageTemplate", schema.get("StageTemplate"))
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set("stageTemplate", obj.get("taskStageTemplate"));
                    }
                })
                .removeField("taskStageTemplate")
                .renameField("stageTemplate", "taskStageTemplate");

        objSchema = schema.get("StageTemplate");
        objSchema.addRealmObjectField("stageType", schema.get("StageType"))
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set("stageType", obj.get("taskStageType"));
                    }
                })
                .removeField("taskStageType")
                .renameField("stageType", "taskStageType");

        objSchema = schema.get("TaskStageList");
        objSchema.addRealmObjectField("stageTemplate", schema.get("StageTemplate"))
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set("stageTemplate", obj.get("taskStageTemplate"));
                    }
                })
                .removeField("taskStageTemplate")
                .renameField("stageTemplate", "taskStageTemplate");

        objSchema = schema.get("TaskStageOperationList");
        objSchema.addRealmObjectField("stageTemplate", schema.get("StageTemplate"))
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(DynamicRealmObject obj) {
                        obj.set("stageTemplate", obj.get("taskStageTemplate"));
                    }
                })
                .removeField("taskStageTemplate")
                .renameField("stageTemplate", "taskStageTemplate");
    }

    /**
     * Переход на версию 16
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion16(DynamicRealm realm) {
        Log.d(TAG, "from version 15");
        RealmSchema schema = realm.getSchema();
        schema.get("Documentation").addRealmObjectField("equipmentModel", schema.get("EquipmentModel"));
    }

    /**
     * Переход на версию 17
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion17(DynamicRealm realm) {
        Log.d(TAG, "from version 16");
        RealmSchema schema = realm.getSchema();
        schema.get("Objects").renameField("descr", "description");
        schema.get("ObjectType").renameField("descr", "description");
    }

    /**
     * Переход на версию 18
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion18(DynamicRealm realm) {
        Log.d(TAG, "from version 17");
        RealmSchema schema = realm.getSchema();
        schema.get("Equipment").removeField("location")
                .addRealmObjectField("location", schema.get("Objects"));
    }

    /**
     * Переход на версию 19
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion19(DynamicRealm realm) {
        Log.d(TAG, "from version 18");
        RealmSchema schema = realm.getSchema();
        schema.rename("Clients", "Contragent");
        schema.get("Contragent").addField("contragentType", int.class);
        schema.get("Contragent").addRealmObjectField("parentContragent", schema.get("Contragent"));
        schema.create("Brigade")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addRealmObjectField("contragent", schema.get("Contragent"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("ContragentUser")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("contragent", schema.get("Contragent"))
                .addRealmObjectField("user", schema.get("User"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("BrigadeUser")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("brigade", schema.get("Brigade"))
                .addRealmObjectField("user", schema.get("User"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.get("Orders").addRealmObjectField("customer", schema.get("Contragent"));
        schema.get("Orders").addRealmObjectField("perpetrator", schema.get("Brigade"));

        schema.create("DefectType")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addRealmObjectField("equipmentType", schema.get("EquipmentType"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("Defect")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("contragent", schema.get("Contragent"))
                .addField("date", Date.class)
                .addRealmObjectField("equipment", schema.get("Equipment"))
                .addRealmObjectField("defectType", schema.get("DefectType"))
                .addField("process", boolean.class)
                .addField("comment", String.class)
                .addRealmObjectField("task", schema.get("Tasks"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
/*
        schema.create("MeasureValueType")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addField("units", String.class)
                .addField("createdAt", Date.class)
               .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
*/
        schema.get("OperationTool").addField("quantity", int.class);

        schema.create("TaskTemplateRepairPart")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("taskTemplate", schema.get("TaskTemplate"))
                .addRealmObjectField("repairPart", schema.get("RepairPart"))
                .addRealmObjectField("measureType", schema.get("MeasureType"))
                .addField("quantity", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("TaskRepairPart")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("task", schema.get("Tasks"))
                .addRealmObjectField("repairPart", schema.get("RepairPart"))
                .addRealmObjectField("measureType", schema.get("MeasureType"))
                .addField("quantity", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        //schema.get("Tasks").addField("comment", String.class);
        //schema.get("TaskStages").addField("comment", String.class);
        schema.get("Orders").addField("comment", String.class);
        schema.get("Operation").addField("comment", String.class);

    }

    private boolean testPropsFields(DynamicRealm realm) {
        boolean result = true;
        RealmSchema schema = realm.getSchema();
        // проверяем соответствие схемы базы со свойствами классов
        Set<RealmObjectSchema> realmObjects = schema.getAll();
        for (RealmObjectSchema realmObject : realmObjects) {
            Log.d(TAG, "Class name = " + realmObject.getClassName());
            Field[] classProps = null;
            Set<String> props = new HashSet<>();
            Map<String, String> propsType = new HashMap<>();
            try {
                Class<?> c = Class.forName("ru.toir.mobile.db.realm." + realmObject.getClassName());
                classProps = c.getDeclaredFields();
                for (Field prop : classProps) {
                    props.add(prop.getName());
                    propsType.put(prop.getName(), prop.getType().getName());
//                    propsType.put(prop.getName(), prop.getGenericType().toString());
                }
            } catch (Exception e) {
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
            for (String fieldName : fieldNames) {
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
