package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/20/18.
 */

public class TaskEquipmentStage extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private TaskTemplate taskTemplate;
    private RealmList<EquipmentStage> equipmentStages;
    private String period;
    private Organization organization;
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

    public TaskTemplate getTaskTemplate() {
        return taskTemplate;
    }

    public void setTaskTemplate(TaskTemplate taskTemplate) {
        this.taskTemplate = taskTemplate;
    }

    public RealmList<EquipmentStage> getEquipmentStages() {
        return equipmentStages;
    }

    public void setEquipmentStages(RealmList<EquipmentStage> equipmentStages) {
        this.equipmentStages = equipmentStages;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
