/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import android.content.Context;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationResultRes extends ru.toir.mobile.db.tables.EquipmentOperationResult {

	/**
	 * 
	 */
	public EquipmentOperationResultRes() {
	}
	
	public static EquipmentOperationResultRes load(Context context, String uuid) {
		EquipmentOperationResultDBAdapter adapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(context));
		
		ru.toir.mobile.db.tables.EquipmentOperationResult result = new ru.toir.mobile.db.tables.EquipmentOperationResult();
		result = adapter.getItemByOperation(uuid);
		if (result != null) {

			EquipmentOperationResultRes item = new EquipmentOperationResultRes();
			item._id = result.get_id();
			item.uuid = result.getUuid();
			item.setEquipment_operation_uuid(result.getEquipment_operation_uuid());
			item.setStart_date(result.getStart_date());
			item.setEnd_date(result.getEnd_date());
			item.setOperation_result_uuid(result.getOperation_result_uuid());
			item.setType(result.getType());
			item.setAttempt_send_date(result.getAttempt_send_date());
			item.setAttempt_count(result.getAttempt_count());
			item.setUpdated(result.isUpdated());
			item.CreatedAt = result.getCreatedAt();
			item.ChangedAt = result.getChangedAt();

			return item;
		} else {
			return null;
		}
	}

}
