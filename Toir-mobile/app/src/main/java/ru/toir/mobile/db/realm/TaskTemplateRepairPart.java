package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 2.05.17.
 */
public class TaskTemplateRepairPart extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private TaskTemplate taskTemplate;
    private RepairPart repairPart;
    private MeasureType measureType;
    private int quantity;
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

    public RepairPart getRepairPart() {
        return repairPart;
    }

    public void setRepairPart (RepairPart repairPart) {
        this.repairPart = repairPart;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType (MeasureType measureType) {
        this.measureType = measureType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

}
