/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;

import ru.toir.mobile.db.adapters.BaseDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.utils.DataUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 *
 */
public class MeasureValueSerializer implements JsonSerializer<MeasureValue> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(MeasureValue item, Type arg1,
			JsonSerializationContext arg2) {

		JsonObject result = new JsonObject();
		result.addProperty(BaseDBAdapter.FIELD_UUID, item.getUuid());
		result.addProperty(MeasureValueDBAdapter.FIELD_EQUIPMENT_OPERATION_UUID, item.getEquipment_operation_uuid());
		result.addProperty(MeasureValueDBAdapter.FIELD_OPERATION_PATTERN_STEP_RESULT, item.getOperation_pattern_step_result());
		result.addProperty(MeasureValueDBAdapter.FIELD_DATE, DataUtils.getDate(item.getDate(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(MeasureValueDBAdapter.FIELD_VALUE, item.getValue());
		result.addProperty(BaseDBAdapter.FIELD_CREATED_AT, DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(BaseDBAdapter.FIELD_CHANGED_AT, DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));
		
		return result;
	}

}
