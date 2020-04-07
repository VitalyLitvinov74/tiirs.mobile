package ru.toir.mobile.db.migration;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration44 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 43");
        RealmSchema schema = realm.getSchema();
        schema.get("User")
                .addField("active2", int.class)
                .transform(new RealmObjectSchema.Function() {
                    @Override
                    public void apply(@NonNull DynamicRealmObject obj) {
                        obj.setInt("active2", obj.getBoolean("active") ? 1 : 0);
                    }
                })
                .removeField("active")
                .renameField("active2", "active");
    }
}
