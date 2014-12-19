package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

/**
 * Singleton to manage a single alarm
 * 
 * @author Michael Fitzmaurice, November 2014
 */
public class Alarms {
	
	public static final String ALARM_INTERVAL_PREFERENCES_KEY = 
		"alarmTimeInterval";
	public static final String NEXT_ALARM_DUE_PREFERENCES_KEY = 
		"nextAlarmDue";
	
	public static final AlarmTimeInterval DEFAULT_ALARM_INTERVAL = 
		new AlarmTimeInterval(1, 0, 0);
	
	private static AlarmManager alarmManager;
	private static SharedPreferences sharedPrefences;
	private static PendingIntent pendingIntent;
	private static Alarms instance;

	private Alarms() {}
	
	public static Alarms getInstance() {
		
		if (pendingIntent == null) {
			throw new IllegalStateException("Context has not yet been set");
		} else {
			synchronized (Alarms.class) {
				if (instance == null) {
					instance = new Alarms();
				}
            }
			return instance;
		}
	}
			
	public static void setContext(Context context, 
									SharedPreferences prefences) {   
		
		alarmManager = 
			(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent receiverIntent = new Intent(context, AlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(context, 
													0, 
													receiverIntent, 
													0);
		sharedPrefences = prefences;
	}
	
	public void setInitialAlarmIfRequired() {
		
		if (sharedPrefences.getString(ALARM_INTERVAL_PREFERENCES_KEY, null) != null) {
			Log.d(LOG_TAG, "Alarm is already set - nothing to do");
		} else {
			Log.i(LOG_TAG, 
				"Alarm has never been set - setting initial default alarm");
			sharedPrefences
				.edit()
				.putString(ALARM_INTERVAL_PREFERENCES_KEY, 
							DEFAULT_ALARM_INTERVAL.serialiseToString() )
				.apply();
			set(DEFAULT_ALARM_INTERVAL);
		}
	}
	
	public void set(AlarmTimeInterval interval) {
		
		if ( interval.isZero() ) {
			Log.w(LOG_TAG, 
				"User attempted to set alarm interval to 0 ms - ignoring...");
		} else {
			long delayMs = interval.toMilliseconds();
			long intervalMs  = interval.toMilliseconds();
			
			Log.d(LOG_TAG, "Setting new notification alarm to start in " 
					+ delayMs + " ms and run every " 
					+ intervalMs + " ms");
			Log.d(LOG_TAG, "Pending intent for alarm manager is: " 
											+ pendingIntent);
			alarmManager.setInexactRepeating(
						AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + delayMs,
						intervalMs,
						pendingIntent);
			setNextAlarmDuePref(interval);
		}
	}
	
	public void cancel() {
		
		Log.d(LOG_TAG, "Cancelling all alarms for PendingIntent: " 
							+ pendingIntent);
		alarmManager.cancel(pendingIntent);
	}
	
	private void setNextAlarmDuePref(AlarmTimeInterval interval) {
		
		long nextAlarmDueMs = 
			System.currentTimeMillis() + interval.toMilliseconds();
		Date nextAlarmDueDate = new Date(nextAlarmDueMs);
		Log.d(LOG_TAG, "Persisting next alarm due time in prefs as " 
				+ nextAlarmDueDate);
		sharedPrefences
			.edit()
			.putLong(NEXT_ALARM_DUE_PREFERENCES_KEY, 
						nextAlarmDueMs)
			.apply();
	}
}
