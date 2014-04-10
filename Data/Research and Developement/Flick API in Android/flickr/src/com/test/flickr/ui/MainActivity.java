package com.test.flickr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.test.flickr.R;

public class MainActivity extends Activity {

	private Button m_btn1, m_btn2, m_btn3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stubl
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);
		m_btn1 = (Button) findViewById(R.id.button1);
		m_btn2 = (Button) findViewById(R.id.button2);
		m_btn3 = (Button) findViewById(R.id.button3);

		m_btn1.setOnClickListener(m_ClickListener);
		m_btn2.setOnClickListener(m_ClickListener);
		m_btn3.setOnClickListener(m_ClickListener);
	}

	OnClickListener m_ClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				startActivity(new Intent(MainActivity.this,
						SimpleAndroidFlickrActivity.class));
				break;

			case R.id.button2:

				startActivity(new Intent(MainActivity.this,
						AndroidFlickrActivity.class));
				break;
			case R.id.button3:

				startActivity(new Intent(MainActivity.this,
						FlickrPhotoGallery.class));
				break;
			default:
				break;
			}
		}
	};
}
