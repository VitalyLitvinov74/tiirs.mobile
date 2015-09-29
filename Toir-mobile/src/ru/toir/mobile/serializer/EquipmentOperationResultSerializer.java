/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.EquipmentOperationResult;
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
			JsonSerializationContext serializeContext) {
		
		JsonObject result = new JsonObject();
		result.addProperty("Id", item.getUuid());
		result.addProperty("StartDate", DataUtils.getDate(item.getStart_date(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty("EndDate", DataUtils.getDate(item.getEnd_date(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty("OperationResultId", item.getOperation_result_uuid());
		result.addProperty("Type", item.getType());
		result.addProperty("CreatedAt", DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty("ChangedAt", DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));

		return result;
	}

}
