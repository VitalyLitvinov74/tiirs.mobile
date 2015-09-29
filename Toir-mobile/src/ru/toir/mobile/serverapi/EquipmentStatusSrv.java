package ru.toir.mobile.serverapi;

import java.util.ArrayList;

import ru.toir.mobile.db.tables.EquipmentStatus;
import com.google.gson.annotations.Expose;

/**
 * Статус оборудования
 * 
 * @author Dmitriy Logachov
 * 
 */
public class EquipmentStatusSrv extends BaseObjectSrv {

	@Expose
	private String Title;
	@Expose
	private int Type;

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
	 * @return the type
	 */
	public int getType() {
		return Type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		Type = type;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return EquipmentStatus
	 */
	public EquipmentStatus getLocal() {

		EquipmentStatus item = new EquipmentStatus();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<EquipmentStatus> getEquipmentStatuses(
			ArrayList<EquipmentStatusSrv> statuses) {

		ArrayList<EquipmentStatus> list = new ArrayList<EquipmentStatus>();
		for (EquipmentStatusSrv status : statuses) {
			list.add(status.getLocal());
		}
		return list;
	}

}
