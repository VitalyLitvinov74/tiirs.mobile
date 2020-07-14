package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 16.09.16.
 */
public class MeasureType extends RealmObject {
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

    public class Type {
        public static final String NONE = "E9ADE49A-3C31-42F8-A751-AAEB890C2190";
        public static final String FREQUENCY = "481C2E40-421E-41AB-8BC1-5FB0D01A4CC3";
        public static final String VOLTAGE = "1BEC4685-466F-4AA6-95FC-A3C01BAF09FE";
        public static final String PRESSURE = "69A71072-7EDD-4FF9-B095-0EF145286D79";
        public static final String PHOTO = "8EB1CC6A-FBD5-4A4E-91EE-CA762B94473C";
    }

}
