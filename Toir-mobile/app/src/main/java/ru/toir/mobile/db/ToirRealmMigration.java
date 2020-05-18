package ru.toir.mobile.db;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import dalvik.system.DexFile;
import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.exceptions.RealmException;
import ru.toir.mobile.db.migration.Migration26;
import ru.toir.mobile.db.migration.Migration27;
import ru.toir.mobile.db.migration.Migration28;
import ru.toir.mobile.db.migration.Migration29;
import ru.toir.mobile.db.migration.Migration30;
import ru.toir.mobile.db.migration.Migration31;
import ru.toir.mobile.db.migration.Migration32;
import ru.toir.mobile.db.migration.Migration33;
import ru.toir.mobile.db.migration.Migration34;
import ru.toir.mobile.db.migration.Migration35;
import ru.toir.mobile.db.migration.Migration36;
import ru.toir.mobile.db.migration.Migration37;
import ru.toir.mobile.db.migration.Migration38;
import ru.toir.mobile.db.migration.Migration39;
import ru.toir.mobile.db.migration.Migration40;
import ru.toir.mobile.db.migration.Migration41;
import ru.toir.mobile.db.migration.Migration42;
import ru.toir.mobile.db.migration.Migration43;
import ru.toir.mobile.db.migration.Migration44;
import ru.toir.mobile.db.migration.Migration45;

/**
 * @author Dmitriy Logachev
 */
class ToirRealmMigration implements RealmMigration {
    private final String TAG = this.getClass().getName();
    private Context context;

    ToirRealmMigration(Context context) {
        this.context = context;
    }

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

        if (oldVersion == 19) {
            toVersion20(realm);
            oldVersion++;
        }

        if (oldVersion == 20) {
            toVersion21(realm);
            oldVersion++;
        }

        if (oldVersion == 21) {
            toVersion22(realm);
            oldVersion++;
        }

        if (oldVersion == 22) {
            toVersion23(realm);
            oldVersion++;
        }

        if (oldVersion == 23) {
            toVersion24(realm);
            oldVersion++;
        }

        if (oldVersion == 24) {
            toVersion25(realm);
            oldVersion++;
        }

        if (oldVersion == 25) {
            new Migration26().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 26) {
            new Migration27().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 27) {
            new Migration28().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 28) {
            new Migration29().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 29) {
            new Migration30().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 30) {
            new Migration31().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 31) {
            new Migration32().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 32) {
            new Migration33().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 33) {
            new Migration34().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 34) {
            new Migration35().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 35) {
            new Migration36().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 36) {
            new Migration37().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 37) {
            new Migration38().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 38) {
            new Migration39().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 39) {
            new Migration40().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 40) {
            new Migration41().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 41) {
            new Migration42().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 42) {
            new Migration43().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 43) {
            new Migration44().migration(realm);
            oldVersion++;
        }

