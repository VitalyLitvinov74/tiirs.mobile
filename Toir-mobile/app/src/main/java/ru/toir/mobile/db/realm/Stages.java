package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 14.09.16.
 */
public class Stages extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String comment;
    private String taskUuid;
    private Equipment equipment;
    private StageVerdict taskStageVerdict;
    private StageStatus taskStageStatus;
    private StageTemplate taskStageTemplate;
    private Date startDate;
    private Date endDate;
    private int flowOrder;
    private Date createdAt;
    private Date changedAt;
    private RealmList<Operation> operations;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public StageVerdict getStageVerdict() {
        return taskStageVerdict;
    }

    public void setStageVerdict(StageVerdict stageVerdict) {
        this.taskStageVerdict = stageVerdict;
    }

    public StageStatus getStageStatus() {
        return taskStageStatus;
    }

    public void setStageStatus(StageStatus stageStatus) {
        this.taskStageStatus = stageStatus;
    }

    public StageTemplate getStageTemplate() {
        return taskStageTemplate;
    }

    public void setStageTemplate(StageTemplate stageTemplate) {
        this.taskStageTemplate = stageTemplate;
    }

    public int getFlowOrder() {
        return flowOrder;
    }

    public void setFlowOrder(int flowOrder) {
        this.flowOrder = flowOrder;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date closeDate) {
        this.endDate = closeDate;
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

    public RealmList<Operation> getOperations() {
        return operations;
    }

    public void setOperations(RealmList<Operation> operations) {
        this.operations = operations;
    }

    public void addOperations(Operation operation) {
        this.operations.add(operation);
    }
}
