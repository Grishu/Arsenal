/*
 * Created on Jun 20, 2011
 *
 * Copyright (c) Sybase, Inc. 2011   
 * All rights reserved.                                    
 */

package com.gmail.charleszq.dataprovider;

import java.util.Collection;

import com.gmail.yuyang226.flickr.contacts.Contact;


/**
 * @author charles
 *
 */
public interface IContactDataProvider {
    
    Collection<Contact> getContacts(String userId);
}
