package com.michaelfitzmaurice.dailyselfie;

import java.util.Date;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfieListViewAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	
	private List<SelfieRecord> selfieList;
	
	public SelfieListViewAdapter(List<SelfieRecord> selfieList, 
								LayoutInflater inflater) {
		this.selfieList = selfieList;
		this.layoutInflater = inflater;
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
		
		viewHolder.thumbnail.setImageBitmap( selfie.getThumbnail() );
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
	
	static class ViewHolder {
		ImageView thumbnail;
		TextView creationDate;
	}

}
