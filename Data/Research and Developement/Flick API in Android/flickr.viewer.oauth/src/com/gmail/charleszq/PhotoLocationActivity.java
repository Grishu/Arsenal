/*
 * Created on Aug 23, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq;

import java.lang.ref.WeakReference;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;

import com.gmail.charleszq.task.GetPhotoImageTask;
import com.gmail.charleszq.task.GetPhotoImageTask.IPhotoFetchedListener;
import com.gmail.charleszq.task.GetPhotoImageTask.PhotoType;
import com.gmail.charleszq.utils.ImageUtils;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Represents the activity to show the map view.
 * 
 * @author charles
 * 
 */
public class PhotoLocationActivity extends MapActivity implements
		IPhotoFetchedListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PhotoLocationActivity.class);
	private static final int INVALID_LAT_LNG_VAL = (int) (360 * 1E6);

	public static final String LAT_VAL = "lat"; //$NON-NLS-1$
	public static final String LONG_VAL = "long"; //$NON-NLS-1$
	public static final String PHOTO_ID = "photo.id"; //$NON-NLS-1$
	public static final String PHOTO_SECRET = "photo.secret"; //$NON-NLS-1$

	/**
	 * The zoom level of the map view.
	 */
	private int mZoomLevel = 15;

	/**
	 * The map view.
	 */
	private MapView mMapView;

	/**
	 * The photo location information.
	 */
	private int mLatitude = INVALID_LAT_LNG_VAL,
			mLongtitude = INVALID_LAT_LNG_VAL;

	/**
	 * the cache geo point instance.
	 */
	private GeoPoint mPhotoGeoPoint = null;

	/**
	 * The weak reference to store the photo bitmap.
	 */
	private WeakReference<Bitmap> mPhotoBitmapRef = null;

	/**
	 * The photo id.
	 */
	private String mPhotoId = null;

	private String mPhotoSecret = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onDestroy() {
		if (mPhotoBitmapRef != null) {
			Bitmap bm = mPhotoBitmapRef.get();
			if (bm != null) {
				bm.recycle();
				bm = null;
				logger.debug("Destroy the bitmap reference."); //$NON-NLS-1$
			}
		}
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.photo_map);
		Intent intent = getIntent();

		if (icicle != null) {
			mLatitude = icicle.getInt(LAT_VAL);
			mLongtitude = icicle.getInt(LONG_VAL);
			mPhotoId = icicle.getString(PHOTO_ID);
			mPhotoSecret = icicle.getString(PHOTO_SECRET);
		} else {
			mLatitude = intent.getExtras().getInt(LAT_VAL);
			mLongtitude = intent.getExtras().getInt(LONG_VAL);
			mPhotoId = intent.getExtras().getString(PHOTO_ID);
			mPhotoSecret = intent.getExtras().getString(PHOTO_SECRET);

		}

		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true);

		MapController mc = mMapView.getController();
		mPhotoGeoPoint = new GeoPoint(mLatitude, mLongtitude);

		mc.animateTo(mPhotoGeoPoint);
		mc.setZoom(mZoomLevel);

		redrawPushpin();
		getPhotoImage();
	}

	/**
	 * Get the photo image.
	 */
	private void getPhotoImage() {
		if (mPhotoId == null) {
			return;
		}

		// use 'null' as the message not shows the progress dialog.
		GetPhotoImageTask task = new GetPhotoImageTask(this,
				PhotoType.SMALL_URL, this, null);
		task.setCacheImageScale(0.25f);
		task.execute(mPhotoId, mPhotoSecret);
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		if (mMapView != null) {
			int zoomLevel = mMapView.getZoomLevel();
			if (zoomLevel != mZoomLevel) {
				redrawPushpin();
				drawPhotoLayer();
				logger.debug("Map view zoom level changed from {} to {}", //$NON-NLS-1$
						mZoomLevel, zoomLevel);
				mZoomLevel = zoomLevel;
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(LAT_VAL, mLatitude);
		outState.putInt(LONG_VAL, mLongtitude);
		if (mPhotoId != null) {
			outState.putString(PHOTO_ID, mPhotoId);
		}
	}

	/**
	 * Redraws the push pin on the map view.
	 */
	private void redrawPushpin() {
		if (mPhotoGeoPoint == null) {
			if (mLatitude != INVALID_LAT_LNG_VAL
					&& mLongtitude != INVALID_LAT_LNG_VAL)
				mPhotoGeoPoint = new GeoPoint(mLatitude, mLongtitude);
		}

		if (mPhotoGeoPoint == null) {
			return;
		}

		MapOverlay mapOverlay = new MapOverlay(this, mPhotoGeoPoint,
				R.drawable.pushpin);
		List<Overlay> listOfOverlays = mMapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mMapView.invalidate();
	}

	private static class MapOverlay extends com.google.android.maps.Overlay {

		private GeoPoint mPosition;
		private Context mContext;
		private int mImageRes = -1;
		private Bitmap mBitmap = null;

		private int mOffsetX = 0;
		private int mOffsetY = 0;
		private float mScaleFactor = 0.3f;

		MapOverlay(Context context, GeoPoint p, int res) {
			this.mPosition = p;
			this.mContext = context;
			this.mImageRes = res;
		}

		MapOverlay(Context context, GeoPoint p, Bitmap bitmap) {
			this.mPosition = p;
			this.mContext = context;
			this.mBitmap = bitmap;
		}

		void setOffsetX(int offsetX, int offsetY, float scale) {
			this.mOffsetX = offsetX;
			this.mOffsetY = offsetY;
			this.mScaleFactor = scale;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();

			if (mPosition == null) {
				screenPts.x = screenPts.y = 0;
			} else {
				mapView.getProjection().toPixels(mPosition, screenPts);
			}

			// ---add the marker---
			Bitmap bmp = mBitmap;
			if (mImageRes != -1) {
				bmp = BitmapFactory.decodeResource(mContext.getResources(),
						mImageRes);
			}
			Bitmap resizedBitmap = ImageUtils.resize(bmp, mScaleFactor);

			// 90 = image_org_height * 0.3
			canvas.drawBitmap(resizedBitmap, screenPts.x + mOffsetX,
					screenPts.y - 90 + mOffsetY, null);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.task.GetPhotoImageTask.IPhotoFetchedListener#
	 * onPhotoFetched(com.aetrion.flickr.photos.Photo, android.graphics.Bitmap)
	 */
	@Override
	public void onPhotoFetched(Photo photo, Bitmap bitmap) {
		if (photo == null) {
			return;
		}
		mPhotoId = photo.getId();
		mPhotoSecret = photo.getSecret();
		mPhotoBitmapRef = new WeakReference<Bitmap>(bitmap);
		drawPhotoLayer();
	}

	/**
	 * Draws the photo on the map.
	 */
	private void drawPhotoLayer() {
		if (mPhotoBitmapRef == null || mPhotoBitmapRef.get() == null
				|| mMapView == null) {
			return;
		}

		Bitmap photoBitmap = mPhotoBitmapRef.get();

		if (mPhotoGeoPoint == null) {
			if (mLatitude != INVALID_LAT_LNG_VAL
					&& mLongtitude != INVALID_LAT_LNG_VAL)
				mPhotoGeoPoint = new GeoPoint(mLatitude, mLongtitude);
		}

		if (mPhotoGeoPoint == null) {
			return;
		}

		MapOverlay mapOverlay = new MapOverlay(this, mPhotoGeoPoint,
				photoBitmap);
		mapOverlay.setOffsetX(50, -50, 1.0f);
		List<Overlay> listOfOverlays = mMapView.getOverlays();
		listOfOverlays.add(mapOverlay);

		mMapView.invalidate();
	}

}
