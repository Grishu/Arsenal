package com.test.flickr.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.flickr.R;
import com.test.flickr.ui.FlickrActivity;
import com.test.flickr.utils.Internet;
import com.test.flickr.utils.RemoteImage;
import com.test.flickr.utils.Screen;

public abstract class FetchAlbums extends AsyncTask<Void, Void, Boolean> {

	private final String url = "http://www.flickr.com/services/rest/?method=flickr.photosets.getList&api_key="
			+ FlickrActivity.API_KEY
			+ "&format=json&user_id="
			+ FlickrActivity.USER_ID;
	private final int[] timeout = { 3, 10 };
	private String thePhotos;

	private Context context;
	private ProgressDialog pd;

	public FetchAlbums(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		pd = ProgressDialog.show(context, "Download", "please wait");
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean success = false;
		try {
			thePhotos = new Internet().GetRequest(url, null, timeout);
			Log.d("karo", thePhotos);
			success = true;
		} catch (Exception e) {
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		pd.dismiss();
		if (result) {
			try {
				thePhotos = thePhotos.split("\\(")[1];
				thePhotos.replace("\\)", "");

				LinearLayout llTaPantaOla = (LinearLayout) ((Activity) context)
						.findViewById(R.id.llTaPantaOla);

				JSONArray Categories = new JSONObject(thePhotos).getJSONObject(
						"photosets").getJSONArray("photoset");

				ArrayList<PhotoSet> thesets = new ArrayList<PhotoSet>();

				for (int i = 0; i < Categories.length(); i++) {
					PhotoSet p = new PhotoSet();
					p.title = Categories.getJSONObject(i)
							.getJSONObject("title").getString("_content");
					p.id = Categories.getJSONObject(i).getString("id");
					p.thumb = "http://farm"
							+ Categories.getJSONObject(i).getString("farm")
							+ ".static.flickr.com/"
							+ Categories.getJSONObject(i).getString("server")
							+ "/"
							+ Categories.getJSONObject(i).getString("primary")
							+ "_"
							+ Categories.getJSONObject(i).getString("secret")
							+ "_s.jpg";
					thesets.add(p);
				}

				for (int i = 0; i < thesets.size(); i++) {
					ImageView ivImage = new ImageView(context);
					ivImage.setImageResource(R.drawable.ic_launcher);
					llTaPantaOla.addView(ivImage);

					TextView bntItem = new TextView(context);
					bntItem.setTypeface(Typeface.DEFAULT_BOLD);
					bntItem.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					bntItem.setPadding(Screen.dpToPx(context, 5), 0, 0, 0);
					String text = thesets.get(i).title;
					try {
						if (text.length() > 30) {
							text = text.substring(0, 30) + "...";
						}
						text = Character.toUpperCase(text.charAt(0))
								+ (text.length() > 1 ? text.substring(1) : "");
					} catch (Exception e) {
					}
					bntItem.setText(text);
					llTaPantaOla.addView(bntItem);
					ivImage.setOnClickListener(new galleryClick(
							thesets.get(i).id));
					new Thumbnail(thesets.get(i).thumb, ivImage).execute();
				}

			} catch (Exception e) {
			}
		}
		super.onPostExecute(result);
	}

	private class galleryClick implements View.OnClickListener {
		String id;

		public galleryClick(String id) {
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			onPhotoCategoryClick(id);
		}

	}

	public abstract void onPhotoCategoryClick(String id);

	private class PhotoSet {
		public String title;
		public String id;
		public String thumb;
	}

	private class Thumbnail extends RemoteImage {
		private ImageView bnt;

		public Thumbnail(String url, ImageView bnt) {
			super(url);
			Log.d("karp", url);
			this.bnt = bnt;
		}

		@Override
		public void onSuccess(Bitmap remoteBitmap) {
			bnt.setImageBitmap(remoteBitmap);

		}

		@Override
		public void onFail() {
			bnt.setImageBitmap(null);
		}

	}

}
