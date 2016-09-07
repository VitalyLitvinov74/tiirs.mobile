package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 * Created on 07.09.16.
 */
public class Equipment extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String equipmentModelUuid;
    private String equipmentStatusUuid;
    private String title;
    private String inventoryNumber;
    private String location;
    private String criticalTypeUuid;
    private String userUuid;
    private int startDate;
    private float latitude;
    private float longitude;
    private String tagId;
    private String image;
    private long createdAt;
    private long changedAt;

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

    public String getEquipmentStatusUuid() {
        return equipmentStatusUuid;
    }
    public void setEquipmentStatusUuid(String equipmentStatusUuid) {
        this.equipmentStatusUuid = equipmentStatusUuid;
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

    public String getUserUuid() {
        return userUuid;
    }
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public int getStartDate() {
        return startDate;
    }
    public void setStartDate(int startDate) {
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
}
