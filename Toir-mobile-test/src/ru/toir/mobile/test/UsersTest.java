/**
 * 
 */
package ru.toir.mobile.test;

import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

/**
 * @author koputo
 * <p>Тестирование адаптера к таблице users</p>
 *
 */
public class UsersTest extends AndroidTestCase {
	private static final String TAG = "UsersTest";
	private static final String TEST_FILE_PREFIX = "test_";
	private UsersDBAdapter adapter;
	private Context context;
	
	/**
	 * Конструктор 
	 */
	public UsersTest() {
		Log.d(TAG, "UsersTest()");
	}
	
	/**
	 * проверка на создание адаптера базы
	 */
	public void testAdapterCreated() {
		assertEquals(true, adapter != null);
	}

	/**
	 * проверка чтения пользователя из базы
	 */
	public void testSelectUserById() {
		String uuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
		Cursor user = adapter.getItem(uuid);
		assertEquals(true, user.moveToFirst());
		assertEquals(uuid, user.getString(user.getColumnIndex(UsersDBAdapter.FIELD_UUID_NAME)));
	}
	
	/**
	 * проверка чтения пользователя из из курсора
	 */
	public void testSelectUserFromCursor() {
		String name = "admin";
		String uuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
		Cursor user = adapter.getItem(uuid);
		assertEquals(true, user.moveToFirst());
		assertEquals(uuid, user.getString(user.getColumnIndex(UsersDBAdapter.FIELD_UUID_NAME)));
		assertEquals(name, user.getString(user.getColumnIndex(UsersDBAdapter.FIELD_NAME_NAME)));
	}

	/**
	 * проверка удаления базы данных
	 * 
	 * тест надуманный, использовался при "постижении" контекстов выполнения приложения
	 * в тестах базы по факту не нужен
	 */
	public void testDeleteDatabase() {
		// закрываем базу
		adapter.close();
		adapter = null;
		assertEquals(true, context.deleteDatabase(TOiRDBAdapter.getDbName()));
		
		// чтобы выполнились последующие тесты, создаём заново базу
		new TOiRDBAdapter(context).open().close();

		// для того чтобы выполнился tearDown, создаём заново адаптер
		adapter = new UsersDBAdapter(context);
		adapter.open();
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
		
		// контекст с переименованием файлов для тестов
		context = new TOiRDatabaseContext(new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX));
		
		// удаляем предыдущую тестовую базу
		context.deleteDatabase(TOiRDBAdapter.getDbName());
		
		// создаём тестовую базу
		new TOiRDBAdapter(context).open().close();
		
		// создаём адаптер для тестов, на базе переименованого и с "правильными" путями к базе данных (TOiRDatabaseContext)
        adapter = new UsersDBAdapter(context);
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
	}
}
