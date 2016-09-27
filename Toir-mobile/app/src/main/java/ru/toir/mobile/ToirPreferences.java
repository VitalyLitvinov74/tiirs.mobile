package ru.toir.mobile;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import ru.toir.mobile.fragments.ToirPreferenceFragment;

public class ToirPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_preference);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ToirPreferenceFragment())
				.commit();
    }

}
