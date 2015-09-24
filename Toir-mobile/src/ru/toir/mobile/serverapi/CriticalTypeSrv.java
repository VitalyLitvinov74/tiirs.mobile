package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import android.content.Context;
import com.google.gson.annotations.Expose;

/**
 * Типы критичности оборудования
 * 
 * @author Dmitriy Logachov
 * 
 */
public class CriticalTypeSrv extends BaseObjectSrv {

	@Expose
	private Integer Value;

	/**
	 * 
	 * @return The Value
	 */
	public Integer getValue() {
		return Value;
	}

	/**
	 * 
	 * @param Value
	 *            The Value
	 */
	public void setValue(Integer Value) {
		this.Value = Value;
	}

	public static ArrayList<CriticalType> getLocalFormat(CriticalTypeSrv[] array) {

		ArrayList<CriticalType> list = new ArrayList<CriticalType>();

		if (array == null) {
			return list;
		}

		for (CriticalTypeSrv element : array) {
			CriticalType item = new CriticalType();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setType(element.getValue());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}

		return list;
	}

	public static void saveAll(CriticalTypeSrv[] array, Context context) {
		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(context));
		adapter.saveItems(getLocalFormat(array));
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return CriticalType
	 */
	public CriticalType getLocal() {

		CriticalType item = new CriticalType();

		item.set_id(0);
		item.setUuid(Id);
		item.setType(Value);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
