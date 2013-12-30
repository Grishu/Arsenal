/*
 * Created on Jun 13, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.actions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.gmail.charleszq.ui.AuthFragmentDialog;

/**
 * Represents the action to show the auth dialog TODO: can add a finish action
 * to do after auth.
 * 
 * @author charles
 */
public class ShowAuthDialogAction extends ActivityAwareAction {

    private IAction mFinishAction;
    
    public ShowAuthDialogAction(Activity act) {
        this(act,null);
    }

    public ShowAuthDialogAction(Activity act, IAction finishAction) {
        super(act);
        this.mFinishAction = finishAction;
    }

    /*
     * (non-Javadoc)
     * @see com.gmail.charleszq.actions.IAction#execute()
     */
    @Override
    public void execute() {
        FragmentManager fm = mActivity.getFragmentManager();
        FragmentTransaction ft = fm
                .beginTransaction();
        Fragment prev = fm.findFragmentByTag(
                "auth_dialog"); //$NON-NLS-1$
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        AuthFragmentDialog authDialog = new AuthFragmentDialog();
        authDialog.setFinishAction(mFinishAction);
        authDialog.setCancelable(true);
        authDialog.show(ft, "auth_dialog"); //$NON-NLS-1$
    }

}
