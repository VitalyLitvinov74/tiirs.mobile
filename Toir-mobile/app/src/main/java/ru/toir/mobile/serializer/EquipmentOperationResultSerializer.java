/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.EquipmentOperationResultRes;
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
		JsonSerializer<EquipmentOperationResultRes> {

	private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(EquipmentOperationResultRes item, Type arg1,
			JsonSerializationContext serializeContext) {

		JsonObject result = new JsonObject();
		result.addProperty("Id", item.getUuid());
		result.addProperty("StartDate",
				DataUtils.getDate(item.getStart_date(), dateFormat));
		result.addProperty("EndDate",
				DataUtils.getDate(item.getEnd_date(), dateFormat));
		result.addProperty("OperationResultId", item.getOperation_result_uuid());
		result.addProperty("Type", item.getType());
		result.addProperty("ChangedAt",
				DataUtils.getDate(item.getChangedAt(), dateFormat));

		return result;
	}

}
