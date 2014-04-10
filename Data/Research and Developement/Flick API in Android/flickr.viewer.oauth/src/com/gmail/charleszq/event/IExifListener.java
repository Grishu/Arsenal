
package com.gmail.charleszq.event;

import java.util.Collection;

import com.gmail.yuyang226.flickr.photos.Exif;

/**
 * @author charles
 */
public interface IExifListener {

    /**
     * After exif inforamtion got
     * 
     * @param bitmap the photo image bitmap
     * @param exifs the exif information.
     */
    void onExifInfoFetched(Collection<Exif> exifs);
}
