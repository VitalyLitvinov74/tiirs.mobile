/**
 * 
 */
package ru.toir.mobile.serializer;

import java.lang.reflect.Type;

import ru.toir.mobile.TaskResult;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
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
		result.add(TaskDBAdapter.TABLE_NAME, serializeContext.serialize(item.mTask));
		result.add(EquipmentOperationDBAdapter.TABLE_NAME, serializeContext.serialize(item.mEquipmentOperations));
		result.add(EquipmentOperationResultDBAdapter.TABLE_NAME, serializeContext.serialize(item.mEquipmentOperationResults));
		result.add(MeasureValueDBAdapter.TABLE_NAME, serializeContext.serialize(item.mMeasureValues));

		return result;
	}

}
