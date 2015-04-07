/**
 * 
 */
package ru.toir.mobile.test;

import ru.toir.mobile.Users;
import android.test.AndroidTestCase;

/**
 * @author koputo
 *
 */
public class UsersTest extends AndroidTestCase {
	/**
	 *  
	 */
	public UsersTest() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * проверка создания "пустого" пользователя 
	 */
	public void testCreateEmptyUser() {
		Users user = new Users();
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
		Users user = new Users(666, "testName", "testLogin", "testPass", 13);
		assertEquals(666, user.getId());
		assertEquals("testName", user.getName());
		assertEquals("testLogin", user.getLogin());
		assertEquals("testPass", user.getPass());
		assertEquals(13, user.getType());
	}
}
