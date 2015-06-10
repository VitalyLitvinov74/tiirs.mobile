package ru.toir.mobile.db.tables;
/**
 * Класс для работы с типами документов
 * Как и все справочники все функции только для чтения 
 */
public class TypeDocs {
		private long _id;
		private int type;
		private String name;

		public TypeDocs() {
		}

		public TypeDocs(long _id,  int type, String name) {
			this._id = _id;
			this.type = type;
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the type
		 */
		public int getType() {
			return type;
		}
		
		/**
		 * @return id
		 */
		public long getId() {
			return _id;
		}
}
