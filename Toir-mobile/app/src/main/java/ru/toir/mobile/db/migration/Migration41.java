package ru.toir.mobile.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class Migration41 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 40");
        RealmSchema schema = realm.getSchema();
        schema.get("Stage").renameField("flowOrder", "type");
    }
}
