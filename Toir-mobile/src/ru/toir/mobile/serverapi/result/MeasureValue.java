/**
 * 
 */
package ru.toir.mobile.serverapi.result;

import java.util.ArrayList;

import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import android.content.Context;

/**
 * @author Dmitriy Logachov
 *
 */
public class MeasureValue extends ru.toir.mobile.db.tables.MeasureValue {

	/**
	 * 
	 */
	public MeasureValue() {
	}
	
	public static ArrayList<MeasureValue> load(Context context, String uuid) {
		MeasureValueDBAdapter adapter = new MeasureValueDBAdapter(new TOiRDatabaseContext(context));
		ArrayList<ru.toir.mobile.db.tables.MeasureValue> valuesList = adapter.getItems(uuid);
		if (valuesList != null) {
			ArrayList<MeasureValue> returnList = new ArrayList<MeasureValue>();
			for (ru.toir.mobile.db.tables.MeasureValue value: valuesList) {

				MeasureValue item = new MeasureValue();
				item._id = value.get_id();
				item.uuid = value.getUuid();
				item.setEquipment_operation_uuid(value.getEquipment_operation_uuid());
				item.setOperation_pattern_step_result_uuid(value.getOperation_pattern_step_result_uuid());
				item.setDate(value.getDate());
				item.setValue(value.getValue());
				item.setAttempt_send_date(value.getAttempt_send_date());
				item.setAttempt_count(value.getAttempt_count());
				item.setUpdated(value.isUpdated());
				item.CreatedAt = value.getCreatedAt();
				item.ChangedAt = value.getChangedAt();

				returnList.add((MeasureValue) item);
			}
			return returnList;
			
		} else {
			return null;
		}
		
	}

}
