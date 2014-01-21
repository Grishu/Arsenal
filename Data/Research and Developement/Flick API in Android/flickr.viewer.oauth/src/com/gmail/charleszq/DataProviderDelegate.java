/**
 * 
 */
package com.gmail.charleszq;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.gmail.charleszq.dataprovider.IPhotoListDataProvider;
import com.gmail.charleszq.dataprovider.PopularPhotoListProvider;
import com.gmail.charleszq.event.IPhotoListReadyListener;
import com.gmail.charleszq.ui.menu.IOptionMenuHandler;
import com.gmail.charleszq.ui.menu.PopularPhotoOptionMenuHandler;

/**
 * Represents the delegate for data provider to get the corresponding option
 * menu resource id and the action handler for a data provider.
 * 
 * @author charles
 * 
 */
public final class DataProviderDelegate {

	/**
	 * The singleton instance.
	 */
	private static DataProviderDelegate mInstance = new DataProviderDelegate();

	/**
	 * The option menu resource map. The <code>Application</code> will
	 * initialize the map.
	 */
	private Map<Class<? extends IPhotoListDataProvider>, Integer> mOptionMenuResMap = new HashMap<Class<? extends IPhotoListDataProvider>, Integer>();

	/**
	 * Private constructor.
	 */
	private DataProviderDelegate() {
		super();
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return
	 */
	public static DataProviderDelegate getInstance() {
		return mInstance;
	}

	/**
	 * 
	 * @param dataProviderClass
	 * @param optionMenuResourceId
	 */
	void registerOptionMenuResource(
			Class<? extends IPhotoListDataProvider> dataProviderClass,
			Integer optionMenuResourceId) {
		mOptionMenuResMap.put(dataProviderClass, optionMenuResourceId);
	}

	/**
	 * Returns the option menu resource id for a given
	 * <code>IPhotoListDataProvider</code>
	 * 
	 * @param clazz
	 * @return <code>null</code> if no option menu resource registered.
	 */
	public Integer getOptionMneuRes(
			Class<? extends IPhotoListDataProvider> clazz) {
		return mOptionMenuResMap.get(clazz);
	}

	public IOptionMenuHandler getOptionMenuHandler(Activity context,
			IPhotoListDataProvider provider, IPhotoListReadyListener listener) {
		if (provider instanceof PopularPhotoListProvider) {
			return new PopularPhotoOptionMenuHandler(context,
					(PopularPhotoListProvider) provider, listener);
		}
		return null;
	}
}
