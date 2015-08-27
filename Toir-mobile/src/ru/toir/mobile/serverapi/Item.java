
package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;
/**
 * Операция в наряде
 * @author Dmitriy Logachov
 *
 */
public class Item {

    @Expose
    private Integer Position;
    @Expose
    private ru.toir.mobile.serverapi.Status Status;
    @Expose
    private ru.toir.mobile.serverapi.Equipment Equipment;
    @Expose
    private ru.toir.mobile.serverapi.OperationType OperationType;
    @Expose
    private ru.toir.mobile.serverapi.OperationPattern OperationPattern;
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
    public ru.toir.mobile.serverapi.Status getStatus() {
        return Status;
    }

    /**
     * 
     * @param Status
     *     The Status
     */
    public void setStatus(ru.toir.mobile.serverapi.Status Status) {
        this.Status = Status;
    }

    /**
     * 
     * @return
     *     The Equipment
     */
    public ru.toir.mobile.serverapi.Equipment getEquipment() {
        return Equipment;
    }

    /**
     * 
     * @param Equipment
     *     The Equipment
     */
    public void setEquipment(ru.toir.mobile.serverapi.Equipment Equipment) {
        this.Equipment = Equipment;
    }

    /**
     * 
     * @return
     *     The OperationType
     */
    public ru.toir.mobile.serverapi.OperationType getOperationType() {
        return OperationType;
    }

    /**
     * 
     * @param OperationType
     *     The OperationType
     */
    public void setOperationType(ru.toir.mobile.serverapi.OperationType OperationType) {
        this.OperationType = OperationType;
    }

    /**
     * 
     * @return
     *     The OperationPattern
     */
    public ru.toir.mobile.serverapi.OperationPattern getOperationPattern() {
        return OperationPattern;
    }

    /**
     * 
     * @param OperationPattern
     *     The OperationPattern
     */
    public void setOperationPattern(ru.toir.mobile.serverapi.OperationPattern OperationPattern) {
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
