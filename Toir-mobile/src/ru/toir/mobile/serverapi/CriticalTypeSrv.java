package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.CriticalType;
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
