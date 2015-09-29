package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.DocumentationType;
import com.google.gson.annotations.Expose;

/**
 * Типы документов
 * 
 * @author Dmitriy Logachov
 * 
 */
public class DocumentationTypeSrv extends BaseObjectSrv {

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
	 * @return DocumentationType
	 */
	public DocumentationType getLocal() {

		DocumentationType item = new DocumentationType();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
