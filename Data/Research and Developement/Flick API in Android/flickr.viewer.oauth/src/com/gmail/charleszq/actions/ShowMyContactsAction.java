/**
 * 
 */

package com.gmail.charleszq.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.gmail.charleszq.R;
import com.gmail.charleszq.event.IContactsFetchedListener;
import com.gmail.charleszq.task.GetContactsTask;
import com.gmail.charleszq.ui.ContactsFragment;
import com.gmail.charleszq.utils.Constants;
import com.gmail.yuyang226.flickr.contacts.Contact;

/**
 * @author charles
 */
public class ShowMyContactsAction extends ActivityAwareAction {

    private IContactsFetchedListener mContactFetchedListener = null;

    public ShowMyContactsAction(Activity activity) {
        super(activity);
    }

    public ShowMyContactsAction(Activity activity,
            IContactsFetchedListener listener) {
        super(activity);
        this.mContactFetchedListener = listener;
    }

    @Override
    public void execute() {
        GetContactsTask task = new GetContactsTask(mActivity,
                mContactFetchedListener == null ? mDefaultListener
                        : mContactFetchedListener);
        task.execute((String) null);
    }

    private IContactsFetchedListener mDefaultListener = new IContactsFetchedListener() {

        @Override
        public void onContactsFetched(Collection<Contact> contacts) {
            FragmentManager fm = mActivity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Contact> ret = new ArrayList<Contact>();
            ret.addAll(contacts);
            ContactsFragment fragment = new ContactsFragment(ret);
            ft.replace(R.id.main_area, fragment);
            ft.addToBackStack(Constants.CONTACT_BACK_STACK);
            ft.commit();
        }
    };
}
