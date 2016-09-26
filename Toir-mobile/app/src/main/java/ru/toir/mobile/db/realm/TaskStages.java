package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 14.09.16.
 */
public class TaskStages extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String comment;
    private String taskUuid;
    private String equipmentUuid;
    private Equipment equipment;
    private String taskStageVerdictUuid;
    private TaskStageVerdict taskStageVerdict;
    private String taskStageStatusUuid;
    private TaskStageStatus taskStageStatus;
    private String taskStageTemplateUuid;
    private TaskStageTemplate taskStageTemplate;
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

    public String getEquipmentUuid() {
        return equipmentUuid;
    }

    public void setEquipmentUuid(String equipmentUuid) {
        this.equipmentUuid = equipmentUuid;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getTaskStageVerdictUuid() {
        return taskStageVerdictUuid;
    }

    public void setTaskStageVerdictUuid(String taskStageVerdictUuid) {
        this.taskStageVerdictUuid = taskStageVerdictUuid;
    }

    public TaskStageVerdict getTaskStageVerdict() {
        return taskStageVerdict;
    }

    public void setTaskStageVerdict(TaskStageVerdict taskStageVerdict) {
        this.taskStageVerdict = taskStageVerdict;
    }

    public String getTaskStageStatusUuid() {
        return taskStageStatusUuid;
    }

    public void setTaskStageStatusUuid(String taskStageStatusUuid) {
        this.taskStageStatusUuid = taskStageStatusUuid;
    }

    public TaskStageStatus getTaskStageStatus() {
        return taskStageStatus;
    }

    public void setTaskStageStatus(TaskStageStatus taskStageStatus) {
        this.taskStageStatus = taskStageStatus;
    }

    public String getTaskStageTemplateUuid() {
        return taskStageTemplateUuid;
    }

    public void setTaskStageTemplateUuid(String taskStageTemplateUuid) {
        this.taskStageTemplateUuid = taskStageTemplateUuid;
    }

    public TaskStageTemplate getTaskStageTemplate() {
        return taskStageTemplate;
    }

    public void setTaskStageTemplate(TaskStageTemplate taskStageTemplate) {
        this.taskStageTemplate = taskStageTemplate;
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
        this.endDate = endDate;
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
