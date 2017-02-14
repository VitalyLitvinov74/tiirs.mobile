package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class TaskStatus extends RealmObject {
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
        public static final String NEW = "1E9B4D73-044C-471B-A08D-26F36EBB22BA";
        public static final String IN_WORK = "07EDBDD8-097C-4E76-A27B-5B567927426B";
        public static final String COMPLETE = "9F980DB5-934C-4DDB-999A-04C6C3DACA59";
        public static final String UN_COMPLETE = "DC6DCA37-2CC9-44DA-AFF9-19BF143E611A";
        public static final String CANCELED = "363C08EC-89D9-47DF-B7CF-63A05D56594C";
    }
}
