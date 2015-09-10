/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;

import ru.toir.mobile.db.adapters.BaseDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.utils.DataUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 *
 */
public class TaskSerializer implements JsonSerializer<Task> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(Task item, Type arg1,
			JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();
		result.addProperty(BaseDBAdapter.FIELD_UUID, item.getUuid());
		result.addProperty(TaskDBAdapter.FIELD_USER_UUID, item.getUsers_uuid());
		result.addProperty(TaskDBAdapter.FIELD_CLOSE_DATE, DataUtils.getDate(item.getClose_date(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(TaskDBAdapter.FIELD_TASK_STATUS_UUID, item.getTask_status_uuid());
		result.addProperty(TaskDBAdapter.FIELD_TASK_NAME, item.getTask_name());
		result.addProperty(BaseDBAdapter.FIELD_CREATED_AT, DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty(BaseDBAdapter.FIELD_CHANGED_AT, DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));
		return result;
	}

}
