/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fb.fbdemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.LoggingBehaviors;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class LoginUsingActivityActivity extends Activity {
	// private static final String URL_PREFIX_FRIENDS =
	// "https://graph.facebook.com/me/friends?access_token=";
	// private static final String URL_PREFIX_FRIENDS =
	// "https://graph.facebook.com/me?fields=id,name,picture&access_token=";
	// private static final String URL_FRIENDS =
	// "https://graph.facebook.com/me/friends?access_token=";
	// "https://graph.facebook.com/me?fields=id,name,email&access_token=" +
	// oAuth.Token;
	private TextView textInstructionsOrLink, m_tvName;
	private Button buttonLoginLogout;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private ImageView m_ivImage;
	public String m_sName, m_sprofUrl, m_url, m_getUrl;
	JSONObject jsonObject = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		buttonLoginLogout = (Button) findViewById(R.id.buttonLoginLogout);
		textInstructionsOrLink = (TextView) findViewById(R.id.instructionsOrLink);
		m_tvName = (TextView) findViewById(R.id.tvName);
		// m_ivImage = (ImageView) findViewById(R.id.ivImag);
		m_getUrl = getIntent().getStringExtra("url");
		System.err.println("UTRLLLLLLLLLLLLLL===" + m_getUrl);
		Settings.addLoggingBehavior(LoggingBehaviors.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this)
						.setCallback(statusCallback));
			}
		}

		updateView();
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			textInstructionsOrLink.setText(m_getUrl + session.getAccessToken());
			// m_url = parseJSON(m_getUrl + session.getAccessToken());
			callRequest m_rs = new callRequest();
			m_rs.execute(session.getAccessToken());
			// System.out.println("Url Response get:====>"
			// + parseJSON(m_url + session.getAccessToken()));
			// Instantiate a JSON object from the request response
			/*
			 * try { jsonObject = new JSONObject(m_url);
			 * 
			 * System.out.println("ID=" + jsonObject.getString("id") + "NAME==>"
			 * + jsonObject.getString("name") + "Pic==" +
			 * jsonObject.getString("picture")); m_sName =
			 * jsonObject.getString("id") + jsonObject.getString("name");
			 * 
			 * JSONObject jobj = new JSONObject(
			 * jsonObject.getString("picture")); JSONObject m_dataobj = new
			 * JSONObject(jobj.getString("data"));
			 * System.out.println("^&&&&&&&&&&&&&&&&&&  " +
			 * m_dataobj.getString("url")); m_sprofUrl =
			 * m_dataobj.getString("url"); } catch (JSONException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * m_tvName.setText(m_sName);
			 */

			buttonLoginLogout.setText(R.string.logout);
			buttonLoginLogout.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					onClickLogout();
				}
			});
		} else {
			textInstructionsOrLink.setText(R.string.instructions);
			buttonLoginLogout.setText(R.string.login);
			buttonLoginLogout.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					onClickLogin();
				}
			});
		}
	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	class callRequest extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			m_url = parseJSON(m_getUrl + params[0]);
			return m_url;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			m_tvName.setText(result);
		}
	}

	public static String parseJSON(String p_url) {
		JSONObject jsonObject = null;
		String json = null;
		try {
			// Create a new HTTP Client
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			// Setup the get request
			HttpGet httpGetRequest = new HttpGet(p_url);
			System.out.println("Request URL--->" + p_url);
			// Execute the request in the client
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			// Grab the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			json = reader.readLine();
			System.err.println("JSON Response--->" + json);
			// Instantiate a JSON object from the request response
			/*
			 * jsonObject = new JSONObject(json); System.out.println("ID=" +
			 * jsonObject.getString("id") + "NAME==>" +
			 * jsonObject.getString("name") + "Pic==" +
			 * jsonObject.getString("picture"));
			 * 
			 * JSONObject jobj = new
			 * JSONObject(jsonObject.getString("picture")); JSONObject m_dataobj
			 * = new JSONObject(jobj.getString("data"));
			 * System.out.println("^&&&&&&&&&&&&&&&&&&  " +
			 * m_dataobj.getString("url"));
			 */

		} catch (Exception e) {
			// In your production code handle any errors and catch the
			// individual exceptions
			e.printStackTrace();
		}
		return json;
	}

}
