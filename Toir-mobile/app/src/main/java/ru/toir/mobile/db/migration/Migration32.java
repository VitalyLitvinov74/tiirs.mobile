package ru.toir.mobile.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmList;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/20/18.
 */

public class Migration32 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 31");
        RealmSchema schema = realm.getSchema();

        schema.get("Operation").addField("stageUuid", String.class);

        for (DynamicRealmObject stage : realm.where("Stage").findAll()) {
            RealmList<DynamicRealmObject> operations = stage.get("operations");
            for (DynamicRealmObject operation : operations) {
                operation.setString("stageUuid", stage.getString("uuid"));
            }
        }
    }
}
