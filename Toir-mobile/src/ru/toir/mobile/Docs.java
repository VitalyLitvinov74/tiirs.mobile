package ru.toir.mobile;
/**
 * Класс для работы с документацией, хранящейся на мобильном клиенте 
 */

public class Docs {		
		private long _id;
		private String name;
		private String link;
		private int type;
		private int equipment;

		/**
		 * 
		 */
		public Docs() {
		}

		public Docs(long _id, String name, String link, int type, int equipment) {
			this._id = _id;
			this.name = name;
			this.link = link;
			this.type = type;
			this.equipment = equipment;
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
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the link
		 */
		public String getLink() {
			return link;
		}

		/**
		 * @param link the link to set
		 */
		public void setLink(String link) {
			this.link = link;
		}

		/**
		 * @return the equipment ID
		 */
		public int getEquipment() {
			return equipment;
		}

		/**
		 * @param to set
		 */
		public void setEquipment(int equipment) {
			this.equipment = equipment;
		}

		/**
		 * @return the type
		 */
		public int getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(int type) {
			this.type = type;
		}
}
