/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;

import ru.toir.mobile.db.adapters.BaseDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.utils.DataUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationResultSerializer implements
		JsonSerializer<EquipmentOperationResult> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(EquipmentOperationResult item, Type arg1,
			JsonSerializationContext arg2) {
		
		JsonObject result = new JsonObject();
		result.addProperty(BaseDBAdapter.FIELD_UUID, item.getUuid());
		result.addProperty(EquipmentOperationResultDBAdapter.FIELD_EQUIPMENT_OPERATION_UUID, item.getEquipment_operation_uuid());
		result.addProperty(EquipmentOperationResultDBAdapter.FIELD_START_DATE, DataUtils.getDate(item.getStart_date(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(EquipmentOperationResultDBAdapter.FIELD_END_DATE, DataUtils.getDate(item.getEnd_date(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(EquipmentOperationResultDBAdapter.FIELD_OPERATION_RESULT_UUID, item.getOperation_result_uuid());
		result.addProperty(EquipmentOperationResultDBAdapter.FIELD_TYPE, item.getType());
		result.addProperty(BaseDBAdapter.FIELD_CREATED_AT, DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(BaseDBAdapter.FIELD_CHANGED_AT, DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));

		return result;
	}

}
