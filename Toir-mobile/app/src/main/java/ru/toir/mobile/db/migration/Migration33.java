package ru.toir.mobile.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmList;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 4/13/18.
 */

public class Migration33 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 32");
        RealmSchema schema = realm.getSchema();


        schema.get("Task").addField("orderUuid", String.class);

        for (DynamicRealmObject order : realm.where("Orders").findAll()) {
            RealmList<DynamicRealmObject> tasks = order.get("tasks");
            for (DynamicRealmObject task : tasks) {
                task.setString("orderUuid", order.getString("uuid"));
            }
        }
    }
}
