package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class SelfieDetailActivity extends Activity {
	
	public static final String IMAGE_FILE_URI = "image-file-uri";  
	
	private File imageFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selfie_detail_view);
		Intent intent = getIntent();
		if (intent != null) {
			Bundle data = intent.getExtras();
			if (data != null) {
				imageFile = (File) data.get(IMAGE_FILE_URI);
				Log.d(LOG_TAG, "Showing detail for image at " + imageFile);
			}
		}
		
		ImageView imageView = (ImageView) findViewById(R.id.selfie_detail);
		int viewWidth = Math.max(imageView.getWidth(), 300);
		int viewHeight = Math.max(imageView.getHeight(), 300);
		Log.d(LOG_TAG, "Scaling image to width and height " 
						+ viewWidth + ", " + viewHeight);
		Bitmap image = getScaledBitMap(imageFile, viewWidth, viewHeight );
		imageView.setImageBitmap(image);
	}
	
	private Bitmap getScaledBitMap(File imageFile, 
									int targetWidth, 
									int targetHeight) {

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetWidth, photoH/targetHeight);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), 
	    								bmOptions);
	}
}
