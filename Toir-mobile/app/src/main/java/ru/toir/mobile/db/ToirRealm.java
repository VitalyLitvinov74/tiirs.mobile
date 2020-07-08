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

    public static void initDb(Context context, String dbName) {
        if (!dbName.contains(".realm")) {
            dbName += ".realm";
        }

        // конфигурация базы
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(dbName)
                .schemaVersion(VERSION)
                .migration(new ToirRealmMigration(context))
                .build();
        // устанавливаем конфигурацию базы
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static Realm getDefaultInstance() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return realm;
    }
}
