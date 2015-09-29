/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import java.util.ArrayList;

import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import android.content.Context;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperation extends ru.toir.mobile.db.tables.EquipmentOperation {
	
	public EquipmentOperationResult equipmentOperationResult;
	public ArrayList<MeasureValue> measureValues;

	/**
	 * 
	 */
	public EquipmentOperation() {
	}

	public static ArrayList<EquipmentOperation> load(Context context, String taskUuid) {

		EquipmentOperationDBAdapter adapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(context));
		
		ArrayList<ru.toir.mobile.db.tables.EquipmentOperation> operationList = adapter.getItems(taskUuid);
		if (operationList != null) {
			
			ArrayList<EquipmentOperation> returnList = new ArrayList<EquipmentOperation>();
			for (ru.toir.mobile.db.tables.EquipmentOperation operation: operationList) {
				EquipmentOperation item = new EquipmentOperation();
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
				item.equipmentOperationResult = EquipmentOperationResult.load(context, item.uuid);
				item.measureValues = MeasureValue.load(context, item.uuid);
				
				returnList.add(item);
			}
			return returnList;
		} else {
			return null;
		}

	}

}
