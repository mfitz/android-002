package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.Alarms.ALARM_INTERVAL_PREFERENCES_KEY;
import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;
import static java.lang.String.format;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment {
	
	private SwitchPreference notificationSwitch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		notificationSwitch = 
			(SwitchPreference)findPreference( 
					getString(R.string.notifications_switch_key) );
		setNotificationSwitchSummary();
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
						setAlarmInterval();
					}
					setNotificationSwitchSummary();
					
					return true;
				}
			}
		);
	}

	private void setNotificationSwitchSummary() {
		
	    AlarmTimeInterval alarmInterval = getAlarmInterval();
		if (alarmInterval != null) {
			String text = 
				format( getString(R.string.notifications_preferences_on_summary), 
						alarmInterval.getDays(), 
						alarmInterval.getHours(),
						alarmInterval.getMinutes() );
			notificationSwitch.setSummary(text);
		} else {
			notificationSwitch.setSummary( 
				getString(R.string.notifications_preferences_off_summary) );
		}
    }
	
	public void setAlarmInterval() {
		
		final Dialog d = new Dialog( getActivity() );
		d.setContentView(R.layout.interval_picker_view);
		d.setTitle( getString(R.string.notifications_interval_picker_title) );
		d.setCancelable(false);
		
		final NumberPicker dayPicker = 
			(NumberPicker) d.findViewById(R.id.daysNumberPicker);
		dayPicker.setMinValue(0);
		dayPicker.setMaxValue(365);
		
		final NumberPicker hoursPicker = 
			(NumberPicker) d.findViewById(R.id.hoursNumberPicker);
		hoursPicker.setMinValue(0);
		hoursPicker.setMaxValue(23);
		
		final NumberPicker minutesPicker = 
			(NumberPicker) d.findViewById(R.id.minutesNumberPicker);
		minutesPicker.setMinValue(0);
		minutesPicker.setMaxValue(59);
		
		AlarmTimeInterval alarmInterval = getAlarmInterval();
		if (alarmInterval != null) {
			dayPicker.setValue( alarmInterval.getDays() );
			hoursPicker.setValue( alarmInterval.getHours() );
			minutesPicker.setValue( alarmInterval.getMinutes() );
		}
		
		Button cancelButton = 
			(Button)d.findViewById(R.id.cancelNotificationIntervalButton);
		cancelButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				notificationSwitch.setChecked(false);
				setNotificationSwitchSummary();
				d.dismiss();				
			}
		});
		
		Button setButton = 
			(Button)d.findViewById(R.id.setNotificationIntervalButton);
		setButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlarmTimeInterval newTimeInterval = new AlarmTimeInterval();
				newTimeInterval.setDays( dayPicker.getValue() );
				newTimeInterval.setHours( hoursPicker.getValue() );
				newTimeInterval.setMinutes( minutesPicker.getValue() );
				if ( newTimeInterval.isZero() ) {
					Toast.makeText(getActivity(),
								R.string.notifications_interval_zero_warning, 
								Toast.LENGTH_LONG)
							.show();
				} else {
					SharedPreferences prefs = 
						PreferenceManager.getDefaultSharedPreferences( 
															getActivity() );
					prefs
						.edit()
						.putString(ALARM_INTERVAL_PREFERENCES_KEY, 
									newTimeInterval.serialiseToString() )
						.apply();
					Alarms.getInstance().set(newTimeInterval);
					setNotificationSwitchSummary();
					d.dismiss();
				}
			}
		});
		d.show();
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
