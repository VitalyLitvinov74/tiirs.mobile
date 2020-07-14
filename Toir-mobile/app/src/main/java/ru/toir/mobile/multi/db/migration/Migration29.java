package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/16/18.
 */

public class Migration29 implements IToirMigration {
    @Override
    public void migration(DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 28");
        RealmSchema schema = realm.getSchema();

        // удаляем более не нужные поля
        schema.get("ToolType").removeField("parentUuid");
        schema.get("TaskTemplate").removeField("equipmentModel");
        schema.get("RepairPartType").removeField("parentRepairType");
        schema.get("RepairPart").removeField("equipmentModel");
        schema.get("RepairPart").removeField("commonRepairPartFlag");
        schema.get("StageTemplate").removeField("equipmentModel");
        schema.get("StageType").removeField("icon");
        schema.get("OperationType").removeField("icon");
        schema.get("EquipmentType").removeField("icon");
        schema.get("OperationTemplate").removeField("first_step");
        schema.get("OperationTemplate").removeField("last_step");
        schema.get("OperationTemplate").removeField("equipmentModel");

        // удаляем более не нужные таблицы
        schema.remove("TaskRepairPart");
        schema.remove("TaskTemplateRepairPart");
        schema.remove("TaskStageOperationList");
        schema.remove("TaskStageList");

        // переименовываем таблицы
        schema.rename("Stages", "Stage");

        // переименовываем поля
        schema.get("Stage").renameField("taskStageVerdict", "stageVerdict");
        schema.get("Stage").renameField("taskStageStatus", "stageStatus");
        schema.get("Stage").renameField("taskStageTemplate", "stageTemplate");
        schema.get("StageTemplate").renameField("taskStageType", "stageType");

        // добавляем недостающие поля
        schema.get("StageVerdict").addRealmObjectField("stageType", schema.get("StageType"));
    }
}
