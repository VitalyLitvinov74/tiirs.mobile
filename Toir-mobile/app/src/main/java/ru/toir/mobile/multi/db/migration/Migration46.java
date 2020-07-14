package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class Migration46 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 45");
        RealmSchema schema = realm.getSchema();
        schema.get("Stage").addRealmListField("tools", schema.get("Tool"));
    }
}
