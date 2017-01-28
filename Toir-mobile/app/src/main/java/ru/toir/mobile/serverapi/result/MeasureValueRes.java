/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import java.util.ArrayList;

import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import android.content.Context;

/**
 * @author Dmitriy Logachov
 *
 */
public class MeasureValueRes extends ru.toir.mobile.db.tables.MeasureValue {

	/**
	 * 
	 */
	public MeasureValueRes() {
	}
	
	public static ArrayList<MeasureValueRes> load(Context context, String uuid) {
		MeasureValueDBAdapter adapter = new MeasureValueDBAdapter(new ToirDatabaseContext(context));
		ArrayList<ru.toir.mobile.db.tables.MeasureValue> valuesList = adapter.getItems(uuid);
		if (valuesList != null) {
			ArrayList<MeasureValueRes> returnList = new ArrayList<MeasureValueRes>();
			for (ru.toir.mobile.db.tables.MeasureValue value: valuesList) {

				MeasureValueRes item = new MeasureValueRes();
				item._id = value.get_id();
				item.uuid = value.getUuid();
				item.setEquipment_operation_uuid(value.getEquipment_operation_uuid());
				item.setOperation_pattern_step_result_uuid(value.getOperation_pattern_step_result_uuid());
				item.setValue(value.getValue());
				item.setAttempt_send_date(value.getAttempt_send_date());
				item.setAttempt_count(value.getAttempt_count());
				item.setUpdated(value.isUpdated());
				item.CreatedAt = value.getCreatedAt();
				item.ChangedAt = value.getChangedAt();

				returnList.add(item);
			}
			return returnList;
			
		} else {
			return null;
		}
		
	}

}
