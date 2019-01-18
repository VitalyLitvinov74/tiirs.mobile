package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class OperationStatus extends RealmObject {

    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private Date createdAt;
    private Date changedAt;

    private static OperationStatus getStatusObject(Realm realm, String statusUuid) {
        return realm.where(OperationStatus.class)
                .equalTo("uuid", statusUuid)
                .findFirst();
    }

    public static OperationStatus getObjectNew(Realm realm) {
        return getStatusObject(realm, Status.NEW);
    }

    public static OperationStatus getObjectInWork(Realm realm) {
        return getStatusObject(realm, Status.IN_WORK);
    }

    public static OperationStatus getObjectComplete(Realm realm) {
        return getStatusObject(realm, Status.COMPLETE);
    }

    public static OperationStatus getObjectUnComplete(Realm realm) {
        return getStatusObject(realm, Status.UN_COMPLETE);
    }

    public static OperationStatus getObjectCanceled(Realm realm) {
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
        public static final String NEW = "18D3D5D4-336F-4B25-BA2B-00A6C7D5EB6C";
        public static final String IN_WORK = "78063CCA-4463-45AD-9124-88CEA2B51017";
        public static final String COMPLETE = "626FC9E9-9F1F-4DE7-937D-74DAD54ED751";
        public static final String UN_COMPLETE = "0F733A22-B65A-4D96-AF86-34F7E6A62B0B";
        public static final String CANCELED = "1A277EB1-1A22-400F-9E03-F094E19FEEDE";
    }
}
