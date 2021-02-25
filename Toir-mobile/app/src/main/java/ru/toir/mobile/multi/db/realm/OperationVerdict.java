package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class OperationVerdict extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String icon;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;

    private static OperationVerdict getVerdictObject(Realm realm, String verdictUuid) {
        return realm.where(OperationVerdict.class)
                .equalTo("uuid", verdictUuid)
                .findFirst();
    }

    public static OperationVerdict getObjectNotDefined(Realm realm) {
        return getVerdictObject(realm, Verdict.NOT_DEFINED);
    }

    public static OperationVerdict getObjectComplete(Realm realm) {
        return getVerdictObject(realm, Verdict.COMPLETE);
    }

    public static OperationVerdict getObjectUnComplete(Realm realm) {
        return getVerdictObject(realm, Verdict.UN_COMPLETE);
    }

    public static OperationVerdict getObjectCanceled(Realm realm) {
        return getVerdictObject(realm, Verdict.CANCELED);
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

    public boolean isNotDefined() {
        return uuid.equals(Verdict.NOT_DEFINED);
    }

    public boolean isComplete() {
        return uuid.equals(Verdict.COMPLETE);
    }

    public boolean isUnComplete() {
        return uuid.equals(Verdict.UN_COMPLETE);
    }

    public boolean isCanceled() {
        return uuid.equals(Verdict.CANCELED);
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public class Verdict {
        public static final String NOT_DEFINED = "5205B8B3-E32B-46D0-9B67-1C47A346168F";
        public static final String COMPLETE = "4B72A9A1-01AA-45E5-BA8A-C4C2F586E8FD";
        public static final String UN_COMPLETE = "17BF9E6F-F9AF-4FA8-8814-C9ED00378D48";
        public static final String CANCELED = "0102D95B-F8CF-4779-8021-0327EC66ED16";
    }

}
