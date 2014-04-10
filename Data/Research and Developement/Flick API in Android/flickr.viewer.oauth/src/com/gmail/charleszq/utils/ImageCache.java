package com.gmail.charleszq.utils;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.graphics.Bitmap;

public final class ImageCache {
	public static int CACHE_SIZE = Constants.DEF_CACHE_SIZE;
	

	private static final Map<String, SoftReference<Bitmap>> cache = new LinkedHashMap<String, SoftReference<Bitmap>>() {
		private static final long serialVersionUID = 1L;

		/* (non-Javadoc)
		 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
		 */
		@Override
		protected boolean removeEldestEntry(
				Entry<String, SoftReference<Bitmap>> eldest) {
			return size() > CACHE_SIZE;
		}
		
	};
	
	private static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static final Lock read  = readWriteLock.readLock();
	private static final Lock write = readWriteLock.writeLock();
	
	/**
	 * 
	 */
	private ImageCache() {
		super();
	}

	public static void dispose() {
		write.lock();
		try {
			for (SoftReference<Bitmap> bm : cache.values()) {
				if (bm != null && bm.get() != null) {
					bm.get().recycle();
				}
			}
			cache.clear();
		} finally {
			write.unlock();
		}
	}

	public static void saveToCache(String url, Bitmap bitmap) {
		if (url == null || bitmap == null) {
			return;
		}
		write.lock();
		try {
			cache.put(url, new SoftReference<Bitmap>(bitmap));
		} finally {
			write.unlock();
		}
	}

	public static Bitmap getFromCache(String url) {
		read.lock();
		try {
			if(!cache.containsKey(url))
				return null;
			return cache.get(url).get();
		} finally {
			read.unlock();
		}
	}
}
