package ru.toir.mobile.db.tables;
/**
 * Класс для работы с документацией, хранящейся на мобильном клиенте 
 */

public class EquipmentDocumentation {		
		private long _id;
		private String uuid;
		private String equipment_uuid;
		private String documentation_type_uuid;
		private String title;
		private String path;
		private long CreatedAt;
		private long ChangedAt;

		/**
		 * 
		 */
		public EquipmentDocumentation() {
		}

		public EquipmentDocumentation(long _id, String uuid, String equipment_uuid, String documentation_type_uuid, String title, String path) {
			this._id = _id;
			this.uuid = uuid;
			this.equipment_uuid = equipment_uuid;
			this.documentation_type_uuid = documentation_type_uuid;
			this.title = title;
			this.path = path;
		}

		/**
		 * @return the _id
		 */
		public long get_id() {
			return _id;
		}

		/**
		 * @param _id the _id to set
		 */
		public void set_id(long _id) {
			this._id = _id;
		}

		/**
		 * @return the uuid
		 */
		public String getUuid() {
			return uuid;
		}

		/**
		 * @param uuid the uuid to set
		 */
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		/**
		 * @return the equipment_uuid
		 */
		public String getEquipment_uuid() {
			return equipment_uuid;
		}

		/**
		 * @param equipment_uuid the equipment_uuid to set
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
		 * @param documentation_type_uuid the documentation_type_uuid to set
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
		 * @param title the title to set
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
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}

		/**
		 * @return the createdAt
		 */
		public long getCreatedAt() {
			return CreatedAt;
		}

		/**
		 * @param createdAt the createdAt to set
		 */
		public void setCreatedAt(long createdAt) {
			CreatedAt = createdAt;
		}

		/**
		 * @return the changedAt
		 */
		public long getChangedAt() {
			return ChangedAt;
		}

		/**
		 * @param changedAt the changedAt to set
		 */
		public void setChangedAt(long changedAt) {
			ChangedAt = changedAt;
		}

}
