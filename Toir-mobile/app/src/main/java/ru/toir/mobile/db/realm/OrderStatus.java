package ru.toir.mobile.db.realm;

import java.util.Date;

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

    /**
     * Класс констант статуса наряда
     *
     * @author Dmitriy Logachov
     */
    public class Status {
        public static final String NEW = "90DDA367-52FB-4D2F-8CA1-6281D0776C3C";
        public static final String IN_WORK = "0F0C9C45-7D02-4206-823F-1202A7102598";
        public static final String COMPLETE = "53238221-0EF7-4737-975E-FD49AFC92A05";
        public static final String UN_COMPLETE = "3DF60C8B-4A61-43D9-B822-28504DD53C2F";
        public static final String CANCELED = "3368F365-4587-47B6-B66B-043959E27B8D";
    }

}
