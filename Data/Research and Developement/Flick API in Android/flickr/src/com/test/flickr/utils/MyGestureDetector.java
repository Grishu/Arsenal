package com.test.flickr.utils;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;


public abstract class MyGestureDetector implements OnGestureListener {
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) >250)
			return false;
			if(e1.getX() - e2.getX() > 150 && Math.abs(velocityX) > 200) {
				rightToLeft();
			} else if (e2.getX() - e1.getX() > 150 && Math.abs(velocityX) > 200) {
				leftToRight();
			}
		} catch (Exception e) {}
		return true;
	}	
	
	@Override
	public boolean onDown(MotionEvent e) {return true;}
	@Override
	public void onLongPress(MotionEvent e) {}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {return false;}
	@Override
	public void onShowPress(MotionEvent e) {}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {return false;}
	
	public abstract void rightToLeft();
	public abstract void leftToRight();
}
