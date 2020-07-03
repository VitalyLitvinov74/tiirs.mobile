package ru.toir.mobile.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Dmitriy Logachev
 */
public class ToirRealm {
    // версия схемы базы данных приложения
    private static final int VERSION = 47;

    public static boolean initDb(Context context, String dbName) {
        if (!dbName.contains(".realm")) {
            dbName += ".realm";
        }

        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(dbName)
                .schemaVersion(VERSION)
                .build();
        try {
            Realm.migrateRealm(realmConfig, new ToirRealmMigration(context));
            Realm.setDefaultConfiguration(realmConfig);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
