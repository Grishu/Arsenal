package com.test.flickr.utils;
	
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;


public abstract class RemoteImage extends AsyncTask<Void, Void, Boolean>{
	private String url;
	private Bitmap remoteBitmap;
	
	public RemoteImage(String url){
		this.url=url;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean theResult=false;
		InputStream is = null;
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
	        HttpResponse responseGet = httpclient.execute(get);  
	        HttpEntity entity = responseGet.getEntity();  
	        if(entity.equals(null)){
	        		theResult=false;
	        }else{
	     	   try{
	     		   is = entity.getContent();
	     		   remoteBitmap = BitmapFactory.decodeStream(is);
	     		   if(remoteBitmap.getHeight()>0){
	     			   theResult=true;
	     		   }else{
	     			   theResult=true;
	     		   }
	     	   }catch (Exception e2) {
	   			Log.e("karp", "1 "+e2.getMessage());
	     		   theResult=false;
				}
	        }	
		}catch (Exception e) {
			Log.e("karp", "2 "+e.getMessage());
  		   theResult=false;
		}
		return theResult;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			onSuccess(remoteBitmap);
		}else{
			onFail();
		}
		super.onPostExecute(result);
	}
	
	
	public HttpParams timeOuts(){
	    HttpParams httpParameters = new BasicHttpParams();
	    int timeoutConnection = 3000;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    int timeoutSocket = 15000;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	    return httpParameters;
	}
	
	public abstract void onSuccess(Bitmap remoteBitmap);
	public abstract void onFail();

}
