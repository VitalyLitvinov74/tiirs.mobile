/**
 * 
 */
package ru.toir.mobile.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import ru.toir.mobile.RFIDActivity;

/**
 * @author koputo
 *
 */
public class RFIDActivityTest extends
		ActivityInstrumentationTestCase2<RFIDActivity> {
	private Activity activity;
	
	/**
	 * @param activityClass
	 */
	public RFIDActivityTest() {
		super(RFIDActivity.class);
	}
	
	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}
	/**
	 * проверяем что активити создана и имеет кнопку "Считать"
	 */
	public void testControlCreated() {
		assertNotNull(activity);
		Button button;
		button = (Button) activity.findViewById(ru.toir.mobile.R.id.cancelButton);
		assertNotNull(button);
	}
}
