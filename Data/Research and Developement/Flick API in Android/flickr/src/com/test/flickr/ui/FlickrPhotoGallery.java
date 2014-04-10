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
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.test.flickr.R;

public class FlickrPhotoGallery extends Activity {

	ProgressDialog progressDialog;
	BackgroundThread backgroundThread;

	public class FlickrImage {
		String Id;
		String Owner;
		String Secret;
		String Server;
		String Farm;
		String Title;

		Bitmap FlickrBitmap;

		FlickrImage(String _Id, String _Owner, String _Secret, String _Server,
				String _Farm, String _Title) {
			Id = _Id;
			Owner = _Owner;
			Secret = _Secret;
			Server = _Server;
			Farm = _Farm;
			Title = _Title;

			FlickrBitmap = preloadBitmap();
		}

		private Bitmap preloadBitmap() {
			Bitmap bm = null;

			String FlickrPhotoPath = "http://farm" + Farm
					+ ".static.flickr.com/" + Server + "/" + Id + "_" + Secret
					+ "_m.jpg";

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

		public Bitmap getBitmap() {
			return FlickrBitmap;
		}

	}

	class FlickrAdapter extends BaseAdapter {
		private Context context;
		private FlickrImage[] FlickrAdapterImage;;

		FlickrAdapter(Context c, FlickrImage[] fImage) {
			context = c;
			FlickrAdapterImage = fImage;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return FlickrAdapterImage.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return FlickrAdapterImage[position];
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView image;
			if (convertView == null) {
				image = new ImageView(context);
				image.setLayoutParams(new Gallery.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				image.setScaleType(ImageView.ScaleType.CENTER_CROP);
				image.setPadding(8, 8, 8, 8);
			} else {
				image = (ImageView) convertView;
			}

			image.setImageBitmap(FlickrAdapterImage[position].getBitmap());

			return image;
		}

	}

	FlickrImage[] myFlickrImage;

	/*
	 * FlickrQuery = FlickrQuery_url + FlickrQuery_per_page +
	 * FlickrQuery_nojsoncallback + FlickrQuery_format + FlickrQuery_tag + q +
	 * FlickrQuery_key + FlickrApiKey
	 */

	String FlickrQuery_url = "http://api.flickr.com/services/rest/?method=flickr.photos.search";
	String FlickrQuery_per_page = "&per_page=10";
	String FlickrQuery_nojsoncallback = "&nojsoncallback=1";
	String FlickrQuery_format = "&format=json";
	String FlickrQuery_tag = "&tags=";
	String FlickrQuery_key = "&api_key=";

	// Apply your Flickr API:
	// www.flickr.com/services/apps/create/apply/?
	String FlickrApiKey = "871805936c074bf3b299789e95bb38ce";

	// final String DEFAULT_SEARCH = "Bill_Gate";
	final String DEFAULT_SEARCH = "new_york";

	EditText searchText;
	Button searchButton;

	Gallery photoBar;

	Bitmap bmFlickr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_gallery_layout);

		searchText = (EditText) findViewById(R.id.searchtext);
		searchText.setText(DEFAULT_SEARCH);
		searchButton = (Button) findViewById(R.id.searchbutton);

		photoBar = (Gallery) findViewById(R.id.photobar);

		searchButton.setOnClickListener(searchButtonOnClickListener);
	}

	private Button.OnClickListener searchButtonOnClickListener = new Button.OnClickListener() {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			progressDialog = ProgressDialog.show(FlickrPhotoGallery.this,
					"ProgressDialog", "Wait!");

			backgroundThread = new BackgroundThread();
			backgroundThread.setRunning(true);
			backgroundThread.start();

		}
	};

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
				inputStream.close();
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

	private FlickrImage[] ParseJSON(String json) {

		FlickrImage[] flickrImage = null;

		bmFlickr = null;
		String flickrId;
		String flickrOwner;
		String flickrSecret;
		String flickrServer;
		String flickrFarm;
		String flickrTitle;

		try {
			JSONObject JsonObject = new JSONObject(json);
			JSONObject Json_photos = JsonObject.getJSONObject("photos");
			JSONArray JsonArray_photo = Json_photos.getJSONArray("photo");

			flickrImage = new FlickrImage[JsonArray_photo.length()];
			for (int i = 0; i < JsonArray_photo.length(); i++) {
				JSONObject FlickrPhoto = JsonArray_photo.getJSONObject(i);
				flickrId = FlickrPhoto.getString("id");
				flickrOwner = FlickrPhoto.getString("owner");
				flickrSecret = FlickrPhoto.getString("secret");
				flickrServer = FlickrPhoto.getString("server");
				flickrFarm = FlickrPhoto.getString("farm");
				flickrTitle = FlickrPhoto.getString("title");
				flickrImage[i] = new FlickrImage(flickrId, flickrOwner,
						flickrSecret, flickrServer, flickrFarm, flickrTitle);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flickrImage;
	}

	public class BackgroundThread extends Thread {
		volatile boolean running = false;
		int cnt;

		void setRunning(boolean b) {
			running = b;
			cnt = 10;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String searchQ = searchText.getText().toString();
			String searchResult = QueryFlickr(searchQ);

			myFlickrImage = ParseJSON(searchResult);

			handler.sendMessage(handler.obtainMessage());
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			progressDialog.dismiss();
			photoBar.setAdapter(new FlickrAdapter(FlickrPhotoGallery.this,
					myFlickrImage));
			Toast.makeText(FlickrPhotoGallery.this, "Flickr images loaded",
					Toast.LENGTH_LONG).show();
		}

	};

}