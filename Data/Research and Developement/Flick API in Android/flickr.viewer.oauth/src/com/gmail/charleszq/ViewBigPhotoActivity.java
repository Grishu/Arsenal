/*
 * Created on Aug 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gmail.charleszq.actions.SaveImageWallpaperAction;
import com.gmail.charleszq.event.IImageDownloadDoneListener;
import com.gmail.charleszq.task.ImageDownloadTask;
import com.gmail.charleszq.task.ImageDownloadTask.ParamType;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.photos.Photo;

/**
 * Represents the activity to view the big photo.
 * <p>
 * The intent of this activity must have a photo id in it.
 * 
 * @author charles
 */
public class ViewBigPhotoActivity extends Activity implements OnTouchListener,
		IImageDownloadDoneListener {

	private static final Logger logger = LoggerFactory
			.getLogger("ViewBigPhotoActivity"); //$NON-NLS-1$

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	/**
	 * The photo id key to save photo id in intent or saved bundle.
	 */
	public static final String PHOTO_ID_KEY = "p.id"; //$NON-NLS-1$
	public static final String PHOTO_SECRET_KEY = "p.sec"; //$NON-NLS-1$

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private int mode = NONE;

	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	// Hide titlebar.
	private boolean hideTitleBar = false;

	// Gesture Handler
	protected GestureListener mGestureListener;
	protected ScaleListener mScaleListener;
	protected ScaleGestureDetector mScaleDetector;
	protected GestureDetector mGestureDetector;
	
	/**
	 * The current photo.
	 */
	private Photo mPhoto;

	/**
	 * The photo id comes from the intent extra.
	 */
	private String mPhotoId;

	/**
	 * The photo secret.
	 */
	private String mPhotoSecret;

	/**
	 * The progress bar.
	 */
	private ProgressBar mProgressBar;

	/**
	 * The image view.
	 */
	private ImageView mImageView;

	/**
	 * the bitmap of the big photo.
	 */
	private Bitmap mPhotoBitmap = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sp = getSharedPreferences(Constants.DEF_PREF_NAME,
				Context.MODE_APPEND);
		hideTitleBar = sp.getBoolean(Constants.SETTING_HIDE_PREVIEW_TITLEBAR, false);		
		
		if (hideTitleBar)
		{
			// Remove Title Bar
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
        
		setContentView(R.layout.view_big_img);

		if (savedInstanceState != null) {
			mPhotoId = savedInstanceState.getString(PHOTO_ID_KEY);
			mPhotoSecret = savedInstanceState.getString(PHOTO_SECRET_KEY);
		} else {
			Intent intent = getIntent();
			mPhotoId = intent.getExtras().getString(PHOTO_ID_KEY);
			mPhotoSecret = intent.getExtras().getString(PHOTO_SECRET_KEY);
		}
		mPhoto = new Photo();
		mPhoto.setId(mPhotoId);
		mPhoto.setSecret(mPhotoSecret);

		mProgressBar = (ProgressBar) findViewById(R.id.progress);
		mImageView = (ImageView) findViewById(R.id.big_image);
		mImageView.setOnTouchListener(this);

		// Initialize listeners
		mGestureListener = new GestureListener();
		mScaleListener = new ScaleListener();
		
		// Initialize detectors
		mScaleDetector = new ScaleGestureDetector(mImageView.getContext(), mScaleListener);
		mGestureDetector = new GestureDetector(mImageView.getContext(), mGestureListener, null, true);
		
		//mImageView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		if (!hideTitleBar)
		{
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		if (mPhotoId != null) {
			File root = new File(Environment.getExternalStorageDirectory(),
					Constants.SD_CARD_FOLDER_NAME);
			File imageFile = new File(root, mPhotoId + ".jpg"); //$NON-NLS-1$
			if (imageFile.exists()) {
				try {
					Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(
							imageFile));
					onImageDownloaded(bm);
				} catch (FileNotFoundException e) {
				}
			} else {
				ImageDownloadTask task = new ImageDownloadTask(mImageView,
						ParamType.PHOTO_ID_LARGE, this);
				task.execute(mPhotoId, mPhotoSecret);
			}
		} else {
			mProgressBar.setVisibility(View.GONE);
			Toast.makeText(this, getString(R.string.error_get_big_image),
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gmail.charleszq.event.IImageDownloadDoneListener#onImageDownloaded
	 * (android.graphics.Bitmap)
	 */
	@Override
	public void onImageDownloaded(Bitmap bitmap) {
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.GONE);
		}
		if (bitmap == null) {
			Toast.makeText(this, getString(R.string.error_download_big_photo),
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		this.mPhotoBitmap = bitmap;
		if (mImageView != null) {
			mImageView.setImageBitmap(mPhotoBitmap);
			fitToScreen();
			center(true, true);
		}
	}

	@Override
	protected void onDestroy() {
		if (mPhotoBitmap != null) {
			mPhotoBitmap.recycle();
			mPhotoBitmap = null;
			logger.debug("Bitmap released."); //$NON-NLS-1$
		}
		mPhoto = null;
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PHOTO_ID_KEY, mPhotoId);
		outState.putString(PHOTO_SECRET_KEY, mPhotoSecret);
		logger.debug(
				"Photo id={} and photoSecret={} saved", mPhotoId, mPhotoSecret); //$NON-NLS-1$
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mScaleDetector.onTouchEvent(event);
		if (!mScaleDetector.isInProgress()) mGestureDetector.onTouchEvent(event);
		
		ImageView view = (ImageView) v;

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			logger.debug("mode=DRAG"); //$NON-NLS-1$
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			logger.debug("oldDist={}", oldDist); //$NON-NLS-1$
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				logger.debug("mode=ZOOM"); //$NON-NLS-1$
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			logger.debug("mode=NONE"); //$NON-NLS-1$
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				logger.debug("newDist={}", newDist); //$NON-NLS-1$
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		center(true, true);
		return true; // indicate event was handled
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu_view_big_image, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mPhoto == null) {
			return super.onOptionsItemSelected(item);
		}

		switch (item.getItemId()) {
		case R.id.menu_item_save:
			SaveImageWallpaperAction action = new SaveImageWallpaperAction(
					this, mPhoto);
			action.execute();
			return true;
		case R.id.menu_item_wallpaper:
			SaveImageWallpaperAction wallAction = new SaveImageWallpaperAction(
					this, mPhoto, true);
			wallAction.execute();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// Center Photo to ImageView
	// 2012/04/17 Luca Vettoretto 
	private void center(boolean horizontal, boolean vertical) {
	    if (mPhotoBitmap == null) {
	        return;
	    }

	    RectF rect = new RectF(0, 0,
	    		mPhotoBitmap.getWidth(),
	    		mPhotoBitmap.getHeight());

	    matrix.mapRect(rect);

	    float height = rect.height();
	    float width  = rect.width();

	    float deltaX = 0, deltaY = 0;

	    if (vertical) {
	        int viewHeight = mImageView.getHeight();
	        if (height < viewHeight) {
	            deltaY = (viewHeight - height) / 2 - rect.top;
	        } else if (rect.top > 0) {
	            deltaY = -rect.top;
	        } else if (rect.bottom < viewHeight) {
	            deltaY = mImageView.getHeight() - rect.bottom;
	        }
	    }

	    if (horizontal) {
	        int viewWidth = mImageView.getWidth();
	        if (width < viewWidth) {
	            deltaX = (viewWidth - width) / 2 - rect.left;
	        } else if (rect.left > 0) {
	            deltaX = -rect.left;
	        } else if (rect.right < viewWidth) {
	            deltaX = viewWidth - rect.right;
	        }
	    }

	    matrix.postTranslate(deltaX, deltaY);
	    mImageView.setImageMatrix(matrix);
	}	

	// Fit Photo to ImageView
	// 2012/04/19 Luca Vettoretto 
	private void fitToScreen(){
		if (mPhotoBitmap == null) {
	        return;
	    }
		
		RectF rect = new RectF(0, 0,
	    		mPhotoBitmap.getWidth(),
	    		mPhotoBitmap.getHeight());
		matrix.mapRect(rect);
	    float bmpHeight = rect.height();
	    float bmpWidth  = rect.width();
	    
        int viewHeight = mImageView.getHeight();
        int viewWidth = mImageView.getWidth();

        float scaleX = viewWidth / (float)bmpWidth;
        float scaleY = viewHeight / (float)bmpHeight;
        
        float minScale = Math.min(scaleX, scaleY);
        
		matrix.postScale(minScale, minScale, mid.x, mid.y);
	    mImageView.setImageMatrix(matrix);
	}
	
	// Gesture Listener Class
	// 2012/04/20 Luca Vettoretto 
	class GestureListener extends GestureDetector.SimpleOnGestureListener 
	{

		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			logger.debug("onDoubleTap");
			fitToScreen();
			center(true, true);
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			logger.debug("onScroll");
			return true;
		}
		
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
		{
			logger.debug("onFling");
			return true;
		}	
	}
	
	// Scale Listener Class
	// 2012/04/20 Luca Vettoretto 
	class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			logger.debug("onScale");
			return true;
		}
		
	}
	
}
