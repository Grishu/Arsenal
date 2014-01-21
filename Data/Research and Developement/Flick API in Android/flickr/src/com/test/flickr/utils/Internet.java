package com.test.flickr.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;



public class Internet {
	
	public String GetRequest(String url, List<NameValuePair>  params, int[] timouts) throws Exception{
		InputStream is = null;
		String serverResponce = ""; 
		if(params!=null){
			String paramString = URLEncodedUtils.format(params, "utf-8");
			url += paramString;
		}
		
		HttpClient httpclient = new DefaultHttpClient(timeOuts(timouts[0],timouts[1]));
		HttpGet get = new HttpGet(url);
        HttpResponse responseGet = httpclient.execute(get);  
        HttpEntity resEntityGet = responseGet.getEntity();  
        if (resEntityGet != null) {  
        	is = resEntityGet.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");	
			}
			is.close();
			serverResponce=sb.toString();
        }
	
		return serverResponce;		
	}
	
	public String PostRequest(String url, List<NameValuePair>  params, int[] timouts) throws Exception{
		InputStream is = null;
		String serverResponce = ""; 
		
		HttpClient httpclient = new DefaultHttpClient(timeOuts(timouts[0],timouts[1]));
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		if(!(entity.equals(null))){
			is = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");	
			}
			is.close();
			serverResponce=sb.toString();
		}
		return serverResponce;
	}
	
	
	public HttpParams timeOuts(int connectionSecs, int socketSecs){
	    HttpParams httpParameters = new BasicHttpParams();
	    int timeoutConnection = connectionSecs*1000;
	    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    int timeoutSocket = socketSecs*1000;
	    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	    return httpParameters;
	}

}
