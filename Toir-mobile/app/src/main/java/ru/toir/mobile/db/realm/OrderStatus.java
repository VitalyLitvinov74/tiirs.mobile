package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class OrderStatus extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String icon;
    private long createdAt;
    private long changedAt;

    /**
     * Класс констант статуса наряда
     *
     * @author Dmitriy Logachov
     */
    public class Status {
        public static final String NEW = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";
        public static final String IN_WORK = "9f980db5-934c-4ddb-999a-04c6c3daca59";
        public static final String COMPLETE = "dc6dca37-2cc9-44da-aff9-19bf143e611a";
        public static final String UNCOMPLETE = "363c08ec-89d9-47df-b7cf-63a05d56594c";
    }

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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(long changedAt) {
        this.changedAt = changedAt;
    }

}
