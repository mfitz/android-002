package com.michaelfitzmaurice.dailyselfie;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.preferences);
		addPreferencesFromResource(R.layout.preferences);
	}
}
