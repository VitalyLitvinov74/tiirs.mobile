package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class Equipment extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String equipmentModelUuid;
    private EquipmentModel equipmentModel;
    private String equipmentStatusUuid;
    private EquipmentStatus equipmentStatus;
    private String title;
    private String inventoryNumber;
    private String location;
    private String criticalTypeUuid;
    private CriticalType criticalType;
    private String userUuid;
    private Date startDate;
    private float latitude;
    private float longitude;
    private String tagId;
    private String image;
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

    public String getEquipmentStatusUuid() {
        return equipmentStatusUuid;
    }

    public void setEquipmentStatusUuid(String equipmentStatusUuid) {
        this.equipmentStatusUuid = equipmentStatusUuid;
    }

    public EquipmentStatus getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(EquipmentStatus equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCriticalTypeUuid() {
        return criticalTypeUuid;
    }

    public void setCriticalTypeUuid(String criticalTypeUuid) {
        this.criticalTypeUuid = criticalTypeUuid;
    }

    public CriticalType getCriticalType() {
        return criticalType;
    }

    public void setCriticalType(CriticalType criticalType) {
        this.criticalType = criticalType;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
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
