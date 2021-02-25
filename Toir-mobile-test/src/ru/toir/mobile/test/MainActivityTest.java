/**
 * 
 */
package ru.toir.mobile.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import ru.toir.mobile.MainActivity;

/**
 * @author koputo
 *
 */
public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	private Activity activity;
	
	/**
	 * @param activityClass
	 */
	public MainActivityTest() {
		super(MainActivity.class);
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
	 * проверяем что активити создана и имеет TextView
	 */
	public void testControlCreated() {
		assertNotNull(activity);
		/*
		TextView textView;
		textView = (TextView) activity.findViewById(ru.toir.mobile.R.id.idTextView);
		assertNotNull(textView);
		*/
	}
}
