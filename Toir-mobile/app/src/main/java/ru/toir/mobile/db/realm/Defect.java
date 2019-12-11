package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 2.05.17.
 */
public class Defect extends RealmObject implements ISend {
    @Index
    private long _id;
    @PrimaryKey
    private String uuid;
    private User user;
    private Date date;
    private Equipment equipment;
    private DefectType defectType;
    private DefectLevel defectLevel;
    private boolean process;
    private String comment;
    private Task task;
    private Date createdAt;
    private Date changedAt;
    private boolean sent;

    public static long getLastId() {
        Realm realm = Realm.getDefaultInstance();

        Number lastId = realm.where(Defect.class).max("_id");
        if (lastId == null) {
            lastId = 0;
        }

        realm.close();
        return lastId.longValue();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public DefectType getDefectType() {
        return defectType;
    }

    public void setDefectType(DefectType defectType) {
        this.defectType = defectType;
    }

    public DefectLevel getDefectLevel() {
        return defectLevel;
    }

    public void setDefectLevel(DefectLevel defectLevel) {
        this.defectLevel = defectLevel;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    public boolean isProcess() {
        return process;
    }

    public void setProcess(boolean process) {
        this.process = process;
    }

    public boolean isSent() {
        return sent;
    }

    @Override
    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
