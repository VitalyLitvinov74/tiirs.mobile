package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.toir.mobile.multi.AuthorizedUser;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class Equipment extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private EquipmentModel equipmentModel;
    private EquipmentStatus equipmentStatus;
    private Equipment parentEquipment;
    private String title;
    private String inventoryNumber;
    private String serialNumber;
    private Objects location;
    private CriticalType criticalType;
    private Date startDate;
    private float latitude;
    private float longitude;
    private String tagId;
    private String image;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;

    public static String getImageRoot() {
        return "equipment";
    }

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

    public Objects getLocation() {
        return location;
    }

    public void setLocation(Objects location) {
        this.location = location;
    }

    public CriticalType getCriticalType() {
        return criticalType;
    }

    public void setCriticalType(CriticalType criticalType) {
        this.criticalType = criticalType;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Equipment getParentEquipment() {
        return parentEquipment;
    }

    public void setParentEquipment(Equipment parentEquipment) {
        this.parentEquipment = parentEquipment;
    }

    @Override
    public String getImageFileName() {
        return image;
    }

    @Override
    public String getImageFilePath(String dbName) {
        String dir;
        dir = dbName + "/" + getImageRoot() + "/" + equipmentModel.getUuid();
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return "/storage/" + userName + "/" + getImageRoot() + "/" + equipmentModel.getUuid();
    }

    public String getAnyImageFilePath() {
        String dir = null;
        String dbName = AuthorizedUser.getInstance().getDbName();

        if (image != null && !image.equals("")) {
            dir = getImageFilePath(dbName);
        } else {
            String modelImage = equipmentModel.getImageFileName();
            if (modelImage != null && !modelImage.equals("")) {
                dir = equipmentModel.getImageFilePath(dbName);
            }
        }

        return dir;
    }

    public String getAnyImage() {
        String file = null;

        if (image != null && !image.equals("")) {
            file = image;
        } else {
            String modelImage = equipmentModel.getImageFileName();
            if (modelImage != null && !modelImage.equals("")) {
                file = equipmentModel.getImageFileName();
            }
        }

        return file;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
