/**
 * 
 */
package ru.toir.mobile.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.MeasureValueRes;
import ru.toir.mobile.utils.DataUtils;
import android.util.Base64;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 * 
 */
public class MeasureValueSerializer implements JsonSerializer<MeasureValueRes> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(MeasureValueRes item, Type arg1,
			JsonSerializationContext serializeContext) {

		JsonObject result = new JsonObject();
		result.addProperty("Id", item.getUuid());
		result.addProperty("OperationPatternStepResultId",
				item.getOperation_pattern_step_result_uuid());
		String value = item.getValue();
		result.addProperty("Value", value);
		// пока тупо считаем что если значение начинается с '/' то это файл
		if (value.startsWith("/")) {
			File file = new File(value);
			int bufferSize = 1024;
			int count;
			byte buffer[] = new byte[bufferSize];
			StringBuilder valueBase64 = new StringBuilder();
			try {
				FileInputStream fis = new FileInputStream(file);
				while ((count = fis.read(buffer)) > 0) {
					valueBase64.append(Base64.encodeToString(buffer, 0, count,
							Base64.NO_CLOSE));
				}
				// TODO решить каким образом будут передаваться на сервер
				// бинарные данные
				// result.addProperty("BinaryValue", valueBase64.toString());
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.addProperty("CreatedAt",
				DataUtils.getDate(item.getCreatedAt(), "yyyy-MM-dd hh:mm:ss"));
		result.addProperty("ChangedAt",
				DataUtils.getDate(item.getChangedAt(), "yyyy-MM-dd hh:mm:ss"));

		return result;
	}

}
