package com.pbo.apps.dailyselfie;

import android.graphics.Bitmap;

/**
 *  A class to hold the data pertaining to an item in the gallery
 */

class GalleryItem {
    private String mImagePath;

    GalleryItem(String imagePath) {
        mImagePath = imagePath;
    }

    String getImagePath() {
        return mImagePath;
    }
}
