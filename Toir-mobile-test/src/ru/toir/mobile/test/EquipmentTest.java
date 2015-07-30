/**
 * 
 */
package ru.toir.mobile.test;

import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

/**
 * @author koputo
 * <p>Тестирование адаптера к таблице equipment</p>
 *
 */
public class EquipmentTest extends AndroidTestCase {
	private static final String TAG = "EquipmentTest";
	private static final String TEST_FILE_PREFIX = "test_";
	private EquipmentDBAdapter adapter;
	private Context context;
	
	/**
	 * Конструктор 
	 */
	public EquipmentTest() {
		Log.d(TAG, "EquipmentTest()");
	}
	
	/**
	 * проверка на создание адаптера базы
	 */
	public void testAdapterCreated() {
		assertEquals(true, adapter != null);
	}

	/**
	 * проверка чтения оборудования из базы
	 */
	public void testSelectEquipment() {
		/*
		Cursor cursor = adapter.getItem("0000-00-00-00-0000");
		assertEquals(true, cursor.moveToFirst());
		assertEquals(1, cursor.getString(cursor.getColumnIndex(EquipmentDBAdapter.FIELD_UUID_NAME)));
		*/
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 * 
	 * вроде как при выполнении тестов база должна удаляться автоматически перед каждым тестом, хз.
	 * удаяем руками. возможно это связанно с переопределением контекста, т.к. мы храним базу на внешнем
	 * накопителе, и не все методы необходимые для этого определены. проверить!!!!
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Log.d(TAG, "setUp");
		
		// создаём тестовый контекст (в котором используются правильные пути к базе с префиксом к создаваемым файлам)
		context = new TOiRDatabaseContext(new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX));
		
		// удаляем базу
		context.deleteDatabase(TOiRDBAdapter.getDbName());
		
		// создаём базу
		new TOiRDBAdapter(context).open().close();
		
		// создаём адаптер для тестов, на базе переименованого и с "правильными" путями к базе данных (TOiRDatabaseContext)
        adapter = new EquipmentDBAdapter(context);
		adapter.open();
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Log.d(TAG, "tearDown");
		adapter.close();
		adapter = null;
		context.deleteDatabase(TOiRDBAdapter.getDbName());
	}
}
