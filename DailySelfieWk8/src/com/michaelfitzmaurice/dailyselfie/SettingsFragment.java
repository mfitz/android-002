package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;
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

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		final SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences( getActivity() );
		Log.d(LOG_TAG, "All prefs in onCreate(): " + prefs.getAll() );
		
		addPreferencesFromResource(R.xml.preferences);
		
		SwitchPreference notificationSwitch = 
			(SwitchPreference)findPreference( 
					getString(R.string.notifications_switch_key) );
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
					return true;
				}
			}
		);
	}
	
	public void setAlarmInterval() {

		final Dialog d = new Dialog( getActivity() );
		d.setContentView(R.layout.interval_picker_view);
		d.setTitle( getString(R.string.notifications_interval_picker_title) );
		d.setCancelable(false);
		
		// TODO set current values of spinners from preferences
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
		minutesPicker.setMinValue(1);
		minutesPicker.setMaxValue(59);
		
		Button cancelButton = 
			(Button)d.findViewById(R.id.cancelNotificationIntervalButton);
		cancelButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss();				
			}
		});
		
		Button setButton = 
			(Button)d.findViewById(R.id.setNotificationIntervalButton);
		setButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "Set button clicked!");
				AlarmTimeInterval newTimeInterval = new AlarmTimeInterval();
				newTimeInterval.setDays( dayPicker.getValue() );
				newTimeInterval.setHours( hoursPicker.getValue() );
				newTimeInterval.setMinutes( minutesPicker.getValue() );
				Alarms.getInstance().set(newTimeInterval);
				// TODO save to preferences
				d.dismiss();				
			}
		});
		d.show();
	}
}


