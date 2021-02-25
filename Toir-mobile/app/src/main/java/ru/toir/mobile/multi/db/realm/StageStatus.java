package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 14.09.16.
 */
public class StageStatus extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String icon;
    private Date createdAt;
    private Date changedAt;

    private static StageStatus getStatusObject(Realm realm, String statusUuid) {
        return realm.where(StageStatus.class)
                .equalTo("uuid", statusUuid)
                .findFirst();
    }

    public static StageStatus getObjectNew(Realm realm) {
        return getStatusObject(realm, Status.NEW);
    }

    public static StageStatus getObjectInWork(Realm realm) {
        return getStatusObject(realm, Status.IN_WORK);
    }

    public static StageStatus getObjectComplete(Realm realm) {
        return getStatusObject(realm, Status.COMPLETE);
    }

    public static StageStatus getObjectUnComplete(Realm realm) {
        return getStatusObject(realm, Status.UN_COMPLETE);
    }

    public static StageStatus getObjectCanceled(Realm realm) {
        return getStatusObject(realm, Status.CANCELED);
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

    public boolean isNew() {
        return uuid.equals(Status.NEW);
    }

    public boolean isInWork() {
        return uuid.equals(Status.IN_WORK);
    }

    public boolean isComplete() {
        return uuid.equals(Status.COMPLETE);
    }

    public boolean isUnComplete() {
        return uuid.equals(Status.UN_COMPLETE);
    }

    public boolean isCanceled() {
        return uuid.equals(Status.CANCELED);
    }

    public class Status {
        public static final String NEW = "193BA468-AF86-4A8E-888D-65B99106B3AB";
        public static final String IN_WORK = "58F136B8-8532-44BC-8599-8293D21CF1C1";
        public static final String COMPLETE = "F5C3788B-6659-409F-913F-32555CE327C8";
        public static final String UN_COMPLETE = "BCFF9E0C-218D-42F0-9D82-5C296E8D6997";
        public static final String CANCELED = "37EF99C3-C1E8-43A7-8262-18719ED17402";
    }

}
