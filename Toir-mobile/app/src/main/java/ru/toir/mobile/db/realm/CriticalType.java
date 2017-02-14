package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class CriticalType extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String icon;
    private Date createdAt;
    private Date changedAt;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public class Status {
        public static final String TYPE_1 = "1B5FD0C6-019D-41AE-8403-D2F9EEEADA6B";
        public static final String TYPE_2 = "B0E66ED9-2D44-4CEB-93D6-443EFCC51290";
        public static final String TYPE_3 = "40D90415-B29D-438A-97F2-978FB56D6ABB";
        public static final String TYPE_4 = "B5339CB7-FABC-4972-B13E-5115BB7E8DBA";
        public static final String TYPE_5 = "62A9AA68-9FE5-4D8C-A4B8-34278B95E51E";
    }
}
