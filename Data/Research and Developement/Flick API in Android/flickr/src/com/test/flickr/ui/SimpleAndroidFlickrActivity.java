package com.test.flickr.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.flickr.R;

/**
 * Class that search for the username on flickr and get all the details with
 * image of the user.
 * 
 */

public class SimpleAndroidFlickrActivity extends Activity {

	/*
	 * FlickrQuery = FlickrQuery_url + FlickrQuery_per_page +
	 * FlickrQuery_nojsoncallback + FlickrQuery_format + FlickrQuery_tag + q +
	 * FlickrQuery_key + FlickrApiKey
	 */

	String FlickrQuery_url = "http://api.flickr.com/services/rest/?method=flickr.photos.search";
	String FlickrQuery_per_page = "&per_page=1";
	String FlickrQuery_nojsoncallback = "&nojsoncallback=1";
	String FlickrQuery_format = "&format=json";
	String FlickrQuery_tag = "&tags=";
	String FlickrQuery_key = "&api_key=";

	// Apply your Flickr API:
	// www.flickr.com/services/apps/create/apply/?
	String FlickrApiKey = "871805936c074bf3b299789e95bb38ce";

	EditText searchText;
	Button searchButton;
	TextView textQueryResult, textJsonResult;
	ImageView imageFlickrPhoto;
	Bitmap bmFlickr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);

		searchText = (EditText) findViewById(R.id.searchtext);
		searchButton = (Button) findViewById(R.id.searchbutton);
		textQueryResult = (TextView) findViewById(R.id.queryresult);
		textJsonResult = (TextView) findViewById(R.id.jsonresult);
		imageFlickrPhoto = (ImageView) findViewById(R.id.flickrPhoto);

		searchButton.setOnClickListener(searchButtonOnClickListener);
	}

	private Button.OnClickListener searchButtonOnClickListener = new Button.OnClickListener() {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String searchQ = searchText.getText().toString().trim();
			String searchResult = QueryFlickr(searchQ);
			textQueryResult.setText(searchResult);
			String jsonResult = ParseJSON(searchResult);

			textJsonResult.setText(jsonResult);
			if (bmFlickr != null) {
				imageFlickrPhoto.setImageBitmap(bmFlickr);
			}
		}
	};

	// Access Flickr API using HttpClient
	private String QueryFlickr(String q) {

		String qResult = null;

		String qString = FlickrQuery_url + FlickrQuery_per_page
				+ FlickrQuery_nojsoncallback + FlickrQuery_format
				+ FlickrQuery_tag + q + FlickrQuery_key + FlickrApiKey;

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(qString);

		try {
			HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				Reader in = new InputStreamReader(inputStream);
				BufferedReader bufferedreader = new BufferedReader(in);
				StringBuilder stringBuilder = new StringBuilder();

				String stringReadLine = null;

				while ((stringReadLine = bufferedreader.readLine()) != null) {
					stringBuilder.append(stringReadLine + "\n");
				}

				qResult = stringBuilder.toString();

			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return qResult;
	}

	// Parse JSON returned from Flickr Services
	private String ParseJSON(String json) {

		String jResult = null;
		bmFlickr = null;

		String flickrId, flickrOwner, flickrSecret, flickrServer, flickrFarm, flickrTitle;

		try {

			JSONObject JsonObject = new JSONObject(json);

			JSONObject Json_photos = JsonObject.getJSONObject("photos");

			JSONArray JsonArray_photo = Json_photos.getJSONArray("photo");

			// We have only one photo in this exercise

			JSONObject FlickrPhoto = JsonArray_photo.getJSONObject(0);

			jResult = "\nid: " + FlickrPhoto.getString("id") + "\n"

			+ "owner: " + FlickrPhoto.getString("owner") + "\n"

			+ "secret: " + FlickrPhoto.getString("secret") + "\n"

			+ "server: " + FlickrPhoto.getString("server") + "\n"

			+ "farm: " + FlickrPhoto.getString("farm") + "\n"

			+ "title: " + FlickrPhoto.getString("title") + "\n";

			flickrId = FlickrPhoto.getString("id");
			flickrOwner = FlickrPhoto.getString("owner");
			flickrSecret = FlickrPhoto.getString("secret");
			flickrServer = FlickrPhoto.getString("server");
			flickrFarm = FlickrPhoto.getString("farm");
			flickrTitle = FlickrPhoto.getString("title");

			bmFlickr = LoadPhotoFromFlickr(flickrId, flickrOwner, flickrSecret,
					flickrServer, flickrFarm, flickrTitle);

		} catch (JSONException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		return jResult;

	}

	// Load photo from Flickr part I
	private Bitmap LoadPhotoFromFlickr(String id, String owner, String secret,
			String server, String farm, String title) {
		Bitmap bm = null;

		String FlickrPhotoPath = "http://farm" + farm + ".static.flickr.com/"
				+ server + "/" + id + "_" + secret + "_m.jpg";

		URL FlickrPhotoUrl = null;

		try {
			FlickrPhotoUrl = new URL(FlickrPhotoPath);

			HttpURLConnection httpConnection = (HttpURLConnection) FlickrPhotoUrl
					.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.connect();
			InputStream inputStream = httpConnection.getInputStream();
			bm = BitmapFactory.decodeStream(inputStream);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bm;
	}

}