
package ru.toir.mobile.serverapi;

import java.util.Date;
import com.google.gson.annotations.Expose;

/**
 * Операция в наряде
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationSrv {

    @Expose
    private Integer Position;
    @Expose
    private OperationStatusSrv Status;
    @Expose
    private EquipmentSrv Equipment;
    @Expose
    private OperationTypeSrv OperationType;
    @Expose
    private OperationPatternSrv OperationPattern;
    @Expose
    private String Id;
    @Expose
    private Date CreatedAt;
    @Expose
    private Date ChangedAt;

    /**
     * 
     * @return
     *     The Position
     */
    public Integer getPosition() {
        return Position;
    }

    /**
     * 
     * @param Position
     *     The Position
     */
    public void setPosition(Integer Position) {
        this.Position = Position;
    }

    /**
     * 
     * @return
     *     The Status
     */
    public OperationStatusSrv getStatus() {
        return Status;
    }

    /**
     * 
     * @param Status
     *     The Status
     */
    public void setStatus(OperationStatusSrv Status) {
        this.Status = Status;
    }

    /**
     * 
     * @return
     *     The Equipment
     */
    public EquipmentSrv getEquipment() {
        return Equipment;
    }

    /**
     * 
     * @param Equipment
     *     The Equipment
     */
    public void setEquipment(EquipmentSrv Equipment) {
        this.Equipment = Equipment;
    }

    /**
     * 
     * @return
     *     The OperationType
     */
    public OperationTypeSrv getOperationType() {
        return OperationType;
    }

    /**
     * 
     * @param OperationType
     *     The OperationType
     */
    public void setOperationType(OperationTypeSrv OperationType) {
        this.OperationType = OperationType;
    }

    /**
     * 
     * @return
     *     The OperationPattern
     */
    public OperationPatternSrv getOperationPattern() {
        return OperationPattern;
    }

    /**
     * 
     * @param OperationPattern
     *     The OperationPattern
     */
    public void setOperationPattern(OperationPatternSrv OperationPattern) {
        this.OperationPattern = OperationPattern;
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

}
