package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class Migration47 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 46");
        RealmSchema schema = realm.getSchema();
        schema.create("Organization")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.get("User").addRealmObjectField("organization", schema.get("Organization"));

    }
}
