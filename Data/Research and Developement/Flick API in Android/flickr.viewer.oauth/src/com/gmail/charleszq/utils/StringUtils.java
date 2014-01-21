/*
 * Created on Jul 25, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

import com.gmail.charleszq.model.IListItemAdapter;

/**
 * Represents the util class to handle html text.
 * 
 * @author charles
 */
public final class StringUtils {

//	private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);
	
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	static final String KEY_ID = "id"; //$NON-NLS-1$
	static final String KEY_TITLE = "title"; //$NON-NLS-1$
	static final String KEY_ICON_ID = "iconid"; //$NON-NLS-1$
	static final String KEY_TYPE = "type"; //$NON-NLS-1$
	static final String KEY_OBJ_TYPE = "obj"; //$NON-NLS-1$
	static final String KEY_ITEM_COUNT = "cnt";  //$NON-NLS-1$

	/**
	 * Example: [http://www.flickr.com/photos/example/2910192942/]
	 */
	private static final String FILICK_URL_EXPRESSION = "(\\[http){1}+(s)?+(://){1}+.*\\]{1}+"; //$NON-NLS-1$

	public static void formatHtmlString(String string, TextView textView) {

		textView.setText(Html.fromHtml(string));
		Linkify.addLinks(textView, Pattern.compile(FILICK_URL_EXPRESSION),
				"http://", new MatchFilter() { //$NON-NLS-1$

					@Override
					public boolean acceptMatch(CharSequence s, int start,
							int end) {
						return true;
					}

				}, new TransformFilter() {

					@Override
					public String transformUrl(Matcher matcher, String data) {
						if (data.length() > 2) {
							return data.substring(1, data.length() - 1);
						}
						return data;
					}

				});
	}

	public static List<IListItemAdapter> readItemsFromCache(File file)
			throws IOException, JSONException {
		if (!file.exists()) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		List<IListItemAdapter> list = new ArrayList<IListItemAdapter>();
		JSONArray array = new JSONArray(builder.toString());
		for (int i = 0; i < array.length(); i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			list.add(new JsonItemAdapter(jsonObject));
		}
		return list;
	}

	public static void writeItemsToFile(List<IListItemAdapter> list, File file)
			throws JSONException, IOException {
		JSONArray array = new JSONArray();
		for (IListItemAdapter item : list) {
			array.put(toJson(item));
		}

		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(array.toString());
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static JSONObject toJson(IListItemAdapter item)
			throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(KEY_ID, item.getId());
		obj.put(KEY_TITLE, item.getTitle());
		obj.put(KEY_ICON_ID, item.getBuddyIconPhotoIdentifier());
		obj.put(KEY_TYPE, item.getType());
		obj.put(KEY_OBJ_TYPE, item.getObjectClassType());
		return obj;
	}

	/**
	 * Represents the adapter constructed from a JSON object.
	 */
	private static class JsonItemAdapter implements IListItemAdapter {

		private JSONObject mObject;

		JsonItemAdapter(JSONObject json) {
			this.mObject = json;
		}

		@Override
		public String getId() {
			try {
				return mObject.getString(StringUtils.KEY_ID);
			} catch (JSONException e) {
				return null;
			}
		}

		@Override
		public String getTitle() {
			try {
				return mObject.getString(StringUtils.KEY_TITLE);
			} catch (JSONException e) {
				return null;
			}
		}

		@Override
		public String getBuddyIconPhotoIdentifier() {
			try {
				return mObject.getString(StringUtils.KEY_ICON_ID);
			} catch (JSONException e) {
				return null;
			}
		}

		@Override
		public int getType() {
			try {
				return mObject.getInt(StringUtils.KEY_TYPE);
			} catch (JSONException e) {
				return -1;
			}
		}

		@Override
		public String getObjectClassType() {
			try {
				return mObject.getString(StringUtils.KEY_OBJ_TYPE);
			} catch (JSONException e) {
				return null;
			}
		}

		@Override
		public int getItemCount() {
			try {
				return mObject.getInt(StringUtils.KEY_ITEM_COUNT);
			} catch (JSONException ex) {
				return 0;
			}
		}
	}

	private StringUtils() {
	}
}
