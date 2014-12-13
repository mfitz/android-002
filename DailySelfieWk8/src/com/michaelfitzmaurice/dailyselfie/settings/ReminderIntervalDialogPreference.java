package com.michaelfitzmaurice.dailyselfie.settings;

import static com.michaelfitzmaurice.dailyselfie.Alarms.ALARM_INTERVAL_PREFERENCES_KEY;
import static java.lang.String.format;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.michaelfitzmaurice.dailyselfie.AlarmTimeInterval;
import com.michaelfitzmaurice.dailyselfie.Alarms;
import com.michaelfitzmaurice.dailyselfie.R;

public class ReminderIntervalDialogPreference extends DialogPreference {

	public ReminderIntervalDialogPreference(Context context, 
											AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.interval_picker_view);
		setSummary();
	}
	
	@Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            builder.setTitle(R.string.notifications_interval_picker_title);
            builder.setPositiveButton(null, null);
            builder.setNegativeButton(null, null);
            super.onPrepareDialogBuilder(builder);  
    }
	
    @Override
    public void onBindDialogView(View view) {

    	final NumberPicker dayPicker = 
    		(NumberPicker) view.findViewById(R.id.daysNumberPicker);
    	dayPicker.setMinValue(0);
    	dayPicker.setMaxValue(365);
    		
    	final NumberPicker hoursPicker = 
    		(NumberPicker) view.findViewById(R.id.hoursNumberPicker);
    	hoursPicker.setMinValue(0);
    	hoursPicker.setMaxValue(23);

    	final NumberPicker minutesPicker = 
    		(NumberPicker) view.findViewById(R.id.minutesNumberPicker);
    	minutesPicker.setMinValue(0);
    	minutesPicker.setMaxValue(59);

    	AlarmTimeInterval alarmInterval = getAlarmInterval();
    	if (alarmInterval != null) {
    		dayPicker.setValue( alarmInterval.getDays() );
    		hoursPicker.setValue( alarmInterval.getHours() );
    		minutesPicker.setValue( alarmInterval.getMinutes() );
    	}
    	
    	Button cancelButton = 
    		(Button) view.findViewById(R.id.cancelNotificationIntervalButton);
    	cancelButton.setOnClickListener( new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			setSummary();
    			getDialog().dismiss();				
    		}
    	});

    	Button setButton = 
    		(Button) view.findViewById(R.id.setNotificationIntervalButton);
    	setButton.setOnClickListener( new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			AlarmTimeInterval newTimeInterval = new AlarmTimeInterval();
    			newTimeInterval.setDays( dayPicker.getValue() );
    			newTimeInterval.setHours( hoursPicker.getValue() );
    			newTimeInterval.setMinutes( minutesPicker.getValue() );
    			if ( newTimeInterval.isZero() ) {
    				Toast.makeText(getContext(),
    						R.string.notifications_interval_zero_warning, 
    						Toast.LENGTH_LONG)
    						.show();
    			} else {
    				SharedPreferences prefs = getSharedPreferences();
    						PreferenceManager.getDefaultSharedPreferences( 
    								getContext() );
    				prefs
	    				.edit()
	    				.putString(ALARM_INTERVAL_PREFERENCES_KEY, 
	    						newTimeInterval.serialiseToString() )
	    						.apply();
    				setSummary();
    				if ( remindersAreOn() ) {
    					Alarms.getInstance().set(newTimeInterval);
    				}
    				getDialog().dismiss();
    			}
    		}
    	});

    	super.onBindDialogView(view);
    }
    
    private AlarmTimeInterval getAlarmInterval() {
		
		AlarmTimeInterval alarmInterval = null;
		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences( getContext() );
		String alarmIntervalString = 
			prefs.getString(ALARM_INTERVAL_PREFERENCES_KEY, 
											null);
		if (alarmIntervalString != null) {
			alarmInterval = new AlarmTimeInterval(alarmIntervalString);
		}
		
		return alarmInterval;
	}
    
    private void setSummary() {
		
	    AlarmTimeInterval alarmInterval = getAlarmInterval();
		if (alarmInterval != null) {
			String text = 
				format( getContext().getString(
							R.string.notifications_preferences_on_summary), 
						alarmInterval.getDays(), 
						alarmInterval.getHours(),
						alarmInterval.getMinutes() );
			setSummary(text);
		} else {
			setSummary( 
				getContext().getString(
						R.string.notifications_preferences_off_summary) );
		}
    }
    
    private boolean remindersAreOn() {
    	
    	SharedPreferences prefs = 
    		PreferenceManager.getDefaultSharedPreferences( getContext() );
    	String reminderSwitchKey = 
    		getContext().getString(R.string.notifications_switch_key);
    	
    	return prefs.getBoolean(reminderSwitchKey, false);
    }

}
