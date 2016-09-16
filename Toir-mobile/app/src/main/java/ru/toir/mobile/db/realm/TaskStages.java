package ru.toir.mobile.db.realm;

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
    private int startDate;
    private int endDate;
    private int flowOrder;
    private long createdAt;
    private long changedAt;
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

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int closeDate) {
        this.endDate = endDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(long changedAt) {
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
