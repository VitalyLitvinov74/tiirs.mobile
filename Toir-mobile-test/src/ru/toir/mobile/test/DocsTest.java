package ru.toir.mobile.test;

import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.DocsDBAdapter;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

	/**
	 * @author
	 * <p>Тестирование адаптера к таблице docs</p>
	 *
	 */
	public class DocsTest extends AndroidTestCase {
		private static final String TAG = "DocsTest";
		private static final String TEST_FILE_PREFIX = "test_";
		private DocsDBAdapter adapter;
		private Context context;
		
		/**
		 * Конструктор 
		 */
		public DocsTest() {
			Log.d(TAG, "DocsTest()");
		}
		
		/**
		 * проверка создания записи о документации 
		 */
		public void testCreateDocs() {
			long id = adapter.insertDocs("Installation manual v1.1", "/files/manual1.pdf", 1,1 );
			assertEquals(6, id);
		}
		
		/**
		 * проверка на создание адаптера базы
		 */
		public void testAdapterCreated() {
			assertEquals(true, adapter != null);
		}

		/**
		 * проверка чтения документации из базы
		 */
		public void testSelectDocs() {
			Cursor user = adapter.getDocs(1);
			//assertEquals(true, docs.moveToFirst());
			//assertEquals(1, user.getLong(EquipmentDBAdapter.FIELD_ID_COLUMN));
		}
}
