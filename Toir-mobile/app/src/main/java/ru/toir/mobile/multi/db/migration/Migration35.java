package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 21/03/19.
 */

public class Migration35 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 34");
        RealmSchema schema = realm.getSchema();

        schema.get("Defect").addField("sent", boolean.class);
    }
}
