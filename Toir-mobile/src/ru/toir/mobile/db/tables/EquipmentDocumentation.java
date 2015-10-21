package ru.toir.mobile.db.tables;

/**
 * Класс для работы с документацией, хранящейся на мобильном клиенте
 */

public class EquipmentDocumentation extends BaseTable {

	private String equipment_uuid;
	private String documentation_type_uuid;
	private String title;
	private String path;
	private boolean required;

	/**
	 * 
	 */
	public EquipmentDocumentation() {
	}

	/**
	 * @return the equipment_uuid
	 */
	public String getEquipment_uuid() {
		return equipment_uuid;
	}

	/**
	 * @param equipment_uuid
	 *            the equipment_uuid to set
	 */
	public void setEquipment_uuid(String equipment_uuid) {
		this.equipment_uuid = equipment_uuid;
	}

	/**
	 * @return the documentation_type_uuid
	 */
	public String getDocumentation_type_uuid() {
		return documentation_type_uuid;
	}

	/**
	 * @param documentation_type_uuid
	 *            the documentation_type_uuid to set
	 */
	public void setDocumentation_type_uuid(String documentation_type_uuid) {
		this.documentation_type_uuid = documentation_type_uuid;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public String toString() {

		return title;
	}

}
