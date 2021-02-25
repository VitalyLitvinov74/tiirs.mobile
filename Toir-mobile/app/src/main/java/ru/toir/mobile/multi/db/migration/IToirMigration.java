package ru.toir.mobile.multi.db.migration;

import io.realm.DynamicRealm;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 12/11/17.
 */

public interface IToirMigration {
    void migration(DynamicRealm realm);
}
