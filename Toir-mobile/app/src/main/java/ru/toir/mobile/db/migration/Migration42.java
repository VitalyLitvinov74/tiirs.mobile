package ru.toir.mobile.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class Migration42 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 41");
        RealmSchema schema = realm.getSchema();
        schema.create("OrderRepairPart")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("order", schema.get("Orders"))
                .addRealmObjectField("repairPart", schema.get("RepairPart"))
                .addField("quantity", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
    }
}
