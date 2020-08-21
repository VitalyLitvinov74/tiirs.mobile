package ru.toir.mobile.multi.db.realm;

import java.util.Date;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 11/29/17.
 */

public interface IToirDbObject {
    String getUuid();

    String getImageFileName();

    String getImageFilePath(String dbName);

    String getImageFileUrl(String userName);

    Date getCreatedAt();

    Date getChangedAt();
}
