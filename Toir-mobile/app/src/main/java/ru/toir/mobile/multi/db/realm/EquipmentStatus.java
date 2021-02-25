package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class EquipmentStatus extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
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
        public static final String ON_REPAIR = "B012E9F5-14A6-4100-8A16-15A51B4F7258";
        public static final String IN_STOCK = "26C9A653-41F4-4806-9490-EBA9545013E6";

        public static final String NOT_MOUNTED = "62A9AA68-9FE5-4D8C-A4B8-34278B95E51E";
        public static final String WORK = "61C5007F-AE18-4C4E-BD57-737A20EF9EBC";
        public static final String NEED_CHECK = "D818A97E-B6EB-4AEC-9168-174C780E365B";
        public static final String NEED_REPAIR = "7D0713CC-E79D-48D3-A2A2-60898A70BD8A";
        public static final String NOT_WORK = "7B9C5D15-4079-489F-AF73-5135C36B330A";
        public static final String UNKNOWN = "ED20012C-629A-4275-9BFA-A81D08B45758";
    }

}
