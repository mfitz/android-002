package com.michaelfitzmaurice.dailyselfie;

import static com.michaelfitzmaurice.dailyselfie.SelfieListActivity.LOG_TAG;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfieListViewAdapter extends BaseAdapter {

	private static LayoutInflater layoutInflater;
	
	private List<SelfieRecord> selfieList;

	public SelfieListViewAdapter(Context context) {
		this.selfieList = new ArrayList<SelfieRecord>();
		layoutInflater = LayoutInflater.from(context);
		// TODO - make this a background task to free up the UI thread
		populateSelfieListFromStorageDir();
	}

	private void populateSelfieListFromStorageDir() {
		
		// TODO - use file date comparator to sort by mod date
		File storageDir = SelfieListActivity.STORAGE_DIRECTORY;
		Log.d(LOG_TAG, "Looking for existing selfies in " + storageDir);
		File[] files = storageDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			Log.d(LOG_TAG, "Found selfie at " + file);
			SelfieRecord selfie = new SelfieRecord(makeThumbnail(file), file);
			selfieList.add(selfie);
		}
	}

	@Override
	public int getCount() {
		return selfieList.size();
	}

	@Override
	public Object getItem(int position) {
		return selfieList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View newView = convertView;
		ViewHolder viewHolder;
		
		SelfieRecord selfie = selfieList.get(position);
		
		if (null == convertView) {
			viewHolder = new ViewHolder();
			newView = 
				layoutInflater.inflate(R.layout.selfie_record_view, null);
			viewHolder.thumbnail = 
				(ImageView) newView.findViewById(R.id.thumbnail);
			viewHolder.creationDate = 
				(TextView) newView.findViewById(R.id.creation_date);
			newView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) newView.getTag();
		}
		
		viewHolder.thumbnail.setImageBitmap( 
			makeThumbnail(selfie.getFullImageFile() ) );
		String selfieDate = 
			new Date( selfie.getFullImageFile().lastModified() ).toString();
		viewHolder.creationDate.setText(selfieDate);
		
		return newView;
	}
	
	public void add(SelfieRecord listItem) {
		selfieList.add(listItem);
		notifyDataSetChanged();
	}
	
	public void remove(SelfieRecord listItem) {
		selfieList.remove(listItem);
		notifyDataSetChanged();
	}
	
	private Bitmap makeThumbnail(File imageFile) {
		
		return ThumbnailUtils.extractThumbnail(
					BitmapFactory.decodeFile( imageFile.getPath() ), 
					80, 
					80 );
	}
	
	static class ViewHolder {
		ImageView thumbnail;
		TextView creationDate;
	}

}
