package com.example.signaturedemo;

import java.io.File;
import java.io.FileOutputStream;
import com.example.signaturedemo.ColorPickerDialog.OnColorChangedListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * This class shows how we can do Calligraphy sign using View, Paint, Path, and
 * Canvas classes.
 * 
 */

public class Drawing_Activity extends Activity {

	RelativeLayout m_rlContent;
	Signature m_signature;
	Button m_btnClear, m_btnGetSign;
	public static String m_tempDir;
	private Bitmap m_bitmap;
	View m_view;
	int mColor = 0xFFFF0000;
	private Paint m_paint = new Paint();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		m_tempDir = Environment.getExternalStorageDirectory() + "/"
				+ "Signature" + "/";
		prepareDirectory();
		Toast.makeText(this, "Press Menu for More options.", Toast.LENGTH_LONG)
				.show();
		m_rlContent = (RelativeLayout) findViewById(R.id.mnllMain);
		m_signature = new Signature(this, null);
		m_signature.setBackgroundColor(Color.WHITE);
		m_rlContent.addView(m_signature, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		m_btnClear = (Button) findViewById(R.id.btnClear);
		m_btnGetSign = (Button) findViewById(R.id.btnsubmit);
		m_view = m_rlContent;

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, 1, 0, "save");

		menu.add(0, 2, 0, "clear");
		menu.add(0, 3, 0, "Color");
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Log.v("log_tag", "Panel Saved");
			m_view.setDrawingCacheEnabled(true);
			m_signature.save(m_view);
			m_signature.clear();
			break;
		case 2:
			Log.v("log_tag", "Panel Cleared");
			m_signature.clear();
			break;
		case 3:
			//Pick new color from color picker.
			new ColorPickerDialog(this, new OnColorChangedListener() {

				public void colorChanged(int color) {
					// TODO Auto-generated method stub
					m_paint.setColor(color);
					mColor = color;
				}
			}, 0xFFFF0000).show();
			break;
		}
		return true;
	}

	/**
	 * This method will check if SDCard is available or not.
	 * 
	 * @return-true or falses
	 */
	private boolean prepareDirectory() {
		try {
			if (makedirs()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(
					this,
					"Could not initiate File System.. Is Sdcard mounted properly?",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * Will make directory.
	 * 
	 * @return-true or false.
	 */

	private boolean makedirs() {
		File m_tempdir = new File(m_tempDir);
		if (!m_tempdir.exists())
			m_tempdir.mkdirs();

		if (m_tempdir.isDirectory()) {
			File[] files = m_tempdir.listFiles();
			for (File file : files) {
				if (!file.delete()) {
					System.out.println("Failed to delete " + file);
				}
			}
		}
		return (m_tempdir.isDirectory());
	}

	/**
	 * This class is used as View which will be set in Activity. This is used to
	 * draw signature on Canvas by drawing line on User's touchEvent.
	 * 
	 */

	public class Signature extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

		private Path m_path = new Path();

		private float m_lastTouchX;
		private float m_lastTouchY;
		private final RectF m_dirtyRect = new RectF();

		public Signature(Context context, AttributeSet attrs) {
			super(context, attrs);
			m_paint.setAntiAlias(true);
			new ColorPickerDialog(context, new OnColorChangedListener() {

				public void colorChanged(int color) {
					// TODO Auto-generated method stub
					m_paint.setColor(color);
					mColor = color;
				}
			}, 0xFFFF0000).show();

			// m_paint.setColor(Color.RED);
			m_paint.setStyle(Paint.Style.STROKE);
			m_paint.setStrokeJoin(Paint.Join.ROUND);
			m_paint.setStrokeWidth(STROKE_WIDTH);
		}

		/**
		 * Method save the image into sdcard by capturing it using drawingcacheenable mode and converting into bitmap.
		 * @param v-View to capture.
		 */
		public void save(View v) {
			Log.v("log_tag", "Width: " + v.getWidth());
			Log.v("log_tag", "Height: " + v.getHeight());

			if (m_bitmap == null) {
				m_bitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.RGB_565);
			}
			Canvas m_canvas = new Canvas(m_bitmap);
			String m_FtoSave = m_tempDir + System.currentTimeMillis() + ".png";
			System.err.println("File Name is---->" + m_FtoSave);
			Toast.makeText(Drawing_Activity.this,
					"File Saved At-->" + m_FtoSave, Toast.LENGTH_LONG).show();
			File m_file = new File(m_FtoSave);
			try {
				FileOutputStream m_FileOutStream = new FileOutputStream(m_file);
				v.draw(m_canvas);
				m_bitmap.compress(Bitmap.CompressFormat.PNG, 90,
						m_FileOutStream);

				m_FileOutStream.flush();
				m_FileOutStream.close();
				String m_url = Images.Media.insertImage(getContentResolver(),
						m_bitmap, "title", null);
				Log.v("log_tag", "url" + m_url);

			} catch (Exception e) {
				Log.v("log_tag", e.toString());
			}
		}

		/**
		 * This method clears the canvas.
		 */
		public void clear() {
			m_path.reset();
			invalidate();
		}

		/**
		 * Draw a line on canvas.
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(m_path, m_paint);
		}

		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float m_eventX = event.getX();
			float m_eventY = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				m_path.moveTo(m_eventX, m_eventY);
				m_lastTouchX = m_eventX;
				m_lastTouchY = m_eventY;
				return true;

			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:
				resetDirtyRect(m_eventX, m_eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					m_path.lineTo(historicalX, historicalY);
				}
				m_path.lineTo(m_eventX, m_eventY);
				break;

			default:
				debug("Ignored touch event: " + event.toString());
				return false;
			}

			invalidate((int) (m_dirtyRect.left - HALF_STROKE_WIDTH),
					(int) (m_dirtyRect.top - HALF_STROKE_WIDTH),
					(int) (m_dirtyRect.right + HALF_STROKE_WIDTH),
					(int) (m_dirtyRect.bottom + HALF_STROKE_WIDTH));

			m_lastTouchX = m_eventX;
			m_lastTouchY = m_eventY;

			return true;
		}

		private void debug(String string) {
		}

		/**
		 * This method checks the earlier rectangle.
		 * 
		 * @param historicalX
		 * @param historicalY
		 */
		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < m_dirtyRect.left) {
				m_dirtyRect.left = historicalX;
			} else if (historicalX > m_dirtyRect.right) {
				m_dirtyRect.right = historicalX;
			}

			if (historicalY < m_dirtyRect.top) {
				m_dirtyRect.top = historicalY;
			} else if (historicalY > m_dirtyRect.bottom) {
				m_dirtyRect.bottom = historicalY;
			}
		}

		/**
		 * This method resets rectangle.
		 * 
		 * @param eventX
		 * @param eventY
		 */
		private void resetDirtyRect(float eventX, float eventY) {
			m_dirtyRect.left = Math.min(m_lastTouchX, eventX);
			m_dirtyRect.right = Math.max(m_lastTouchX, eventX);
			m_dirtyRect.top = Math.min(m_lastTouchY, eventY);
			m_dirtyRect.bottom = Math.max(m_lastTouchY, eventY);
		}
	}
}
