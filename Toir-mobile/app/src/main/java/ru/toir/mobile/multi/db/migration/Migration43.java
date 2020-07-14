package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class Migration43 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 42");
        RealmSchema schema = realm.getSchema();
        schema.create("Instruction")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addField("title", String.class)
                .addRealmObjectField("user", schema.get("User"))
                .addField("path", String.class)
                .addField("size", int.class)
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");

        schema.create("InstructionStageTemplate")
                .addField("_id", long.class)
                .addField("uuid", String.class)
                .addRealmObjectField("instruction", schema.get("Instruction"))
                .addRealmObjectField("stageTemplate", schema.get("StageTemplate"))
                .addField("createdAt", Date.class)
                .addField("changedAt", Date.class)
                .addPrimaryKey("_id");
    }
}
