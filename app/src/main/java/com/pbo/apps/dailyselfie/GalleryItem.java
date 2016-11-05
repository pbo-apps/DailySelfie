package com.pbo.apps.dailyselfie;

import android.graphics.Bitmap;

/**
 *  A class to hold the data pertaining to an item in the gallery
 */

class GalleryItem {
    private Bitmap mImageBitmap;

    GalleryItem(Bitmap imageBitmap) {
        mImageBitmap = imageBitmap;
    }

    Bitmap getImageBitmap() {
        return mImageBitmap;
    }
}
