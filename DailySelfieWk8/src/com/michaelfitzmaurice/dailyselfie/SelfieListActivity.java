package com.michaelfitzmaurice.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.michaelfitzmaurice.dailyselfie.settings.SettingsActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SelfieListActivity extends ListActivity {
	
	public static final String LOG_TAG = "DailySelfie";
	
	private static final int THUMBNAIL_SCALE_FACTOR = 8;
	private static final File STORAGE_DIRECTORY = getStorageDirectory();
	private static final String STORAGE_DIR_NAME = "dailyselfie";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final SimpleDateFormat DATE_FORMATTER = 
			new SimpleDateFormat("ddMMMyyyy_HHmmss");
	
	private SelfieListViewAdapter listAdapter;
	private ListView listView;
	private LinearLayout progressLayout;
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
		
		setContentView(R.layout.activity_main);
	    listView = (ListView) getListView();
	    progressLayout = (LinearLayout) findViewById(R.id.progressbar_view);
		
		SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(this);
		Log.d(LOG_TAG, "All prefs in SelfieListActivity.onCreate(): " 
						+ prefs.getAll() );
		Alarms.setContext(this);
		Alarms.getInstance().setInitialAlarmIfRequired(prefs);
		
		new ImageLoadTask().execute(null, null, null);
	}
	
	private List<SelfieRecord> selfieListFromStorageDir() {
		
		List<SelfieRecord> selfieList = new ArrayList<SelfieRecord>();
		Log.d(LOG_TAG, "Looking for existing selfies in " + STORAGE_DIRECTORY);
		File[] files = STORAGE_DIRECTORY.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				Log.d(LOG_TAG, "Found selfie at " + file);
				// TODO - check a disk location to see if the correct sized 
				// thumbnail already exists. could also use a memory cache
				// in addition to the disk cache. if cache check misses, 
				// generate the thumbnail and write to cache(s)
				SelfieRecord selfie = 
					new SelfieRecord(makeThumbnail(file), file);
				selfieList.add(selfie);
			}
		}
		
		return selfieList;
	}
	
	private Bitmap makeThumbnail(File imageFile) {
		
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		int thumbnailHeight = metrics.heightPixels / THUMBNAIL_SCALE_FACTOR;
		
		return ThumbnailUtils.extractThumbnail(
					BitmapFactory.decodeFile( imageFile.getPath() ), 
					thumbnailHeight, 
					thumbnailHeight);
	}
	
	private void takeSelfie() {
		Log.d(LOG_TAG, "Taking a new selfie...");
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	    	try {
	    		latestSelfieUri = createImageFile();
	    		Log.d(LOG_TAG, "Instructing camera to save new image at " 	
	    						+ latestSelfieUri);
	    	} catch (IOException e) {
	    		Log.e(LOG_TAG, "Unable to create image file", e);
	    		Toast.makeText(getApplicationContext(), 
	    					"Error - unable to create image file!", 
	    					Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
	    								latestSelfieUri);
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}
	
	private Uri createImageFile() throws IOException {
		
	    String timeStamp = DATE_FORMATTER.format( new Date() );
	    String imageFileName = timeStamp + "_";
	    File imageFile = 
	    		File.createTempFile(imageFileName, 
	    							".jpg",
	    							STORAGE_DIRECTORY );

	    return Uri.fromFile(imageFile);
	}
	
	private static File getStorageDirectory() {
		
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
	        // TODO write the thumbnail to a disk (and also mem?) cache 
	        // to save having to generate it again later
	        SelfieRecord newSelfie = 
	        	new SelfieRecord(makeThumbnail(imageFile), imageFile);
	        listAdapter.add(newSelfie);
	        Log.d(LOG_TAG, "Added new selfie for " + latestSelfieUri);
	    }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		SelfieRecord selfie = (SelfieRecord) listAdapter.getItem(position);
//		Intent detailIntent = new Intent(this, SelfieDetailActivity.class);
//		detailIntent.putExtra( SelfieDetailActivity.IMAGE_FILE_URI, 
//								selfie.getFullImageFile() );
//		startActivity(detailIntent);
		
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile( selfie.getFullImageFile() ), 
							"image/jpg");
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.d(LOG_TAG, "Selected menu item " + item);
		int id = item.getItemId();
		Log.d(LOG_TAG, "Item id: " + id);
		switch (id) {
			case (R.id.take_selfie):
				takeSelfie();
				return true;
			case (R.id.action_settings):
				startActivity( new Intent(this, SettingsActivity.class) );
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class ImageLoadTask extends AsyncTask<Void, Void, Void> {
		
	    @Override
	    protected void onPreExecute() {
	        progressLayout.setVisibility(View.VISIBLE);
	        listView.setVisibility(View.GONE);
	        super.onPreExecute();
	    }

		@Override
        protected Void doInBackground(Void... params) {
			listAdapter = 
					new SelfieListViewAdapter( selfieListFromStorageDir(), 
												getLayoutInflater() );
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					setListAdapter(listAdapter);
				}
			};
			listView.post(runnable);
			
			return null;
        }
		
	    @Override
	    protected void onPostExecute(Void result) {
	    	progressLayout.setVisibility(View.GONE);
	        listView.setVisibility(View.VISIBLE);
	        super.onPostExecute(result);
	    }
	}
}
