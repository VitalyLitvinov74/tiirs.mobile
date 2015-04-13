/**
 * 
 */
package ru.toir.mobile.test;

import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.Users;
import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

/**
 * @author koputo
 *
 */
public class UsersTest extends AndroidTestCase {
	private static final String TAG = "UsersTest";
	private static final String TEST_FILE_PREFIX = "test_";
	private TOiRDBAdapter adapter;
	private Context context;
	TOiRDatabaseContext dbContext;
	
	/**
	 *  
	 */
	public UsersTest() {
		Log.d(TAG, "UsersTest()");
	}
	
	/**
	 * проверка создания "пустого" пользователя 
	 */
	public void testCreateEmptyUser() {
		Users user = new Users(adapter);
		assertEquals(0, user.getId());
		assertEquals("", user.getName());
		assertEquals("", user.getLogin());
		assertEquals("", user.getPass());
		assertEquals(0, user.getType());
	}

	/**
	 * проверка создания пользователя 
	 */
	public void testCreateFilledUser() {
		Users user = new Users(adapter, 666, "testName", "testLogin", "testPass", 13);
		assertEquals(666, user.getId());
		assertEquals("testName", user.getName());
		assertEquals("testLogin", user.getLogin());
		assertEquals("testPass", user.getPass());
		assertEquals(13, user.getType());
	}
	
	/**
	 * проверка на создание адаптера базы
	 */
	public void testAdapterCreated() {
		boolean result = (adapter == null);
		assertEquals(false, result);
	}

	/**
	 * проверка чтения пользователя из базы
	 */
	public void testSelectUser() {
		Users user = new Users(adapter, 1);
		assertEquals(1, user.getId());
	}

	/**
	 * проверка записи в таблицу users
	 */
	public void testInsertUsers() {
		Users user = new Users(adapter);
		user.setName("demon");
		user.setLogin("demonlogin");
		user.setPass("demonpass");
		user.setType(666);
		assertEquals(2, user.saveUsers());
	}

	/**
	 * проверка удаления базы данных
	 */
	public void testDeleteDatabase() {
		adapter.close();
		adapter = null;
		assertEquals(true, dbContext.deleteDatabase(TOiRDBAdapter.getDbName()));
		// чтобы у нас тест выполнился создаём заново объект adapter, т.к. мы его удалили для того что бы закрыть базу и удалить её
		adapter = new TOiRDBAdapter(context);
		adapter.open();
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Log.d(TAG, "setUp");
		
		// меняем контекст (ко всем файлам будет добавлено TEST_FILE_PREFIX)
		context = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
		// контекст базы данных, для удаления после тестов
		dbContext = new TOiRDatabaseContext(context);
		// удаляем базу
        dbContext.deleteDatabase(TOiRDBAdapter.getDbName());

        adapter = new TOiRDBAdapter(context);
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
		dbContext.deleteDatabase(TOiRDBAdapter.getDbName());
		dbContext = null;
	}
}
