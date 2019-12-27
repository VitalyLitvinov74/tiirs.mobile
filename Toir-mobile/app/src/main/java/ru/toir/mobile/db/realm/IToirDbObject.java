package ru.toir.mobile.db.realm;

import java.util.Date;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 11/29/17.
 */

public interface IToirDbObject {
    String getUuid();

    String getImageFile();

    String getImageFilePath();

    String getImageFileUrl(String userName);

    Date getCreatedAt();

    Date getChangedAt();
}
