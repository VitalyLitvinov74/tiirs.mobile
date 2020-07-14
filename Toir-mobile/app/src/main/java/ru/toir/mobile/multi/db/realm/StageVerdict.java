package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 15.09.16.
 */
public class StageVerdict extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String icon;
    private Date createdAt;
    private Date changedAt;
    private StageType stageType;
    private Organization organization;

    private static StageVerdict getVerdictObject(Realm realm, String verdictUuid) {
        return realm.where(StageVerdict.class)
                .equalTo("uuid", verdictUuid)
                .findFirst();
    }

    public static StageVerdict getObjectLookGood(Realm realm) {
        return getVerdictObject(realm, Verdict.LOOK_GOOD);
    }

    public static StageVerdict getObjectComplete(Realm realm) {
        return getVerdictObject(realm, Verdict.COMPLETE);
    }

    public static StageVerdict getObjectUnComplete(Realm realm) {
        return getVerdictObject(realm, Verdict.UN_COMPLETE);
    }

    public static StageVerdict getObjectLookBad(Realm realm) {
        return getVerdictObject(realm, Verdict.LOOK_BAD);
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

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public class Verdict {
        public static final String LOOK_GOOD = "F1B5F0B2-E23B-42A8-BB5D-00AA26A9B1D5";
        public static final String LOOK_BAD = "C0E5E028-7AD2-45D7-96D1-CF2E89F6A18F";
        public static final String COMPLETE = "82D1360B-318B-4883-8AD8-DCC94E0D022F";
        public static final String UN_COMPLETE = "D5AA2C90-3C69-4B73-8197-AE8E7D5614E3";
    }
}
