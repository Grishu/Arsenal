package com.test.flickr.utils;

import android.content.Context;
import android.util.DisplayMetrics;


public class Screen {
	
	   public static int dpToPx(Context context,int arg){
	    	try{
	        DisplayMetrics metrics =context.getResources().getDisplayMetrics();
	        float dp =(float) arg;
	        int pixels = (int) (metrics.density * dp + 0.5f); 
	        return pixels;
	    	}catch (Exception e) {
				return arg;
			}
	    }

}
