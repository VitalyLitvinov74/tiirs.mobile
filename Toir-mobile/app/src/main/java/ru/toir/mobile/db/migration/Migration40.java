package ru.toir.mobile.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class Migration40 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 39");
        RealmSchema schema = realm.getSchema();
        schema.create("DefectLevel")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
        schema.get("Defect").addRealmObjectField("defectLevel", schema.get("DefectLevel"));
    }
}
