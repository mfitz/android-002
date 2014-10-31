package com.michaelfitzmaurice.android.modernart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	
	private static final String TAG = "Lab-ModernArtUi";
	
	List<View> mutableViews = new ArrayList<View>();
	SeekBar seekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View view4 = findViewById(R.id.LinearLayout4);
		view4.setBackgroundColor(0xFF5500AA);
		mutableViews.add(view4);
		
		View view5 = findViewById(R.id.LinearLayout5);
		view5.setBackgroundColor(0xFF880044);
		mutableViews.add(view5);
		
		View view6 = findViewById(R.id.LinearLayout6);
		view6.setBackgroundColor(0xFFFF0000);
		mutableViews.add(view6);
		
		View view7 = findViewById(R.id.LinearLayout7);
		view7.setBackgroundColor(0xFFFFFFFF);
		
		View view8 = findViewById(R.id.LinearLayout8);
		view8.setBackgroundColor(0xFF0000FF);
		mutableViews.add(view8);
		
		seekBar = (SeekBar)findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// do nothing
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// do nothing
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, 
										int progress,
										boolean fromUser) {
				Log.d(TAG, "SeekBar moved to " + progress);
				modifyMutableViewColours(progress);
			}
		});
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
		int id = item.getItemId();
		if (id == R.id.action_more_info) {
			showMoreInfoDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void modifyMutableViewColours(int greenValue) {
		
		for (View view : mutableViews) {
			ColorDrawable bg = (ColorDrawable) view.getBackground();
			int color = bg.getColor();
			int red = Color.red(color);
			int green = Color.green(color);
			int blue = Color.blue(color);
			Log.d(TAG, "Starting RGB values for view " + view.getId() 
					+ " : " + red + "," + green + "," + blue);
			int newColour = Color.rgb(red, greenValue, blue);
			view.setBackgroundColor(newColour);
		}
	}
	
	private void showMoreInfoDialog() {
		
		AlertDialog.Builder alertDialogBuilder = 
			new AlertDialog.Builder(this);
		alertDialogBuilder
			.setMessage(R.string.more_info_text)
			.setCancelable(false)
			.setPositiveButton(R.string.more_info_learn_more, 
							new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					openMoreInfoLink();
				}
			})
			.setNegativeButton(R.string.more_info_cancel,
							new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
		alertDialogBuilder.create().show();
	}
	
	private void openMoreInfoLink() {
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse( getString(R.string.more_info_url) );
		Log.d(TAG, "Opening link to " + uri + " ...");
		intent.setData(uri);
		startActivity(intent);
	}
}
