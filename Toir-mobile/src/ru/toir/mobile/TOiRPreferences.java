package ru.toir.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class TOiRPreferences extends Activity {

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new TOiRPreferenceFragment()).commit();
	}
	
	public class TOiRPreferenceFragment extends PreferenceFragment {

		/* (non-Javadoc)
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
		
	}


}
