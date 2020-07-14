package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Oleg
 *         Created by olejek on 13/12/17.
 */

public class Migration28 implements IToirMigration {

    @Override
    public void migration(DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 27");
        RealmSchema schema = realm.getSchema();
        schema.get("Equipment").addField("serialNumber", String.class);
        schema.get("Orders").addField("reason", String.class);
    }
}
