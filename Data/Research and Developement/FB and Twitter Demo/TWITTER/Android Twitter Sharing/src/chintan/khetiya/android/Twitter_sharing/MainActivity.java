package chintan.khetiya.android.Twitter_sharing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import chintan.khetiya.android.Twitter_code.Twitt_Sharing;

//In case if you get any error then check out the jar file version. it should be latest one.

public class MainActivity extends Activity {

	// Replace your KEY here and Run ,
	public final String consumer_key = "trWwomp0b09ER2A8H1cQg";
	public final String secret_key = "PAC3E3CtcPcTuPl9VpCuzY6eDD8hPZPwp6gRDCviLs";
	File casted_image, myImage;
	InputStream m_imageStream;
	String string_img_url = null, string_msg = null;
	Button btn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setContentView(R.layout.main);
			btn = (Button) findViewById(R.id.btn);
			btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onClickTwitt();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			showToast("View problem");
		}
	}

	public void Call_My_Blog(View v) {
		Intent intent = new Intent(MainActivity.this, My_Blog.class);
		startActivity(intent);

	}

	// Here you can pass the string message & image path which you want to share
	// in Twitter.
	public void onClickTwitt() {
		if (isNetworkAvailable()) {
			Twitt_Sharing twitt = new Twitt_Sharing(MainActivity.this,
					consumer_key, secret_key);
//			string_img_url = "http://www.wikihow.com/images/9/91/Get-the-URL-for-Pictures-Step-1.jpg";
			// "http://3.bp.blogspot.com/_Y8u09A7q7DU/S-o0pf4EqwI/AAAAAAAAFHI/PdRKv8iaq70/s1600/id-do-anything-logo.jpg";
			string_msg = "Using PeekaBoo."
					+ "https://play.google.com/store/apps/details?id=com.penqgames.tk.brtsway";// "http://chintankhetiya.wordpress.com/";
			// here we have web url image so we have to make it as file to
			// upload
			Bitmap m_bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher, null);
			
			try {
				myImage = new File(Environment.getExternalStorageDirectory()
						+ "/share.jpg");
				System.err.println("Path===="+myImage);
				InputStream inputStream = getResources().openRawResource(
						R.drawable.twitter_icon);
				OutputStream out = new FileOutputStream(myImage);
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0)
					out.write(buf, 0, len);
				out.close();
				inputStream.close();
			} catch (IOException e) {
			}

			m_imageStream = getResources().openRawResource(
					R.drawable.twitter_icon);
			// m_imageStream = bitmapToInputStream(m_bitmap);
			 myImage = String_to_File(string_img_url);
			// Now share both message & image to sharing activity
			twitt.shareToTwitter(string_msg, myImage, m_imageStream);

		} else {
			showToast("No Network Connection Available !!!");
		}
	}

	// when user will click on twitte then first that will check that is
	// internet exist or not
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static InputStream bitmapToInputStream(Bitmap bitmap) {
		int size = bitmap.getHeight() * bitmap.getRowBytes();
		ByteBuffer buffer = ByteBuffer.allocate(size);
		bitmap.copyPixelsToBuffer(buffer);
		return new ByteArrayInputStream(buffer.array());
	}

	private void showToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

	}

	public File getDir(Context context) {
		File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "PeekaBoo");
        else
            cacheDir = context.getCacheDir();
        return cacheDir;
    }
	// this function will make your image to file
	public File String_to_File(String img_url) {

		try {
			File rootSdDirectory = Environment.getExternalStorageDirectory();

			casted_image = new File(rootSdDirectory, "attachment.jpg");
			if (casted_image.exists()) {
				casted_image.delete();
			}
			casted_image.createNewFile();

			FileOutputStream fos = new FileOutputStream(casted_image);

			URL url = new URL(img_url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			InputStream in = connection.getInputStream();

			byte[] buffer = new byte[1024];
			int size = 0;
			while ((size = in.read(buffer)) > 0) {
				fos.write(buffer, 0, size);
			}
			fos.close();
			return casted_image;

		} catch (Exception e) {

			System.out.print(e);
			// e.printStackTrace();

		}
		return casted_image;
	}

}
