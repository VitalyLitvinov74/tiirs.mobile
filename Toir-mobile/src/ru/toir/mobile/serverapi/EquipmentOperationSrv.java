
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Операция в наряде
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationSrv extends BaseObjectSrv {

    @Expose
    private Integer Position;
    @Expose
    private OperationStatusSrv Status;
    @Expose
    private EquipmentSrv Equipment;
    @Expose
    private OperationTypeSrv OperationType;
    @Expose
    private String OperationPatternId;

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
    public String getOperationPatternId() {
        return OperationPatternId;
    }

    /**
     * 
     * @param OperationPattern
     *     The OperationPattern
     */
    public void setOperationPatternId(String OperationPattern) {
        this.OperationPatternId = OperationPattern;
    }

}
