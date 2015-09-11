/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.db.adapters.BaseDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.utils.DataUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationSerializer implements
		JsonSerializer<EquipmentOperation> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(EquipmentOperation item, Type arg1,
			JsonSerializationContext arg2) {

		JsonObject result = new JsonObject();
		result.addProperty(BaseDBAdapter.FIELD_UUID, item.getUuid());
		result.addProperty(EquipmentOperationDBAdapter.FIELD_TASK_UUID, item.getTask_uuid());
		result.addProperty(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID, item.getEquipment_uuid());
		result.addProperty(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID, item.getOperation_type_uuid());
		result.addProperty(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID, item.getOperation_pattern_uuid());
		result.addProperty(EquipmentOperationDBAdapter.FIELD_OPERATION_STATUS_UUID, item.getOperation_status_uuid());
		result.addProperty(EquipmentOperationDBAdapter.FIELD_OPERATION_TIME, item.getOperation_time());
		result.addProperty(BaseDBAdapter.FIELD_CREATED_AT, DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(BaseDBAdapter.FIELD_CHANGED_AT, DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));
		return result;
	}

}
