package com.pbo.apps.dailyselfie;

import android.net.Uri;

/**
 * Interface to allow fragments to call an activity's dispatchCropPictureIntent method
 */
interface OnCropImageListener {
    void dispatchCropPictureIntent(Uri photoUri);
}
