package ru.toir.mobile.db;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Dmitriy Logachev
 *
 */
public class ToirRealm {
    // версия схемы базы данных приложения
    public static final int VERSION = 11;

    public static void init(Context context) {
        init(context, "toir.realm");
    }

    public static void init(Context context, String dbName) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .name(dbName)
                .schemaVersion(VERSION)
                .build();
        try {
            Realm.migrateRealm(realmConfig, new ToirRealmMigration());
        } catch (Exception e) {
            // ни чего не делаем
        }

        Realm.setDefaultConfiguration(realmConfig);

        // инициализируем интерфейс для отладки через Google Chrome
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                        .build());
    }

    public static boolean isDBActual() {
        return Realm.getDefaultInstance().getVersion() == Realm.getDefaultInstance().getVersion();
    }
}
