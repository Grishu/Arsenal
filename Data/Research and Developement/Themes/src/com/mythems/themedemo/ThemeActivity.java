package com.mythems.themedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ThemeActivity extends Activity implements OnClickListener
{
	
	private Button m_btnBlueTheme, m_btnBlackTheme, m_btnPinkTheme;
	private Intent m_intent;
	public static int m_nThemeId;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme_layout);
		m_btnBlueTheme = (Button) findViewById(R.id.thbtnBlueTheme);
		m_btnBlackTheme = (Button) findViewById(R.id.thbtnBlackTheme);
		m_btnPinkTheme = (Button) findViewById(R.id.thbtnPinkTheme);
		
		m_btnBlueTheme.setOnClickListener(this);
		m_btnBlackTheme.setOnClickListener(this);
		m_btnPinkTheme.setOnClickListener(this);
		
		m_intent = new Intent(ThemeActivity.this, ViewThemeActivity.class);
	}
	
	@Override
	public void onClick(View p_v)
	{
		switch (p_v.getId())
			{
				case R.id.thbtnBlackTheme:
					m_nThemeId = R.style.Theme_Black;
					break;
				case R.id.thbtnBlueTheme:
					m_nThemeId = R.style.Theme_Blue;
					break;
				case R.id.thbtnPinkTheme:
					m_nThemeId = R.style.Theme_Pink;
					break;
				
				default:
					break;
			}
		startActivity(m_intent);
		
	}
	
}