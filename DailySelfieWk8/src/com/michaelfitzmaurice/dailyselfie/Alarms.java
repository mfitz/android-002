package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Singleton to manage a single alarm
 * 
 * @author Michael Fitzmaurice, November 2014
 */
public class Alarms {
	
	private static final long ONE_MINUTE_IN_MS = 1000 * 60;
	private static final long ONE_HOUR_IN_MS = ONE_MINUTE_IN_MS * 60;
	private static final long ONE_DAY_IN_MS = ONE_HOUR_IN_MS * 24;
	
	
	private static final long TWO_MINUTES_IN_MS = ONE_MINUTE_IN_MS * 2;
	
	private static AlarmManager alarmManager;
	private static PendingIntent pendingIntent;
	private static Alarms instance;
	private static long delayMs = TWO_MINUTES_IN_MS;
	private static long intervalMs = TWO_MINUTES_IN_MS;

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
			
	public static void setContext(Context context) {   
		
		alarmManager = 
			(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent receiverIntent = new Intent(context, AlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(context, 
													0, 
													receiverIntent, 
													0);
	}
	
	public void set() {
		
		Log.d(LOG_TAG, "Setting new notification alarm to start in " 
						+ delayMs + "ms and run every " 
						+ intervalMs + " ms");
		Log.d(LOG_TAG, "Pending intent for alarm manager is: " 
										+ pendingIntent);
		alarmManager.setInexactRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP,
					SystemClock.elapsedRealtime() + delayMs,
					intervalMs,
					pendingIntent);
	}
	
	public void set(AlarmTimeInterval interval) {
		
		long millisecondsInterval = 
			(interval.getDays() * ONE_DAY_IN_MS) 
			+ (interval.getHours() * ONE_HOUR_IN_MS) 
			+ (interval.getMinutes() * ONE_MINUTE_IN_MS);
		delayMs = millisecondsInterval;
		intervalMs  = millisecondsInterval;
		this.set();
	}
	
	public void cancel() {
		
		Log.d(LOG_TAG, "Cancelling all alarms for PendingIntent: " 
							+ pendingIntent);
		alarmManager.cancel(pendingIntent);
	}
}
