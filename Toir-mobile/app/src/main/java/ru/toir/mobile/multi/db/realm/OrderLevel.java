package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 5.10.16.
 */
public class OrderLevel extends RealmObject {
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

    public class Level {
        public static final String Level1 = "DB392A36-A970-4BB3-96AB-FEF0F5FEBB95";
        public static final String Level2 = "CEE7D7C4-3050-40DD-8E2D-073D2A18FDB9";
        public static final String Level3 = "673CE002-26EC-4132-944D-9F29A596FCFD";
        public static final String Level4 = "7C18B8BE-D744-4692-84FC-CA3EF904EF0A";
        public static final String Level5 = "EE9D663F-72B5-434A-BEB7-F9B1637920FB";
    }
}
