/**
 * 
 */
package com.gmail.charleszq.model;

import java.util.List;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

/**
 * Represents the cursor to be used as the recent tag search suggestion, since
 * <code>SearchView</code> can only use a <code>CursorAdapter</code> which will
 * take a <code>Cursor</code>.
 * 
 * @author charles
 * 
 */
public class RecentTagsCursor implements Cursor {

	private List<String> mRecentTags;
	private int mCurrentPosition = 0;

	/**
	 * Constructor.
	 * 
	 * @param tags the recent searched tags.
	 */
	public RecentTagsCursor(List<String> tags) {
		this.mRecentTags = tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getCount()
	 */
	@Override
	public int getCount() {
		return mRecentTags.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getPosition()
	 */
	@Override
	public int getPosition() {
		return mCurrentPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#move(int)
	 */
	@Override
	public boolean move(int offset) {
		int pos = mCurrentPosition + offset;
		if (pos < 0 || pos >= getCount()) {
			return false;
		} else {
			mCurrentPosition = pos;
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#moveToPosition(int)
	 */
	@Override
	public boolean moveToPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return false;
		} else {
			mCurrentPosition = position;
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#moveToFirst()
	 */
	@Override
	public boolean moveToFirst() {
		mCurrentPosition = 0;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#moveToLast()
	 */
	@Override
	public boolean moveToLast() {
		mCurrentPosition = getCount() - 1;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#moveToNext()
	 */
	@Override
	public boolean moveToNext() {
		int pos = mCurrentPosition + 1;
		if (pos >= getCount()) {
			return false;
		} else {
			mCurrentPosition = pos;
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#moveToPrevious()
	 */
	@Override
	public boolean moveToPrevious() {
		int pos = mCurrentPosition - 1;
		if (pos < 0) {
			return false;
		} else {
			mCurrentPosition = pos;
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#isFirst()
	 */
	@Override
	public boolean isFirst() {
		return mCurrentPosition == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#isLast()
	 */
	@Override
	public boolean isLast() {
		return mCurrentPosition == getCount() - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#isBeforeFirst()
	 */
	@Override
	public boolean isBeforeFirst() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#isAfterLast()
	 */
	@Override
	public boolean isAfterLast() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getColumnIndex(java.lang.String)
	 */
	@Override
	public int getColumnIndex(String columnName) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getColumnIndexOrThrow(java.lang.String)
	 */
	@Override
	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return "_id"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		return new String[] { "_id" }; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getBlob(int)
	 */
	@Override
	public byte[] getBlob(int columnIndex) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getString(int)
	 */
	@Override
	public String getString(int columnIndex) {
		return mRecentTags.get(mCurrentPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#copyStringToBuffer(int,
	 * android.database.CharArrayBuffer)
	 */
	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getShort(int)
	 */
	@Override
	public short getShort(int columnIndex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getInt(int)
	 */
	@Override
	public int getInt(int columnIndex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getLong(int)
	 */
	@Override
	public long getLong(int columnIndex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getFloat(int)
	 */
	@Override
	public float getFloat(int columnIndex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getDouble(int)
	 */
	@Override
	public double getDouble(int columnIndex) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#isNull(int)
	 */
	@Override
	public boolean isNull(int columnIndex) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#deactivate()
	 */
	@Override
	public void deactivate() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#requery()
	 */
	@Override
	public boolean requery() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#close()
	 */
	@Override
	public void close() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#registerContentObserver(android.database.
	 * ContentObserver)
	 */
	@Override
	public void registerContentObserver(ContentObserver observer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#unregisterContentObserver(android.database.
	 * ContentObserver)
	 */
	@Override
	public void unregisterContentObserver(ContentObserver observer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#registerDataSetObserver(android.database.
	 * DataSetObserver)
	 */
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#unregisterDataSetObserver(android.database.
	 * DataSetObserver)
	 */
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.Cursor#setNotificationUri(android.content.ContentResolver
	 * , android.net.Uri)
	 */
	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getWantsAllOnMoveCalls()
	 */
	@Override
	public boolean getWantsAllOnMoveCalls() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#getExtras()
	 */
	@Override
	public Bundle getExtras() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.Cursor#respond(android.os.Bundle)
	 */
	@Override
	public Bundle respond(Bundle extras) {
		return null;
	}

	@Override
	public int getType(int columnIndex) {
		return FIELD_TYPE_STRING;
	}

}
