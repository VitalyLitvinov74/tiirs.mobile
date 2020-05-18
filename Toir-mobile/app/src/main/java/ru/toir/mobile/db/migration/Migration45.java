package ru.toir.mobile.db.migration;

import android.support.annotation.NonNull;
import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration45 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 44");
        RealmSchema schema = realm.getSchema();
        schema.get("RepairPart").addField("code", String.class);
    }
}
