/**
 * 
 */
package com.gmail.charleszq.utils;

/**
 * Represents the class to store constants
 * 
 * @author charles
 */
public final class Constants {

	/**
	 * The preference name.
	 */
	public static final String DEF_PREF_NAME = "flickr_viewer"; //$NON-NLS-1$
	public static final String ID_SCHEME = "flickr-viewer-hd-oauth"; //$NON-NLS-1$

	/**
	 * The folder name stored in the sd card to save temp files of this
	 * application.
	 */
	public static final String SD_CARD_FOLDER_NAME = "flickrviewer"; //$NON-NLS-1$

	/**
	 * The preference setting keys.
	 */
	public static final String PHOTO_LIST_CACHE_SIZE = "photo.list.cache.size"; //$NON-NLS-1$
	public static final String PHOTO_GRID_COL_COUNT = "photo.grid.col.count"; //$NON-NLS-1$
	public static final String PHOTO_PAGE_SIZE = "photo.grid.page.size"; //$NON-NLS-1$
	public static final String ENABLE_CONTACT_UPLOAD_NOTIF = "notif.enable.contact.upload"; //$NON-NLS-1$
	public static final String NOTIF_CONTACT_UPLOAD_INTERVAL = "notif.contact.upload.interval"; //$NON-NLS-1$
	public static final String ENABLE_PHOTO_ACT_NOTIF = "notif.enable.photo.activity"; //$NON-NLS-1$
	public static final String NOTIF_PHOTO_ACT_INTERVAL = "notif.photo.activity.interval"; //$NON-NLS-1$
	public static final String SETTING_SHOW_APP_TITLE = "show.app.title"; //$NON-NLS-1$
	public static final String SETTING_TAG_SRH_MODE_AND = "tag.search.mode"; //$NON-NLS-1$
	public static final String SETTING_ADD_TO_POOL_AUTO_BEGIN = "add.to.pool.auto.begin"; //$NON-NLS-1$
	public static final String SETTING_HIDE_PREVIEW_TITLEBAR = "hide.preview.titlebar"; //$NON-NLS-1$

	public static final String FLICKR_TOKEN_SECRENT = "token.secret"; //$NON-NLS-1$
	public static final String FLICKR_TOKEN = "flickr.token"; //$NON-NLS-1$
	public static final String FLICKR_USER_ID = "flickr.user.id"; //$NON-NLS-1$
	public static final String FLICKR_USER_NAME = "flickr.user.name"; //$NON-NLS-1$
	public static final String FLICKR_BUDDY_IMAGE_FILE_NAME = "mybuddyicon.jpg"; //$NON-NLS-1$

	/**
	 * The default setting values.
	 */
	public static final int DEF_CACHE_SIZE = 50;
	public static final int DEF_GRID_COL_COUNT = 3;
	public static final int DEF_GRID_PAGE_SIZE = 18;

	public static final String PHOTO_LIST_BACK_STACK = "photo.list"; //$NON-NLS-1$
	public static final String SETTING_BACK_STACK = "settings"; //$NON-NLS-1$
	public static final String CONTACT_BACK_STACK = "contacts"; //$NON-NLS-1$
	public static final String ACTIVITY_BACK_STACK = "activities"; //$NON-NLS-1$
	public static final String USER_COLL_BACK_STACK = "user.coll"; //$NON-NLS-1$
	public static final String MAIN_MENU_BACK_STACK = "main.menu"; //$NON-NLS-1$
	public static final String LOCATION_BACK_STACK = "photo.location"; //$NON-NLS-1$
	public static final String HELP_BACK_STACK = "help"; //$NON-NLS-1$
	
	//fragment tags
	public static final String FRG_TAG_MAIN_NAV = "main.nav"; //$NON-NLS-1$


	// notifications
	public static final String CONTACT_UPLOAD_PHOTO_NOTIF_INTENT_ACTION = "INTENT_ACTION_CONTACT_PHOTO_UPLOADED"; //$NON-NLS-1$
	public static final String CONTACT_IDS_WITH_PHOTO_UPLOADED = "BUNDLE_KEY_CONTACTS_WITH_PHOTO_UPLOADED"; //$NON-NLS-1$
	public static final int COTACT_UPLOAD_NOTIF_ID = 1;

	public static final String ACT_ON_MY_PHOTO_NOTIF_INTENT_ACTION = "INTENT_ACTION_ACT_ON_MY_PHOTO"; //$NON-NLS-1$
	public static final int ACT_ON_MY_PHOTO_NOTIF_ID = 2;

	// Intent actions for broadcast receiver
	public static final String INTENT_ACTION_CHECK_CONTACT_UPLOAD_RECEIVER = "INTENT_ACTION_CHECK_CONTACT_UPLOAD_RECEIVER"; //$NON-NLS-1$
	public static final String INTENT_ACTION_CHECK_PHOTO_ACTIVITY_RECEIVER = "INTENT_ACTION_CHECK_PHOTO_ACTIVITY_RECEIVER"; //$NON-NLS-1$

	// notification related check interval
	public static final int SERVICE_CHECK_INTERVAL = 2;

	/**
	 * Private constructor to prevent this class to be instanced.
	 */
	private Constants() {
	}
}
