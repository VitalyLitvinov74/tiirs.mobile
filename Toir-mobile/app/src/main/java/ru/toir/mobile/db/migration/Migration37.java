package ru.toir.mobile.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Olejek
 * Created by olejek on 26/09/19.
 */

public class Migration37 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 36");
        RealmSchema schema = realm.getSchema();
        schema.create("Message")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("fromUser", schema.get("User"))
                .addRealmObjectField("toUser", schema.get("User"))
                .addField("date", Date.class)
                .addField("text", String.class)
                .addField("status", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("uuid")
                .addIndex("_id");
    }
}
