package ru.toir.mobile.db.migration;

import android.util.Log;
import java.util.Date;
import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/20/18.
 */

public class Migration30 implements IToirMigration {
    @Override
    public void migration(DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 29");
        RealmSchema schema = realm.getSchema();

        // переименовываем таблицы
        schema.rename("Tasks", "Task");

        // создаём таблицы
        schema.create("StageOperation")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("stageTemplate", schema.get("StageTemplate"))
                .addRealmObjectField("operationTemplate", schema.get("OperationTemplate"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("EquipmentStage")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("equipment", schema.get("Equipment"))
                .addRealmListField("stageOperations", schema.get("StageOperation"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("TaskEquipmentStage")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("taskTemplate", schema.get("TaskTemplate"))
                .addRealmListField("equipmentStages", schema.get("EquipmentStage"))
                .addField("period", String.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("OperationRepairPart")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("operationTemplate", schema.get("OperationTemplate"))
                .addRealmObjectField("repairPart", schema.get("RepairPart"))
                .addField("quantity", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        // удаляем более не нужные поля
        schema.get("EquipmentStatus").removeField("icon");
        schema.get("OperationStatus").removeField("icon");

        // переименовываем поля
        schema.get("Objects").renameField("parentObject", "parent");
        schema.get("Task").renameField("taskStages", "stages");
    }
}
