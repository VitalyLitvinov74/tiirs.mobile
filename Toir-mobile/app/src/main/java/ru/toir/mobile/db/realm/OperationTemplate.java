package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 13.09.16.
 */
public class OperationTemplate extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String description;
    private String image;
    private int normative;
    private int first_step;
    private int last_step;
    private String equipmentModelUuid;
    private EquipmentModel equipmentModel;
    private String operationTypeUuid;
    private OperationType operationType;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNormative() {
        return normative;
    }

    public void setNormative(int normative) {
        this.normative = normative;
    }

    public int getFirst_step() {
        return first_step;
    }

    public void setFirst_step(int first_step) {
        this.first_step = first_step;
    }

    public int getLast_step() {
        return last_step;
    }

    public void setLast_step(int last_step) {
        this.last_step = last_step;
    }

    public String getEquipmentModelUuid() {
        return equipmentModelUuid;
    }

    public void setEquipmentModelUuid(String equipmentModelUuid) {
        this.equipmentModelUuid = equipmentModelUuid;
    }

    public EquipmentModel getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public String getOperationTypeUuid() {
        return operationTypeUuid;
    }

    public void setOperationTypeUuid(String operationTypeUuid) {
        this.operationTypeUuid = operationTypeUuid;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
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
