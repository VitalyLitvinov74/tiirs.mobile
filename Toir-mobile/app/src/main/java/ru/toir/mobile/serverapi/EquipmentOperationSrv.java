package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.toir.mobile.db.tables.EquipmentOperation;
import com.google.gson.annotations.Expose;

import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.OperationStatus;

/**
 * Операция в наряде
 * 
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
	@Expose
	private ArrayList<MeasureValueSrv> MeasuredValues;
	@Expose
	private String EquipmentOperationResultId;
	@Expose
	private Date InspectionStartTime;
	@Expose
	private Date InspectionEndTime;
	@Expose
	private String OperationResultId;

	/**
	 * 
	 * @return The Position
	 */
	public Integer getPosition() {
		return Position;
	}

	/**
	 * 
	 * @param Position
	 *            The Position
	 */
	public void setPosition(Integer Position) {
		this.Position = Position;
	}

	/**
	 * 
	 * @return The Status
	 */
	public OperationStatusSrv getStatus() {
		return Status;
	}

	/**
	 * 
	 * @param Status
	 *            The Status
	 */
	public void setStatus(OperationStatusSrv Status) {
		this.Status = Status;
	}

	/**
	 * 
	 * @return The Equipment
	 */
	public EquipmentSrv getEquipment() {
		return Equipment;
	}

	/**
	 * 
	 * @param Equipment
	 *            The Equipment
	 */
	public void setEquipment(EquipmentSrv Equipment) {
		this.Equipment = Equipment;
	}

	/**
	 * 
	 * @return The OperationType
	 */
	public OperationTypeSrv getOperationType() {
		return OperationType;
	}

	/**
	 * 
	 * @param OperationType
	 *            The OperationType
	 */
	public void setOperationType(OperationTypeSrv OperationType) {
		this.OperationType = OperationType;
	}

	/**
	 * 
	 * @return The OperationPattern
	 */
	public String getOperationPatternId() {
		return OperationPatternId;
	}

	/**
	 * 
	 * @param OperationPattern
	 *            The OperationPattern
	 */
	public void setOperationPatternId(String OperationPattern) {
		this.OperationPatternId = OperationPattern;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @param uuid
	 *            наряда
	 * @return EquipmentOperation
	 */
	public EquipmentOperation getLocal(String uuid) {

		EquipmentOperation item = new EquipmentOperation();

		item.set_id(0);
		item.setUuid(Id);
		item.setTask_uuid(uuid);
		item.setEquipment_uuid(Equipment.getId());
		item.setOperation_type_uuid(OperationType.getId());
		item.setOperation_pattern_uuid(OperationPatternId);
		item.setOperation_status_uuid(Status.getId());
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<OperationType> getOperationTypes(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<OperationType> list = new ArrayList<OperationType>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getOperationType().getLocal());
		}
		return list;
	}

	public static ArrayList<Equipment> getEquipments(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<Equipment> list = new ArrayList<Equipment>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getEquipment().getLocal());
		}
		return list;
	}

	public static ArrayList<OperationStatus> getOperationStatuses(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<OperationStatus> list = new ArrayList<OperationStatus>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getStatus().getLocal());
		}
		return list;
	}

	public static Set<String> getOperationPatternUuids(
			ArrayList<EquipmentOperationSrv> operations) {

		Map<String, String> list = new HashMap<String, String>();
		for (EquipmentOperationSrv operation : operations) {
			list.put(operation.getOperationPatternId(), null);
		}
		return list.keySet();
	}

	public static ArrayList<EquipmentSrv> getEquipmentSrvs(
			ArrayList<EquipmentOperationSrv> operations) {

		ArrayList<EquipmentSrv> list = new ArrayList<EquipmentSrv>();
		for (EquipmentOperationSrv operation : operations) {
			list.add(operation.getEquipment());
		}
		return list;
	}

	public static Set<String> getOperationTypeUuids(
			ArrayList<EquipmentOperationSrv> operations) {

		Map<String, String> list = new HashMap<String, String>();
		for (EquipmentOperationSrv operation : operations) {
			list.put(operation.getOperationType().getId(), null);
		}
		return list.keySet();
	}

	/**
	 * @return the measuredValues
	 */
	public ArrayList<MeasureValueSrv> getMeasuredValues() {
		return MeasuredValues;
	}

	/**
	 * @param measuredValues
	 *            the measuredValues to set
	 */
	public void setMeasuredValues(ArrayList<MeasureValueSrv> measuredValues) {
		MeasuredValues = measuredValues;
	}

	public static ArrayList<MeasureValue> getMeasureValues(
			ArrayList<TaskSrv> tasks) {

		ArrayList<MeasureValue> list = new ArrayList<MeasureValue>();

		if (tasks != null) {
			for (TaskSrv task : tasks) {
				ArrayList<EquipmentOperationSrv> operations = task.getItems();
				if (operations != null) {
					for (EquipmentOperationSrv operation : operations) {
						ArrayList<MeasureValueSrv> measureValues = operation
								.getMeasuredValues();
						if (measureValues != null) {
							for (MeasureValueSrv measureValue : measureValues) {
								list.add(measureValue.getLocal());
							}
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * @return the equipmentOperationResultId
	 */
	public String getEquipmentOperationResultId() {
		return EquipmentOperationResultId;
	}

	/**
	 * @param equipmentOperationResultId
	 *            the equipmentOperationResultId to set
	 */
	public void setEquipmentOperationResultId(String equipmentOperationResultId) {
		EquipmentOperationResultId = equipmentOperationResultId;
	}

	/**
	 * @return the inspectionStartTime
	 */
	public Date getInspectionStartTime() {
		return InspectionStartTime;
	}

	/**
	 * @param inspectionStartTime
	 *            the inspectionStartTime to set
	 */
	public void setInspectionStartTime(Date inspectionStartTime) {
		InspectionStartTime = inspectionStartTime;
	}

	/**
	 * @return the inspectionEndTime
	 */
	public Date getInspectionEndTime() {
		return InspectionEndTime;
	}

	/**
	 * @param inspectionEndTime
	 *            the inspectionEndTime to set
	 */
	public void setInspectionEndTime(Date inspectionEndTime) {
		InspectionEndTime = inspectionEndTime;
	}

	/**
	 * @return the operationResultId
	 */
	public String getOperationResultId() {
		return OperationResultId;
	}

	/**
	 * @param operationResultId
	 *            the operationResultId to set
	 */
	public void setOperationResultId(String operationResultId) {
		OperationResultId = operationResultId;
	}
}
