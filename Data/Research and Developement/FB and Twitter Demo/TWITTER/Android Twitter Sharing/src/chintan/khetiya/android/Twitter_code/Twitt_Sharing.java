package chintan.khetiya.android.Twitter_code;

import java.io.File;
import java.io.InputStream;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import chintan.khetiya.android.Twitter_code.Twitter_Handler.TwDialogListener;

public class Twitt_Sharing {

	private final Twitter_Handler mTwitter;
	private final Activity activity;
	private String twitt_msg;
	private File image_path;
	private InputStream m_inputImage;

	public Twitt_Sharing(Activity act, String consumer_key,
			String consumer_secret) {
		this.activity = act;
		mTwitter = new Twitter_Handler(activity, consumer_key, consumer_secret);
	}

	public void shareToTwitter(String msg, File Image_url, InputStream p_image) {
		this.twitt_msg = msg;
		this.image_path = Image_url;
		m_inputImage = p_image;
		mTwitter.setListener(mTwLoginDialogListener);

		if (mTwitter.hasAccessToken()) {
			// this will post data in asyn background thread
			showTwittDialog();
		} else {
			mTwitter.authorize();
		}
	}

	private void showTwittDialog() {

		new PostTwittTask().execute(twitt_msg);

	}

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		@Override
		public void onError(String value) {
			showToast("Login Failed");
			mTwitter.resetAccessToken();
		}

		@Override
		public void onComplete(String value) {
			showTwittDialog();
		}
	};

	void showToast(final String msg) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

			}
		});

	}

	class PostTwittTask extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Posting Twitt...");
			pDialog.setCancelable(false);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... twitt) {
			try {
				// mTwitter.updateStatus(twitt[0]);
				// File imgFile = new File("/sdcard/bluetooth/Baby.jpg");

				Share_Pic_Text_Titter(image_path, twitt_msg,
						mTwitter.twitterObj, m_inputImage);
				return "success";

			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					return "Posting Failed because of Duplicate message...";
				}
				e.printStackTrace();
				return "Posting Failed!!!";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();

			if (null != result && result.equals("success")) {
				showToast("Posted Successfully");

			} else {
				showToast(result);
			}

			super.onPostExecute(result);
		}
	}

	public void Share_Pic_Text_Titter(File image_path, String message,
			Twitter twitter, InputStream p_image) throws Exception {
		try {
			String filename = "Koala.jpg";
			String path =Environment.getExternalStorageDirectory()+ filename;
			File f = new File(path); 
			
			/*InputStream inputStream = activity.getResources().openRawResource(
					R.drawable.twitter_icon);
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			inputStream.close();*/

			StatusUpdate st = new StatusUpdate(message);
//			st.setMedia("PeekaBoo", p_image);
//			 st.setMedia(image_path);
			twitter.updateStatus(st);

			/*
			 * Toast.makeText(activity, "Successfully update on Twitter...!",
			 * Toast.LENGTH_SHORT).show();
			 */
		} catch (TwitterException e) {
			Log.d("TAG", "Pic Upload error" + e.getErrorMessage());
			// Toast.makeText(activity,
			// "Ooopss..!!! Failed to update on Twitter.",
			// Toast.LENGTH_SHORT).show();
			throw e;
		}
	}

	public void Authorize_UserDetail() {

	}

}
