package ru.toir.mobile.db.realm;

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
    private String taskStageUuid;
    private String operationVerdictUuid;
    private OperationVerdict operationVerdict;
    private String operationStatusUuid;
    private OperationStatus operationStatus;
    private String operationTemplateUuid;
    private OperationTemplate operationTemplate;
    private int startDate;
    private int endDate;
    private long createdAt;
    private long changedAt;
    private int flowOrder;

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

    public String getTaskStageUuid() {
        return taskStageUuid;
    }

    public void setTaskStageUuid(String taskStageUuid) {
        this.taskStageUuid = taskStageUuid;
    }

    public String getOperationVerdictUuid() {
        return operationVerdictUuid;
    }

    public void setOperationVerdictUuid(String operationVerdictUuid) {
        this.operationVerdictUuid = operationVerdictUuid;
    }

    public OperationVerdict getOperationVerdict() {
        return operationVerdict;
    }

    public void setOperationVerdict(OperationVerdict operationVerdict) {
        this.operationVerdict = operationVerdict;
    }

    public String getOperationStatusUuid() {
        return operationStatusUuid;
    }

    public void setOperationStatusUuid(String operationStatusUuid) {
        this.operationVerdictUuid = operationVerdictUuid;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getOperationTemplateUuid() {
        return operationTemplateUuid;
    }

    public void setOperationTemplateUuid(String operationTemplateUuid) {
        this.operationTemplateUuid = operationTemplateUuid;
    }

    public OperationTemplate getOperationTemplate() {
        return operationTemplate;
    }

    public void setOperationTemplate(OperationTemplate operationTemplate) {
        this.operationTemplate = operationTemplate;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public int getFlowOrder() {
        return flowOrder;
    }

    public void setFlowOrder(int flowOrder) {
        this.flowOrder = flowOrder;
    }
}
