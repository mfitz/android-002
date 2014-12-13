package com.michaelfitzmaurice.dailyselfie.settings;

import static com.michaelfitzmaurice.dailyselfie.Alarms.ALARM_INTERVAL_PREFERENCES_KEY;
import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import com.michaelfitzmaurice.dailyselfie.AlarmTimeInterval;
import com.michaelfitzmaurice.dailyselfie.Alarms;
import com.michaelfitzmaurice.dailyselfie.R;

public class SettingsFragment extends PreferenceFragment {
	
	private SwitchPreference notificationSwitch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		notificationSwitch = 
			(SwitchPreference)findPreference( 
					getString(R.string.notifications_switch_key) );
		notificationSwitch.setTitle(R.string.notifications_switch_title);
		notificationSwitch.setSummary(
			R.string.notifications_switch_summary);
		notificationSwitch.setOnPreferenceChangeListener(
			new OnPreferenceChangeListener() {
			
				@Override
				public boolean onPreferenceChange(Preference preference, 
													Object newValue) {
					boolean switchValue = (Boolean)newValue;
					Log.d(LOG_TAG, "New value for notification switch will be " 
									+ switchValue);
					if (switchValue == false) {
						Log.d(LOG_TAG, "Disabling notifications... ");
						Alarms.getInstance().cancel();
					} else {
						Log.d(LOG_TAG, "Enabling notifications... ");
						Alarms.getInstance().set( getAlarmInterval() );
					}
					
					return true;
				}
			}
		);
	}
	
	private AlarmTimeInterval getAlarmInterval() {
		
		AlarmTimeInterval alarmInterval = null;
		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences( getActivity() );
		String alarmIntervalString = 
			prefs.getString(ALARM_INTERVAL_PREFERENCES_KEY, null);
		if (alarmIntervalString != null) {
			alarmInterval = new AlarmTimeInterval(alarmIntervalString);
		}
		
		return alarmInterval;
	}
}
