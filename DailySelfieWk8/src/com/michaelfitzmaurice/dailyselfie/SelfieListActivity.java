package com.michaelfitzmaurice.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SelfieListActivity extends ListActivity {
	
	public static final String LOG_TAG = "DailySelfie";
	
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	
	private SelfieListViewAdapter listAdapter;
	private Uri latestSelfieUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		listAdapter = new SelfieListViewAdapter( getApplicationContext() );
		setListAdapter(listAdapter);
	}
	
	private void takeSelfie() {
		Log.d(LOG_TAG, "Taking a new selfie...");
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	    	try {
	    		latestSelfieUri = imageFileUri();
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
	
	private Uri imageFileUri() throws IOException {
		
	    String timeStamp = 
	    	new SimpleDateFormat( "yyyyMMdd_HHmmss").format(new Date() );
	    String imageFileName = "selfie_" + timeStamp + "_";
	    File storageDir = 
	    		Environment.getExternalStoragePublicDirectory(
	    				Environment.DIRECTORY_PICTURES);
	    File imageFile = 
	    		File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
//	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    return Uri.fromFile(imageFile);
	}
	
	@Override
	protected void onActivityResult(int requestCode, 
									int resultCode, 
									Intent data) {
		
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//	        Bundle extras = data.getExtras();
//	        Bitmap thumbnail = (Bitmap) extras.get("data");
//	        Log.d(LOG_TAG, "Camera gave us a new thumbnail: " + thumbnail);
//	        Uri imageFileUri = 
//	        	(Uri) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
//	        Log.d(LOG_TAG, "Picture should now exist at " + imageFileUri);
	        File imageFile = new File( latestSelfieUri.getPath() );
//	        File imageFile = new File()
	        SelfieRecord newSelfie = 
	        	new SelfieRecord(makeThumbnail(imageFile), imageFile);
	        listAdapter.add(newSelfie);
	        Log.d(LOG_TAG, "Added new selfie for " + latestSelfieUri);
	    }
	}
	
	private Bitmap makeThumbnail(File imageFile) {
		
		return ThumbnailUtils.extractThumbnail(
					BitmapFactory.decodeFile( imageFile.getPath() ), 
					12, 
					12);
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
