package ru.toir.mobile.serverapi;

import java.util.ArrayList;

import ru.toir.mobile.db.tables.MeasureType;
import com.google.gson.annotations.Expose;

/**
 * Тип измерения
 * @author Dmitriy Logachov
 *
 */
public class MeasureTypeSrv extends BaseObjectSrv {

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
	 * @return MeasureType
	 */
    public MeasureType getLocal() {

		MeasureType item = new MeasureType();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<MeasureType> getMeasureTypes(ArrayList<MeasureTypeSrv> types) {

		ArrayList<MeasureType> list = new ArrayList<MeasureType>();
		for (MeasureTypeSrv type : types) {
			list.add(type.getLocal());
		}
		return list;
	}

}
