package ru.toir.mobile.multi.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/20/18.
 */

public class Migration31 implements IToirMigration {
    @Override
    public void migration(DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 30");
        RealmSchema schema = realm.getSchema();

        schema.get("Task").removeField("equipment");

        schema.get("OperationTemplate")
                .addRealmListField("operationTools", schema.get("OperationTool"));

        schema.get("OperationTemplate")
                .addRealmListField("operationRepairParts", schema.get("OperationRepairPart"));

        schema.get("OperationTool")
                .removeField("operationTemplate")
                .addField("operationTemplateUuid", String.class);

        schema.get("OperationRepairPart")
                .removeField("operationTemplate")
                .addField("operationTemplateUuid", String.class);
    }
}
