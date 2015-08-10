
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Equipment {

    @Expose
    private String Name;
    @Expose
    private ru.toir.mobile.serverapi.EquipmentType EquipmentType;
    @Expose
    private ru.toir.mobile.serverapi.GeoCoordinates GeoCoordinates;
    @Expose
    private ru.toir.mobile.serverapi.CriticalityType CriticalityType;
    @Expose
    private List<Document> Documents = new ArrayList<Document>();
    @Expose
    private String Id;
    @Expose
    private String Tag;
    @Expose
    private String StartupDate;
    @Expose
    private String CreatedAt;
    @Expose
    private String ChangedAt;

    /**
     * 
     * @return
     *     The Name
     */
    public String getName() {
        return Name;
    }

    /**
     * 
     * @param Name
     *     The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * 
     * @return
     *     The EquipmentType
     */
    public ru.toir.mobile.serverapi.EquipmentType getEquipmentType() {
        return EquipmentType;
    }

    /**
     * 
     * @param EquipmentType
     *     The EquipmentType
     */
    public void setEquipmentType(ru.toir.mobile.serverapi.EquipmentType EquipmentType) {
        this.EquipmentType = EquipmentType;
    }

    /**
     * 
     * @return
     *     The GeoCoordinates
     */
    public ru.toir.mobile.serverapi.GeoCoordinates getGeoCoordinates() {
        return GeoCoordinates;
    }

    /**
     * 
     * @param GeoCoordinates
     *     The GeoCoordinates
     */
    public void setGeoCoordinates(ru.toir.mobile.serverapi.GeoCoordinates GeoCoordinates) {
        this.GeoCoordinates = GeoCoordinates;
    }

    /**
     * 
     * @return
     *     The CriticalityType
     */
    public ru.toir.mobile.serverapi.CriticalityType getCriticalityType() {
        return CriticalityType;
    }

    /**
     * 
     * @param CriticalityType
     *     The CriticalityType
     */
    public void setCriticalityType(ru.toir.mobile.serverapi.CriticalityType CriticalityType) {
        this.CriticalityType = CriticalityType;
    }

    /**
     * 
     * @return
     *     The Documents
     */
    public List<Document> getDocuments() {
        return Documents;
    }

    /**
     * 
     * @param Documents
     *     The Documents
     */
    public void setDocuments(List<Document> Documents) {
        this.Documents = Documents;
    }

    /**
     * 
     * @return
     *     The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * 
     * @return
     *     The CreatedAt
     */
    public String getCreatedAt() {
        return CreatedAt;
    }

    /**
     * 
     * @param CreatedAt
     *     The CreatedAt
     */
    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    /**
     * 
     * @return
     *     The ChangedAt
     */
    public String getChangedAt() {
        return ChangedAt;
    }

    /**
     * 
     * @param ChangedAt
     *     The ChangedAt
     */
    public void setChangedAt(String ChangedAt) {
        this.ChangedAt = ChangedAt;
    }

	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		Tag = tag;
	}

	/**
	 * @return the startupDate
	 */
	public String getStartupDate() {
		return StartupDate;
	}

	/**
	 * @param startupDate the startupDate to set
	 */
	public void setStartupDate(String startupDate) {
		StartupDate = startupDate;
	}

}
