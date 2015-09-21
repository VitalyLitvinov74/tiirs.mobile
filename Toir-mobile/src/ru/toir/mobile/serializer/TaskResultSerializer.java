/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.TaskResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 *
 */
public class TaskResultSerializer implements JsonSerializer<TaskResult> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(TaskResult item, Type arg1,
			JsonSerializationContext serializeContext) {

		JsonObject result = new JsonObject();
		result.add("Task", serializeContext.serialize(item.task));

		return result;
	}

}