        if (oldVersion == 44) {
            new Migration45().migration(realm);
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
    @SuppressWarnings("unused")
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
                .addRealmObjectField("defectLevel", schema.get("DefectLevel"))
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

    /**
     * Переход на версию 20
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion20(DynamicRealm realm) {
        Log.d(TAG, "from version 19");
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema objSchema = schema.get("Defect");
        objSchema.addRealmObjectField("user", schema.get("User"));
        objSchema.removeField("contragent");

        objSchema = schema.get("Contragent");
        objSchema.addField("photo", String.class);
        objSchema.addField("longitude", double.class);
        objSchema.addField("latitude", double.class);
    }

    /**
     * Переход на версию 21
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion21(DynamicRealm realm) {
        Log.d(TAG, "from version 20");
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema objSchema = schema.get("Contragent");
        objSchema.addField("address", String.class);
    }

    /**
     * Переход на версию 22
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion22(DynamicRealm realm) {
        Log.d(TAG, "from version 21");
        RealmSchema schema = realm.getSchema();
        schema.create("OperationPhoto")
                .addField("_id", long.class)
                .addIndex("_id")
                .addField("uuid", String.class)
                .addPrimaryKey("uuid")
                .addRealmObjectField("operation", schema.get("Operation"))
                .addField("fileName", String.class)
                .addField("sent", boolean.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class);
    }

    /**
     * Переход на версию 23
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion23(DynamicRealm realm) {
        Log.d(TAG, "from version 22");
        RealmSchema schema = realm.getSchema();
        schema.rename("OperationPhoto", "OperationFile");
    }

    /**
     * Переход на версию 24
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion24(DynamicRealm realm) {
        Log.d(TAG, "from version 23");
        RealmSchema schema = realm.getSchema();
        schema.get("Orders").renameField("receiveDate", "receivDate");
    }

    /**
     * Переход на версию 25
     *
     * @param realm - экземпляр realmDB
     */
    private void toVersion25(DynamicRealm realm) {
        Log.d(TAG, "from version 24");
        RealmSchema schema = realm.getSchema();
        schema.get("MeasuredValue").addField("sent", boolean.class);
    }

    private boolean testPropsFields(DynamicRealm realm) {
        RealmSchema schema = realm.getSchema();

        // проверяем соответствие схемы базы со свойствами классов
        Set<RealmObjectSchema> realmObjects = schema.getAll();
        Set<String> tableList = new LinkedHashSet<>();
        for (RealmObjectSchema realmObject : realmObjects) {
            String tableName = realmObject.getClassName();
            Log.d(TAG, "Class name = " + tableName);
            tableList.add(tableName);
            Field[] classProps;
            Set<String> props = new HashSet<>();
            Map<String, String> propsType = new HashMap<>();
            try {
                Class<?> c = Class.forName("ru.toir.mobile.db.realm." + tableName);
                classProps = c.getDeclaredFields();
                for (Field prop : classProps) {
                    props.add(prop.getName());
                    propsType.put(prop.getName(), prop.getType().getName());
//                    propsType.put(prop.getName(), prop.getGenericType().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // проверяем количество и названия полей и свойств
            Set<String> fieldNames = realmObject.getFieldNames();
            Set<String> backProps = new HashSet<>(props);
            props.removeAll(fieldNames);
            fieldNames.removeAll(backProps);
            if (props.size() == 0 && fieldNames.size() == 0) {
                Log.d(TAG, "Список полей идентичен.");
            } else {
                StringBuilder b = new StringBuilder();
                if (props.size() > 0) {
                    for (String item : props) {
                        b.append(item).append(", ");
                    }

                    Log.e(TAG, "Список свойств класса без соответствующих полей в таблице: " + b.toString());
                }

                if (fieldNames.size() > 0) {
                    b.setLength(0);
                    for (String item : fieldNames) {
                        b.append(item).append(", ");
                    }

                    Log.e(TAG, "Список полей таблицы без соответствующих свойств класса: " + b.toString());
                }

                return false;
            }

            // сравниваем типы свойств и полей
            for (String fieldName : fieldNames) {
                String realmType = realmObject.getFieldType(fieldName).name();
                String propType = propsType.get(fieldName);
                if (!realmType.equals(getType(propType))) {
                    Log.e(TAG, "Type not same (fName = " + fieldName + "): fType = " + realmType + ", pType = " + propType);
                    return false;
                }
            }
        }

        // TODO: реализовать загрузку classes.dex и поиск в нём <Lru.toir.mobile.db.realm.*;>
        // получаем список классов объектов которые выступают в роли таблиц
        Set<String> classList = new HashSet<>();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> iter = df.entries();
            while (iter.hasMoreElements()) {
                String classPath = iter.nextElement();
                if (classPath.contains("ru.toir.mobile.db.realm") && !classPath.contains("$")) {
                    try {
                        Class<?> driverClass = Class.forName(classPath);
                        if (!driverClass.isInterface()) {

                            Constructor<?> constructor = driverClass.getConstructor();
                            RealmObject o = (RealmObject) constructor.newInstance();
                            o.getClass().getMethod("deleteFromRealm");
                            classList.add(classPath.substring(classPath.lastIndexOf('.') + 1));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // проверяем соответствие полученых списков классов и таблиц
        Set<String> backTableList = new HashSet<>(tableList);
        tableList.removeAll(classList);
        classList.removeAll(backTableList);
        if (tableList.size() == 0 && classList.size() == 0) {
            Log.d(TAG, "Список классов соответствует списку таблиц.");
        } else {
            StringBuilder b = new StringBuilder();
            if (tableList.size() > 0) {
                for (String item : tableList) {
                    b.append(item).append(", ");
                }

                Log.e(TAG, "Список таблиц без соответствующих классов: " + b.toString());
            }

            if (classList.size() > 0) {
                b.setLength(0);
                for (String item : classList) {
                    b.append(item).append(", ");
                }

                Log.e(TAG, "Список классов без соответствующих таблиц: " + b.toString());
            }

            return false;
        }

        return true;
    }

    private String getType(String type) {
        String result = type.substring(type.lastIndexOf('.') + 1).toUpperCase();

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
