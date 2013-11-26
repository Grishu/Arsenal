package com.fb.fbdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button btn1, btn2;
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me?fields=id,name,picture&access_token=";
	private static final String URL_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);

		btn1.setOnClickListener(m_click);
		btn2.setOnClickListener(m_click);
	}

	OnClickListener m_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				startActivity(new Intent(getApplicationContext(),
						LoginUsingActivityActivity.class).putExtra("url",
						URL_PREFIX_FRIENDS));
				break;
			case R.id.button2:
				startActivity(new Intent(getApplicationContext(),
						LoginUsingActivityActivity.class).putExtra("url",
						URL_FRIENDS));
				break;

			default:
				break;
			}

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
