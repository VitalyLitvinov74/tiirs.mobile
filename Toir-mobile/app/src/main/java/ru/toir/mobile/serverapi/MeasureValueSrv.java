/**
 * 
 */
package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.MeasureValue;

import com.google.gson.annotations.Expose;

/**
 * Результат измерений
 * 
 * @author Dmitriy Logachov
 * 
 */
public class MeasureValueSrv extends BaseObjectSrv {

	@Expose
	private String OrderItemId;
	@Expose
	private String Value;
	@Expose
	private String OperationPatternStepResultId;

	/**
	 * @return the orderItemId
	 */
	public String getOrderItemId() {
		return OrderItemId;
	}

	/**
	 * @param orderItemId
	 *            the orderItemId to set
	 */
	public void setOrderItemId(String orderItemId) {
		OrderItemId = orderItemId;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return Value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		Value = value;
	}

	/**
	 * @return the operationPatternStepResultId
	 */
	public String getOperationPatternStepResultId() {
		return OperationPatternStepResultId;
	}

	/**
	 * @param operationPatternStepResultId
	 *            the operationPatternStepResultId to set
	 */
	public void setOperationPatternStepResultId(
			String operationPatternStepResultId) {
		OperationPatternStepResultId = operationPatternStepResultId;
	}
	
	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @param uuid
	 *            наряда
	 * @return EquipmentOperation
	 */
	public MeasureValue getLocal() {
		
		MeasureValue item = new MeasureValue();

		item.setUuid(Id);
		item.setEquipment_operation_uuid(OrderItemId);
		item.setOperation_pattern_step_result_uuid(OperationPatternStepResultId);
		item.setValue(Value);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
