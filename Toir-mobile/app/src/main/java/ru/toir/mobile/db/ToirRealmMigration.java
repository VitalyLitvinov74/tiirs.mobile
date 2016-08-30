package ru.toir.mobile.db;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *
 */
public class ToirRealmMigration implements RealmMigration {
    private final String TAG = this.getClass().getName();

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d(TAG, "oldVersion = " + oldVersion);
        Log.d(TAG, "newVersion = " + newVersion);

        if (oldVersion == newVersion) {
            return;
        }

//        if (oldVersion == 0) {
//            Log.d(TAG, "from version 0");
//            schema.get("Person").addField("LastName", String.class);
//            oldVersion++;
//        }

    }
}
