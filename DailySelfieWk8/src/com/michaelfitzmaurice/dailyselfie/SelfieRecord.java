package com.michaelfitzmaurice.dailyselfie;

import java.io.File;

import android.graphics.Bitmap;

public class SelfieRecord {
	
	private Bitmap thumbnail;
	private File fullImageFile;
	
	public SelfieRecord(Bitmap thumbnail, File fullImageFile) {
		super();
		this.thumbnail = thumbnail;
		this.fullImageFile = fullImageFile;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public File getFullImageFile() {
		return fullImageFile;
	}

}
