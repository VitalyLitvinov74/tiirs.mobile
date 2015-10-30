/**
 * 
 */
package ru.toir.mobile.serializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import ru.toir.mobile.serverapi.result.MeasureValueRes;
import ru.toir.mobile.utils.DataUtils;
import android.util.Base64;
import android.util.Base64OutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Dmitriy Logachov
 * 
 */
public class MeasureValueSerializer implements JsonSerializer<MeasureValueRes> {

	private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

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
			File fname = new File(value);
			result.addProperty("Value", fname.getName());
			File file = new File(value);
			int bufferSize = 1024;
			int count;
			byte buffer[] = new byte[bufferSize];
			try {
				FileInputStream fis = new FileInputStream(file);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Base64OutputStream b64os = new Base64OutputStream(baos,
						Base64.NO_WRAP);

				while ((count = fis.read(buffer)) > 0) {
					b64os.write(buffer, 0, count);
				}

				b64os.close();
				baos.close();
				fis.close();

				result.addProperty("BinaryValue", baos.toString());
				result.addProperty("Encoding", "base64");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		result.addProperty("CreatedAt",
				DataUtils.getDate(item.getCreatedAt(), dateFormat));
		result.addProperty("ChangedAt",
				DataUtils.getDate(item.getChangedAt(), dateFormat));

		return result;
	}
}
