package com.test.flickr.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

import com.test.flickr.R;
import com.test.flickr.utils.Internet;
import com.test.flickr.utils.RemoteImage;



public abstract class FetchPhotos extends AsyncTask<Void, Void, Boolean>{
	
	private Context context;
	private ProgressDialog pd;
	private String thePhotos="";
	private String url;
	private final int[] timeout={3,10};
	
	public ArrayList<PictureInfo> thePics;

	public FetchPhotos(Context context,String url) {
		this.context=context;	
		this.url=url;
		
	}
	
	private void fillGalery(JSONObject theFeed) throws Exception{
		
		JSONArray  Categories=theFeed.getJSONArray("photo");
		
		thePics = new ArrayList<PictureInfo>();
		
		for (int i=0;i<(Categories.length()>15?15:Categories.length());i++){
			PictureInfo p = new PictureInfo();
			JSONObject pic = Categories.getJSONObject(i);
			p.title =pic.getString("title");
			String url="http://farm"+pic.getString("farm")+".staticflickr.com/"+pic.getString("server")+"/"+
							pic.getString("id")+"_"+pic.getString("secret")+".jpg";
			p.url=url;
			p.executed=false;
			thePics.add(p);
		}
		
		for(int i=0;i<(thePics.size());i++){
			ImageView ivPic = new ImageView(context);
			ivPic.setImageResource(R.drawable.ic_launcher);
			LinearLayout llAll = new LinearLayout(context);
			llAll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			llAll.setGravity(Gravity.CENTER);
			llAll.setOrientation(1);
			ProgressBar pb = new ProgressBar(context,null, android.R.attr.progressBarStyleSmall);
			pb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			llAll.addView(pb);
			llAll.addView(ivPic);

			thePics.get(i).iv=ivPic;
			thePics.get(i).pb=pb;
			((ViewAnimator)((Activity) context).findViewById(R.id.PictureAnimator)).addView(llAll);
			
		}
		
		LoadPhoto(thePics.get(0));
	}	


	@Override
	protected void onPreExecute() {
		pd=ProgressDialog.show(context, "downloading", "please wait");
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try{
			thePhotos = new Internet().GetRequest(url, null, timeout);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		pd.dismiss();
		if(result){
			try {
				thePhotos=thePhotos.split("\\(")[1];
				thePhotos.replace("\\)", "");
				fillGalery(new JSONObject(thePhotos).getJSONObject("photoset"));
			} catch (Exception e) {Log.e("karp", "photolist2: "+e.getMessage());onFetchError();onFetchError();}
		}else{
			onFetchError();
		}
		super.onPostExecute(result);
	}
	
	
	public abstract void onFetchError();
	
	public void LoadPhoto(PictureInfo pi){
		Log.d("karp", "LoadPhoto");
		if(!(pi.executed)){
			new LoadPics(pi).execute();
			pi.executed=true;
		}
	}
	
	
	private class LoadPics extends RemoteImage{
		private ImageView ivTarget;
		private ProgressBar pb;
		public LoadPics(PictureInfo pi) {
			super(pi.url);
			this.ivTarget=pi.iv;
			this.pb=pi.pb;
		}		
		@Override
		public void onSuccess(Bitmap remoteBitmap) {
			try{
				pb.setVisibility(View.INVISIBLE);
				ivTarget.setImageBitmap(remoteBitmap);	
			}catch (Exception e) {}
		}		
		@Override
		public void onFail() {pb.setVisibility(View.INVISIBLE);}
	}

}