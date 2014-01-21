package com.test.flickr.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

import com.test.flickr.R;
import com.test.flickr.data.FetchAlbums;
import com.test.flickr.data.FetchPhotos;
import com.test.flickr.utils.MyGestureDetector;

public class FlickrActivity extends Activity {

	public static final String API_KEY = "32f767495c207c061d12bf61d72e0b75";// "da4fadd0084ea1799ad33048f0d6a5c5";
	public static final String USER_ID = "112277956";

	private boolean exit = true;
	private Photos thePhotoList;
	private int photoNum = 0;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;

	/*
	 * DemoTest Key: 871805936c074bf3b299789e95bb38ce Secret: ee52457ad5fe930c
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new Albums().execute();
	}

	private class Albums extends FetchAlbums {
		@Override
		public void onPhotoCategoryClick(String id) {
			setContentView(R.layout.pics);
			exit = false;
			try {
				((ViewAnimator) findViewById(R.id.PictureAnimator))
						.removeAllViews();
			} catch (Exception e) {
			}
			String url = "http://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos&format=json&api_key="
					+ API_KEY + "&photoset_id=" + USER_ID;
			thePhotoList = new Photos(url);
			thePhotoList.execute();

			photoNum = 0;

			gestureDetector = new GestureDetector(new TheGestureDetector());
			gestureListener = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					return gestureDetector.onTouchEvent(event);
				}
			};
			((ViewAnimator) FlickrActivity.this
					.findViewById(R.id.PictureAnimator))
					.setOnTouchListener(gestureListener);

		}

		public Albums() {
			super(FlickrActivity.this);
		}
	}

	private class Photos extends FetchPhotos {
		@Override
		public void onFetchError() {
		}

		public Photos(String url) {
			super(FlickrActivity.this, url);
		}
	}

	private class TheGestureDetector extends MyGestureDetector {

		@Override
		public void rightToLeft() {
			photoNum++;
			if (photoNum == ((ViewAnimator) findViewById(R.id.PictureAnimator))
					.getChildCount()) {
				photoNum = 0;
			}
			thePhotoList.LoadPhoto(thePhotoList.thePics.get(photoNum));
			((ViewAnimator) findViewById(R.id.PictureAnimator)).showNext();
		}

		@Override
		public void leftToRight() {
			photoNum--;
			thePhotoList.LoadPhoto(thePhotoList.thePics.get(photoNum));
			((ViewAnimator) findViewById(R.id.PictureAnimator)).showPrevious();
		}

	}

	@Override
	public void onBackPressed() {
		if (exit) {
			super.onBackPressed();
		} else {
			setContentView(R.layout.main);
			((LinearLayout) (FlickrActivity.this)
					.findViewById(R.id.llTaPantaOla)).removeAllViews();
			new Albums().execute();
			exit = true;
		}
	}

}