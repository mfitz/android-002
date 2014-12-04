package com.michaelfitzmaurice.dailyselfie;

import android.app.Activity;
import android.os.Bundle;

public class PreferencesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getFragmentManager()
			.beginTransaction()
			.replace (android.R.id.content, new SettingsFragment() )
			.commit();
		setTitle(R.string.action_settings);
	}
}
