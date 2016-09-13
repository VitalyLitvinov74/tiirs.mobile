package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 * Created on 07.09.16.
 */
public class EquipmentStatus extends RealmObject {
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
     *
     */
    public class Status {
        public static final String WORK = "75ad2a48-16d8-4592-ba38-8137b38ad669";
        public static final String DONT_WORK = "ff1ce1eb-e17e-4e8b-bed9-98e7e9d163af";
        public static final String UNMOUNTED = "ac9e9d23-980a-4bd1-9008-675946b1e794";
        public static final String ON_REPAIR = "b012e9f5-14a6-4100-8a16-15a51b4f7258";
        public static final String IN_STOCK = "26c9a653-41f4-4806-9490-eba9545013e6";
        public static final String UNKNOWN = "ed20012c-629a-4275-9bfa-a81d08b45758";
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
