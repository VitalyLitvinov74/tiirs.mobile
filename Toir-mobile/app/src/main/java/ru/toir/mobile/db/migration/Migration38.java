package ru.toir.mobile.db.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * @author Olejek
 * Created by olejek on 26/09/19.
 */

public class Migration38 implements IToirMigration {
    @Override
    public void migration(final DynamicRealm realm) {
        Log.d(this.getClass().getSimpleName(), "from version 37");
    }
}
