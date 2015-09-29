/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.MeasureValueRes;
import ru.toir.mobile.utils.DataUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 *
 */
public class MeasureValueSerializer implements JsonSerializer<MeasureValueRes> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(MeasureValueRes item, Type arg1,
			JsonSerializationContext serializeContext) {

		JsonObject result = new JsonObject();
		result.addProperty("Id", item.getUuid());
		result.addProperty("OperationPatternStepResultId", item.getOperation_pattern_step_result_uuid());
		result.addProperty("Date", DataUtils.getDate(item.getDate(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty("Value", item.getValue());
		result.addProperty("CreatedAt", DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty("ChangedAt", DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));
		
		return result;
	}

}
