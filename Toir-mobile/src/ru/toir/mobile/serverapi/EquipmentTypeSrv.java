package ru.toir.mobile.serverapi;

import java.util.ArrayList;

import ru.toir.mobile.db.tables.EquipmentType;
import com.google.gson.annotations.Expose;

/**
 * Тип оборудования
 * 
 * @author Dmitriy Logachov
 * 
 */
public class EquipmentTypeSrv extends BaseObjectSrv {

	@Expose
	private String Title;

	/**
	 * 
	 * @return The Title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * 
	 * @param Title
	 *            The Title
	 */
	public void setTitle(String Title) {
		this.Title = Title;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return EquipmentType
	 */
	public EquipmentType getLocal() {

		EquipmentType item = new EquipmentType();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<EquipmentType> getEquipmentTypes(
			ArrayList<EquipmentTypeSrv> types) {

		ArrayList<EquipmentType> list = new ArrayList<EquipmentType>();
		for (EquipmentTypeSrv type : types) {
			list.add(type.getLocal());
		}
		return list;
	}

}
