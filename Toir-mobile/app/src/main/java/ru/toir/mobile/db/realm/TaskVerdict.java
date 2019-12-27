package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 15.09.16.
 */
public class TaskVerdict extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private TaskType taskType;
    private String icon;
    private Date createdAt;
    private Date changedAt;

    private static TaskVerdict getVerdictObject(Realm realm, String verdictUuid) {
        return realm.where(TaskVerdict.class)
                .equalTo("uuid", verdictUuid)
                .findFirst();
    }

    public static TaskVerdict getObjectNotDefined(Realm realm) {
        return getVerdictObject(realm, Verdict.NOT_DEFINED);
    }

    public static TaskVerdict getObjectComplete(Realm realm) {
        return getVerdictObject(realm, Verdict.COMPLETE);
    }

    public static TaskVerdict getObjectUnComplete(Realm realm) {
        return getVerdictObject(realm, Verdict.UN_COMPLETE);
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

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
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

    public class Verdict {
        public static final String NOT_DEFINED = "0916D468-A631-4FC9-898C-04B7C9415284";
        public static final String COMPLETE = "91556B39-88FD-416B-9176-DC996E6EC234";
        public static final String UN_COMPLETE = "6286085F-260F-4BF2-9CB1-81CB590B0D4E";
    }
}
