package com.michaelfitzmaurice.android.modernart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	List<View> mutableViews = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View bg = findViewById(R.id.LinearLayout1);
		bg.setBackgroundColor(Color.BLACK);
		
		View view4 = findViewById(R.id.LinearLayout4);
		view4.setBackgroundColor(0xFF00FF00);
		mutableViews.add(view4);
		
		View view5 = findViewById(R.id.LinearLayout5);
		view5.setBackgroundColor(0xFFFF0000);
		mutableViews.add(view5);
		
		View view6 = findViewById(R.id.LinearLayout6);
		view6.setBackgroundColor(0xFF0000FF);
		mutableViews.add(view6);
		
		View view7 = findViewById(R.id.LinearLayout7);
		view7.setBackgroundColor(0xFFFFFFFF);
		
		View view8 = findViewById(R.id.LinearLayout8);
		view8.setBackgroundColor(0xFF00FFFF);
		mutableViews.add(view8);
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
