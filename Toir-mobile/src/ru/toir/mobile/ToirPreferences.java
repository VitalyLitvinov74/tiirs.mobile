package ru.toir.mobile;

import ru.toir.mobile.fragments.ToirPreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ToirPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ToirPreferenceFragment())
				.commit();
	}

}
