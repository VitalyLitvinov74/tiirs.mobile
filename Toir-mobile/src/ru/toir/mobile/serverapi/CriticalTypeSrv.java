package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;

import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import android.content.Context;

import com.google.gson.annotations.Expose;
/**
 * Типы критичности оборудования
 * @author Dmitriy Logachov
 *
 */
public class CriticalTypeSrv {

	@Expose
	private String Id;
	@Expose
	private Integer Value;
	@Expose
	private Date CreatedAt;
	@Expose
	private Date ChangedAt;

	/**
	 * 
	 * @return The Id
	 */
	public String getId() {
		return Id;
	}

	/**
	 * 
	 * @param Id
	 *            The Id
	 */
	public void setId(String Id) {
		this.Id = Id;
	}

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
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @param createdAt
	 *            the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}

	/**
	 * @return the changedAt
	 */
	public Date getChangedAt() {
		return ChangedAt;
	}

	/**
	 * @param changedAt
	 *            the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
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

}
