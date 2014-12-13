package com.michaelfitzmaurice.dailyselfie.settings;

import com.michaelfitzmaurice.dailyselfie.R;
import com.michaelfitzmaurice.dailyselfie.R.string;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getFragmentManager()
			.beginTransaction()
			.replace( android.R.id.content, new SettingsFragment() )
			.commit();
		setTitle(R.string.action_settings);
	}
}
