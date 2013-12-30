/*
 * Created on Jul 26, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.event;

/**
 * Represents the message handler interface.
 * 
 * @author Charles
 */
public interface IFlickrViewerMessageHandler {

    /**
     * Handles the message.
     * 
     * @param message
     */
    void handleMessage(FlickrViewerMessage message);
}
