package ru.toir.mobile.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17/03/19.
 */

public class Migration34 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 33");
        RealmSchema schema = realm.getSchema();

        // создаём таблицы
        schema.create("AttributeType")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("name", String.class)
                .addField("refresh", boolean.class)
                .addField("units", String.class)
                .addField("type", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("EquipmentAttribute")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("attributeType", schema.get("AttributeType"))
                .addRealmObjectField("equipment", schema.get("Equipment"))
                .addField("date", Date.class)
                .addField("value", String.class)
                .addField("sent", boolean.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("uuid")
                .addIndex("_id");
    }
}
