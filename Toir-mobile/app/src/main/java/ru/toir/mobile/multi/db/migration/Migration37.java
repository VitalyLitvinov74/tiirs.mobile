package ru.toir.mobile.multi.db.migration;

import android.os.Environment;
import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmResults;
import io.realm.RealmSchema;

public class Migration37 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 36");
        RealmSchema schema = realm.getSchema();

        // создаём таблицы
        schema.create("CommonFile")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("path", String.class)
                .addField("name", String.class)
                .addField("description", String.class)
                .addField("require", boolean.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("MediaFile")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("entityUuid", String.class)
                .addField("path", String.class)
                .addField("name", String.class)
                .addField("sent", boolean.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("uuid")
                .addIndex("_id");

        RealmResults<DynamicRealmObject> opFiles = realm.where("OperationFile").findAll();
        for (DynamicRealmObject opFile : opFiles) {
            DynamicRealmObject mFile = realm.createObject("MediaFile", opFile.getString("uuid"));
            mFile.setLong("_id", opFile.getLong("_id"));
            mFile.setString("entityUuid", opFile.getObject("operation").getString("uuid"));
            mFile.setString("path", Environment.DIRECTORY_PICTURES);
            mFile.setString("name", opFile.getString("fileName"));
            mFile.setBoolean("sent", opFile.getBoolean("sent"));
            mFile.setDate("createdAt", opFile.getDate("createdAt"));
            mFile.setDate("changedAt", opFile.getDate("changedAt"));
        }
    }
}
