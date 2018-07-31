package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 13.09.16.
 */
public class Operation extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String stageUuid;
    private OperationVerdict operationVerdict;
    private OperationStatus operationStatus;
    private OperationTemplate operationTemplate;
    private Date startDate;
    private Date endDate;
    private int flowOrder;
    private String comment;
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

    public OperationVerdict getOperationVerdict() {
        return operationVerdict;
    }

    public void setOperationVerdict(OperationVerdict operationVerdict) {
        this.operationVerdict = operationVerdict;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public OperationTemplate getOperationTemplate() {
        return operationTemplate;
    }

    public void setOperationTemplate(OperationTemplate operationTemplate) {
        this.operationTemplate = operationTemplate;
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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getFlowOrder() {
        return flowOrder;
    }

    public void setFlowOrder(int flowOrder) {
        this.flowOrder = flowOrder;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStageUuid() {
        return stageUuid;
    }

    public void setStageUuid(String stageUuid) {
        this.stageUuid = stageUuid;
    }

    public OperationStatus getStatus() {
        return operationStatus;
    }

    public boolean isNew() {
        return getStatus().getUuid().equals(OperationStatus.Status.NEW);
    }

    public boolean isInWork() {
        return getStatus().getUuid().equals(OperationStatus.Status.IN_WORK);
    }

    public boolean isComplete() {
        return getStatus().getUuid().equals(OperationStatus.Status.COMPLETE);
    }

    public boolean isUnComplete() {
        return getStatus().getUuid().equals(OperationStatus.Status.UN_COMPLETE);
    }

    public boolean isCanceled() {
        return getStatus().getUuid().equals(OperationStatus.Status.CANCELED);
    }
}
