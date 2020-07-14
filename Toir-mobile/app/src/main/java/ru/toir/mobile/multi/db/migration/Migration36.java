package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration36 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 35");
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema obj = schema.get("Defect");
        obj.removePrimaryKey();
        obj.addPrimaryKey("uuid");
        obj.addIndex("_id");
    }
}
