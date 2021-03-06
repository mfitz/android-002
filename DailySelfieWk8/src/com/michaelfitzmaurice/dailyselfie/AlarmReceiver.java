package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	
	private static final int NOTIFICATION_ID = 1;

	public AlarmReceiver() {}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Received a broadcast");
		Log.d(LOG_TAG, "Intent is " + intent);
		Log.d(LOG_TAG, "Context is " + context);
		
		notifyReceived(context);

		Intent notificationIntent = 
			new Intent(context, SelfieListActivity.class);
		PendingIntent pendingIntent = 
			PendingIntent.getActivity(context, 
										0,
										notificationIntent, 
										Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Uri soundUri = 
			RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		long[] vibratePattern = { 0, 200, 200, 300 };

		Notification.Builder notificationBuilder = 
			new Notification.Builder(context)
				.setTicker( context.getString(R.string.notification_ticker) )
				.setSmallIcon(android.R.drawable.ic_menu_camera)
				.setAutoCancel(true).setContentTitle( 
					context.getString(R.string.notification_content_title) )
				.setContentText( 
					context.getString(R.string.notification_content_text) )
				.setContentIntent(pendingIntent)
				.setSound(soundUri).setVibrate(vibratePattern);

		NotificationManager notificationManager = 
			(NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify( NOTIFICATION_ID,
									notificationBuilder.build() );
	}
	
	public void notifyReceived(Context context) {

		SharedPreferences sharedPrefences = 
			context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
		AlarmTimeInterval interval = Alarms.DEFAULT_ALARM_INTERVAL;
		String intervalString = 
			sharedPrefences.getString(Alarms.ALARM_INTERVAL_PREFERENCES_KEY, 
										null);
		if (intervalString != null) {
			interval = new AlarmTimeInterval(intervalString);
		}
		setNextAlarmDuePref(sharedPrefences, interval);
    }
	
	private void setNextAlarmDuePref(SharedPreferences sharedPrefences, 
									AlarmTimeInterval interval) {
		
		long nextAlarmDueMs = 
			System.currentTimeMillis() + interval.toMilliseconds();
		Date nextAlarmDueDate = new Date(nextAlarmDueMs);
		Log.d(LOG_TAG, "Persisting next alarm due time in prefs as " 
				+ nextAlarmDueDate);
		sharedPrefences
			.edit()
			.putLong(Alarms.NEXT_ALARM_DUE_PREFERENCES_KEY, 
						nextAlarmDueMs)
			.apply();
	}

}
