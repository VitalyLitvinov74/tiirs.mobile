/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.TaskRes;
import ru.toir.mobile.utils.DataUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 * 
 */
public class TaskSerializer implements JsonSerializer<TaskRes> {

	private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(TaskRes item, Type arg1,
			JsonSerializationContext serializeContext) {
		JsonObject result = new JsonObject();
		result.addProperty("Id", item.getUuid());
		result.addProperty("EmployeeId", item.getUsers_uuid());
		result.addProperty("CloseDate",
				DataUtils.getDate(item.getClose_date(), dateFormat));
		result.addProperty("TaskStatusId", item.getTask_status_uuid());
		result.addProperty("TaskName", item.getTask_name());
		result.addProperty("CreatedAt",
				DataUtils.getDate(item.getCreatedAt(), dateFormat));
		result.addProperty("ChangedAt",
				DataUtils.getDate(item.getChangedAt(), dateFormat));
		result.add("EquipmentOperations",
				serializeContext.serialize(item.getEquipmentOperations()));

		return result;
	}

}
