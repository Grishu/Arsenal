/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Fragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

import com.gmail.charleszq.R;

/**
 * Represents the fragment to show the help page of this application.
 * 
 * @author charles
 */
public class HelpFragment extends Fragment {

    /**
     * The webview to show the help html content.
     */
    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mWebView = new WebView(getActivity());
        mWebView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        mWebView.getSettings().setJavaScriptEnabled(true);
        return mWebView;
    }

    @Override
    public void onStart() {
        super.onStart();
        AssetManager am = getActivity().getAssets();
        InputStream is = null;
        try {
        	String htmlFile = getActivity().getString(R.string.help_file_name);
            is = am.open(htmlFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            int ch = reader.read();
            while (ch != -1) {
                sb.append((char) ch);
                ch = reader.read();
            }
            mWebView.loadDataWithBaseURL(htmlFile, sb.toString(), "text/html", "utf-8", null); //$NON-NLS-1$//$NON-NLS-2$
            //mWebView.loadData(sb.toString(), "text/html", "utf-8"); //$NON-NLS-1$//$NON-NLS-2$
        } catch (IOException e) {
        } finally {
            if( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
