package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		final SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences( getActivity() );
		Log.d(LOG_TAG, "All prefs in onCreate(): " + prefs.getAll() );
		
		addPreferencesFromResource(R.xml.preferences);
		
		SwitchPreference notificationSwitch = 
			(SwitchPreference) findPreference("notificationsXXX_switch");
		notificationSwitch.setOnPreferenceChangeListener(
			new OnPreferenceChangeListener() {
			
				@Override
				public boolean onPreferenceChange(Preference preference, 
													Object newValue) {
					Log.d(LOG_TAG, "All prefs before preference change handler returns: " 
										+ prefs.getAll() );
					Log.d(LOG_TAG, "New value for notification switch will be " 
							+ newValue);
					return true;
				}
			}
		);
		
	}
	
	private String dumpPrefs(SharedPreferences prefs) {
		
		StringBuilder builder = new StringBuilder();
//		prefs.
		
		return builder.toString();
	}
}


