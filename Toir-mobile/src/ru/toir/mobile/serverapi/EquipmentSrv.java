
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Оборудование
 * @author Dmitriy Logachov
 *
 */
public class EquipmentSrv {

    @Expose
    private String Name;
    @Expose
    private EquipmentTypeSrv EquipmentType;
    @Expose
    private GeoCoordinatesSrv GeoCoordinates;
    @Expose
    private CriticalTypeSrv CriticalityType;
    @Expose
    private List<EquipmentDocumentationSrv> Documents = new ArrayList<EquipmentDocumentationSrv>();
    @Expose
    private String Id;
    @Expose
    private String Tag;
    @Expose
    private Date StartupDate;
    @Expose
    private Date CreatedAt;
    @Expose
    private Date ChangedAt;
    @Expose
    private EquipmentStatusSrv EquipmentStatus;

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
    public EquipmentTypeSrv getEquipmentType() {
        return EquipmentType;
    }

    /**
     * 
     * @param EquipmentType
     *     The EquipmentType
     */
    public void setEquipmentType(EquipmentTypeSrv EquipmentType) {
        this.EquipmentType = EquipmentType;
    }

    /**
     * 
     * @return
     *     The GeoCoordinates
     */
    public GeoCoordinatesSrv getGeoCoordinates() {
        return GeoCoordinates;
    }

    /**
     * 
     * @param GeoCoordinates
     *     The GeoCoordinates
     */
    public void setGeoCoordinates(GeoCoordinatesSrv GeoCoordinates) {
        this.GeoCoordinates = GeoCoordinates;
    }

    /**
     * 
     * @return
     *     The CriticalityType
     */
    public CriticalTypeSrv getCriticalityType() {
        return CriticalityType;
    }

    /**
     * 
     * @param CriticalityType
     *     The CriticalityType
     */
    public void setCriticalityType(CriticalTypeSrv CriticalityType) {
        this.CriticalityType = CriticalityType;
    }

    /**
     * 
     * @return
     *     The Documents
     */
    public List<EquipmentDocumentationSrv> getDocuments() {
        return Documents;
    }

    /**
     * 
     * @param Documents
     *     The Documents
     */
    public void setDocuments(List<EquipmentDocumentationSrv> Documents) {
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
    public Date getCreatedAt() {
        return CreatedAt;
    }

    /**
     * 
     * @param CreatedAt
     *     The CreatedAt
     */
    public void setCreatedAt(Date CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    /**
     * 
     * @return
     *     The ChangedAt
     */
    public Date getChangedAt() {
        return ChangedAt;
    }

    /**
     * 
     * @param ChangedAt
     *     The ChangedAt
     */
    public void setChangedAt(Date ChangedAt) {
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
	public Date getStartupDate() {
		return StartupDate;
	}

	/**
	 * @param startupDate the startupDate to set
	 */
	public void setStartupDate(Date startupDate) {
		StartupDate = startupDate;
	}

	/**
	 * @return the equipmentStatus
	 */
	public EquipmentStatusSrv getEquipmentStatus() {
		return EquipmentStatus;
	}

	/**
	 * @param equipmentStatus the equipmentStatus to set
	 */
	public void setEquipmentStatus(EquipmentStatusSrv equipmentStatus) {
		EquipmentStatus = equipmentStatus;
	}

}
