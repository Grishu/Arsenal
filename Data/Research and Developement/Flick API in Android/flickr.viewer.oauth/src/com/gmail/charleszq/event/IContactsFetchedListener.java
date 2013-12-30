/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.event;

import java.util.Collection;

import com.gmail.yuyang226.flickr.contacts.Contact;

/**
 * @author charles
 *
 */
public interface IContactsFetchedListener {

    void onContactsFetched(Collection<Contact> contacts);
}
