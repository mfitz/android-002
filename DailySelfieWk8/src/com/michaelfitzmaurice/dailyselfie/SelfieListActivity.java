package com.michaelfitzmaurice.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SelfieListActivity extends ListActivity {
	
	static final File STORAGE_DIRECTORY = getStorageDirectory();
	static final String LOG_TAG = "DailySelfie";
	
	private static final String STORAGE_DIR_NAME = "dailyselfie";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final SimpleDateFormat DATE_FORMATTER = 
			new SimpleDateFormat("ddMMMyyyy_HHmmss");
	private static final long TWO_MINUTES_IN_MS = 
			1000 * 60 * 2;
	
	private SelfieListViewAdapter listAdapter;
	private Uri latestSelfieUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (STORAGE_DIRECTORY.exists() == false) {
			Log.i(LOG_TAG, "Creating new storage directory at " 
					+ STORAGE_DIRECTORY.getAbsolutePath() );
			boolean created = STORAGE_DIRECTORY.mkdir();
			Log.i(LOG_TAG, "Created storage directory:  " + created);
		} else {
			Log.d(LOG_TAG, "Storage directory already exists at " 
					+ STORAGE_DIRECTORY.getAbsolutePath() );
		}
		
		listAdapter = new SelfieListViewAdapter( getApplicationContext() );
		setListAdapter(listAdapter);
		
		// TODO set alarm for AlarmNotifier, every 2 minutes, from now
		AlarmManager alarmManager = 
			(AlarmManager) getSystemService(ALARM_SERVICE);
		Intent receiverIntent = 
			new Intent(SelfieListActivity.this, AlarmNotifier.class);
		PendingIntent pendingIntent = 
			PendingIntent.getBroadcast(SelfieListActivity.this, 
										0, 
										receiverIntent, 
										0);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
											0,
											TWO_MINUTES_IN_MS,
											pendingIntent);
	}
	
	private void takeSelfie() {
		Log.d(LOG_TAG, "Taking a new selfie...");
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	    	try {
	    		latestSelfieUri = createImageFile();
	    		Log.d(LOG_TAG, "Instructing camera to save new image at " + latestSelfieUri);
	    	} catch (IOException e) {
	    		Log.e(LOG_TAG, "Unable to create image file", e);
	    		Toast.makeText(getApplicationContext(), 
	    					"Error - unable to create image file!", 
	    					Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, latestSelfieUri);
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	private Uri createImageFile() throws IOException {
		
	    String timeStamp = DATE_FORMATTER.format( new Date() );
	    String imageFileName = timeStamp + "_";
	    File imageFile = 
	    		File.createTempFile(imageFileName, 
	    							".jpg",
	    							getStorageDirectory() );

	    return Uri.fromFile(imageFile);
	}
	
	static File getStorageDirectory() {
		
		File externalStorageDir = 
	    		Environment.getExternalStoragePublicDirectory(
	    				Environment.DIRECTORY_PICTURES);
		return new File(externalStorageDir, STORAGE_DIR_NAME);
	}
	
	@Override
	protected void onActivityResult(int requestCode, 
									int resultCode, 
									Intent data) {
		
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        File imageFile = new File( latestSelfieUri.getPath() );
	        SelfieRecord newSelfie = 
	        	new SelfieRecord(null, imageFile);
	        listAdapter.add(newSelfie);
	        Log.d(LOG_TAG, "Added new selfie for " + latestSelfieUri);
	    }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SelfieRecord selfie = (SelfieRecord) listAdapter.getItem(position);
		Intent detailIntent = new Intent(this, SelfieDetailActivity.class);
		detailIntent.putExtra( SelfieDetailActivity.IMAGE_FILE_URI, 
								selfie.getFullImageFile() );
		startActivity(detailIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.d(LOG_TAG, "Selected menu item " + item);
		int id = item.getItemId();
		if (id == R.id.take_selfie) {
			takeSelfie();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
