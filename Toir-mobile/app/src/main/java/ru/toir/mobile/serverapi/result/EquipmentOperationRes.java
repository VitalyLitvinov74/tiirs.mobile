/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import java.util.ArrayList;

import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import android.content.Context;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationRes extends ru.toir.mobile.db.tables.EquipmentOperation {
	
	private EquipmentOperationResultRes equipmentOperationResult;
	private ArrayList<MeasureValueRes> measureValues;

	/**
	 * 
	 */
	public EquipmentOperationRes() {
	}

	public static ArrayList<EquipmentOperationRes> load(Context context, String taskUuid) {

		EquipmentOperationDBAdapter adapter = new EquipmentOperationDBAdapter(new ToirDatabaseContext(context));
		
		ArrayList<ru.toir.mobile.db.tables.EquipmentOperation> operationList = adapter.getItems(taskUuid);
		if (operationList != null) {
			
			ArrayList<EquipmentOperationRes> returnList = new ArrayList<EquipmentOperationRes>();
			for (ru.toir.mobile.db.tables.EquipmentOperation operation: operationList) {
				EquipmentOperationRes item = new EquipmentOperationRes();
				item.set_id(operation.get_id());
				item.setUuid(operation.getUuid());
				item.setTask_uuid(operation.getTask_uuid());
				item.setEquipment_uuid(operation.getEquipment_uuid());
				item.setOperation_type_uuid(operation.getOperation_type_uuid());
				item.setOperation_pattern_uuid(operation.getOperation_pattern_uuid());
				item.setOperation_status_uuid(operation.getOperation_status_uuid());
				item.setOperation_time(operation.getOperation_time());
				item.CreatedAt = operation.getCreatedAt();
				item.ChangedAt = operation.getChangedAt();
				item.equipmentOperationResult = EquipmentOperationResultRes.load(context, item.uuid);
				item.measureValues = MeasureValueRes.load(context, item.uuid);
				
				returnList.add(item);
			}
			return returnList;
		} else {
			return null;
		}

	}

	/**
	 * @return the equipmentOperationResult
	 */
	public EquipmentOperationResultRes getEquipmentOperationResult() {
		return equipmentOperationResult;
	}

	/**
	 * @param equipmentOperationResult the equipmentOperationResult to set
	 */
	public void setEquipmentOperationResult(
			EquipmentOperationResultRes equipmentOperationResult) {
		this.equipmentOperationResult = equipmentOperationResult;
	}

	/**
	 * @return the measureValues
	 */
	public ArrayList<MeasureValueRes> getMeasureValues() {
		return measureValues;
	}

	/**
	 * @param measureValues the measureValues to set
	 */
	public void setMeasureValues(ArrayList<MeasureValueRes> measureValues) {
		this.measureValues = measureValues;
	}

}
