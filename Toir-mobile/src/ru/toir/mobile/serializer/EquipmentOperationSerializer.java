/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.EquipmentOperationRes;
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
		JsonSerializer<EquipmentOperationRes> {

	private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(EquipmentOperationRes item, Type arg1,
			JsonSerializationContext serializeContext) {

		JsonObject result = new JsonObject();
		result.addProperty("Id", item.getUuid());
		result.addProperty("EquipmentId", item.getEquipment_uuid());
		result.addProperty("OperationTypeId", item.getOperation_type_uuid());
		result.addProperty("OperationPatternId",
				item.getOperation_pattern_uuid());
		result.addProperty("OperationStatusId", item.getOperation_status_uuid());
		result.addProperty("OperationTime", item.getOperation_time());
		result.addProperty("CreatedAt",
				DataUtils.getDate(item.getCreatedAt(), dateFormat));
		result.addProperty("ChangedAt",
				DataUtils.getDate(item.getChangedAt(), dateFormat));
		result.add("EquipmentOperationResult",
				serializeContext.serialize(item.getEquipmentOperationResult()));
		result.add("MeasuredValues",
				serializeContext.serialize(item.getMeasureValues()));

		return result;
	}

}
