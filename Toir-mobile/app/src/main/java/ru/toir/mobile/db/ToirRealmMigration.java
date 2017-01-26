package ru.toir.mobile.db;

import android.util.Log;
import java.util.Date;
import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

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
                    .addField("photo", String.class)
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
                    .addField("longitude", Double.class)
                    .addField("latitude", Double.class)
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
                    .addField("commonRepairPartFlag",Integer.class);

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

    }
}
