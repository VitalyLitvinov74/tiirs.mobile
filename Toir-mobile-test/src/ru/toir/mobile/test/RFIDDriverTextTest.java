/**
 * 
 */
package ru.toir.mobile.test;

import ru.toir.mobile.rfid.driver.RFIDDriverText;
import android.test.AndroidTestCase;

/**
 * @author koputo
 *
 */
public class RFIDDriverTextTest extends AndroidTestCase {
	private RFIDDriverText driver;
	
	/**
	 * 
	 */
	public void testPreConditions() {
		assertNotNull(driver);
	}
	
	/**
	 * 
	 */
	public void testInitDriver() {
		assertTrue(driver.init());
	}
	
	/**
	 * 
	 */
	public void testReadTag() {
		assertTrue(driver.init());
		assertEquals("01234567", driver.read());
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		driver = new RFIDDriverText();
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		driver.close();
		driver = null;
	}
	
	

}
