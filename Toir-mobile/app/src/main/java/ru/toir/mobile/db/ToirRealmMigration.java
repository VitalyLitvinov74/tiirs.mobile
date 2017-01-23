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

    }
}
