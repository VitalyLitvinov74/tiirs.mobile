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
    public static void init(Context context) {
        init(context, "toir");
    }

    public static void init(Context context, String dbName) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .name(dbName)
                .schemaVersion(0)
                .build();
        try {
            Realm.migrateRealm(realmConfig, new ToirRealmMigration());
        } catch (Exception e) {
            // ни чего не делаем
        }

        Realm.setDefaultConfiguration(realmConfig);

        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
                        .build());
    }
}
