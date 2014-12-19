package com.michaelfitzmaurice.dailyselfie.settings;

import static com.michaelfitzmaurice.dailyselfie.Alarms.ALARM_INTERVAL_PREFERENCES_KEY;
import static com.michaelfitzmaurice.dailyselfie.Alarms.NEXT_ALARM_DUE_PREFERENCES_KEY;
import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;
import static java.lang.String.format;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.michaelfitzmaurice.dailyselfie.AlarmTimeInterval;
import com.michaelfitzmaurice.dailyselfie.Alarms;
import com.michaelfitzmaurice.dailyselfie.R;

public class SettingsFragment extends PreferenceFragment {
	
	private SwitchPreference notificationSwitch;
	private OnSharedPreferenceChangeListener preferenceListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		notificationSwitch = 
			(SwitchPreference)findPreference( 
					getString(R.string.notifications_switch_key) );
		notificationSwitch.setTitle(R.string.notifications_switch_title);
		if ( notificationSwitch.isChecked() ) {
			notificationSwitch.setSummary( alarmNextDueString() );
		} else {
			notificationSwitch.setSummary(
					R.string.notifications_switched_off_confirmation);
		}
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
						SettingsFragment.this.notificationSwitch.setSummary(
							R.string.notifications_switched_off_confirmation);
						showToast(R.string.notifications_switched_off_confirmation);
					} else {
						Log.d(LOG_TAG, "Enabling notifications... ");
						AlarmTimeInterval interval = getAlarmInterval(); 
						Alarms.getInstance().set(interval);
						SettingsFragment.this.notificationSwitch.setSummary( 
							alarmNextDueString() );
						showToast(R.string.notifications_switched_on_confirmation,
									interval.getDays(),
									interval.getHours(),
									interval.getMinutes() );
					}
					
					return true;
				}
			}
		);
		
		preferenceListener = new NextAlarmPreferenceListener();
		getActivity().getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE) 
			.registerOnSharedPreferenceChangeListener(preferenceListener);
	}
	
	@Override
	public void onDestroy() {
		
		if (preferenceListener != null) {
			getActivity().getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE)
				.unregisterOnSharedPreferenceChangeListener(preferenceListener);
		}
		super.onDestroy();
	}

	private AlarmTimeInterval getAlarmInterval() {
		
		AlarmTimeInterval alarmInterval = null;
		SharedPreferences prefs = 
			getActivity().getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
		String alarmIntervalString = 
			prefs.getString(ALARM_INTERVAL_PREFERENCES_KEY, null);
		if (alarmIntervalString != null) {
			alarmInterval = new AlarmTimeInterval(alarmIntervalString);
		}
		
		return alarmInterval;
	}
	
	private void showToast(int messageId, Object... formatArgs) {
    	
    	String message = getActivity().getString(messageId);
    	if (formatArgs != null) {
    		message = format(message, formatArgs);
    	}
    	
    	Toast.makeText(getActivity(),
						message, 
						Toast.LENGTH_SHORT)
				.show();
    }
	
	private long alarmNextDueMs() {
		
	    SharedPreferences sharedPrefs = 
	    	getActivity().getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
		long alarmNextDueMs = 
			sharedPrefs.getLong(NEXT_ALARM_DUE_PREFERENCES_KEY, -1);
		
	    return alarmNextDueMs;
    }
	
	private String alarmNextDueString() {
		
		return format(getString(R.string.notifications_next_due), 
						new Date( alarmNextDueMs() ).toString() );
	}
	
	private class NextAlarmPreferenceListener 
	implements OnSharedPreferenceChangeListener {

		@Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, 
        										String key) {
			
			if ( NEXT_ALARM_DUE_PREFERENCES_KEY.equals(key) ) {
				Log.d(LOG_TAG, 
					"Updating setting notification switch summary in response " 
							+ "to change in shared preferences key " 
							+ NEXT_ALARM_DUE_PREFERENCES_KEY);
				notificationSwitch.setSummary( alarmNextDueString() );
			}
        }
	}
}
