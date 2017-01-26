package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 16.01.17.
 */
public class RepairPart extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private EquipmentModel equipmentModel;
    private RepairPartType repairPartType;
    private int commonRepairPartFlag;
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

    public EquipmentModel getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public RepairPartType getRepairPartType() {
        return repairPartType;
    }

    public void setRepairPartType(RepairPartType repairPartType) {
        this.repairPartType = repairPartType;
    }

    public int getCommonRepairPartFlag() {
        return commonRepairPartFlag;
    }

    public void setCommonRepairPartFlag(int commonRepairPartFlag) {
        this.commonRepairPartFlag = commonRepairPartFlag;
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
