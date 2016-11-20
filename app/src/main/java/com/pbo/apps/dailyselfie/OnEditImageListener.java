package com.pbo.apps.dailyselfie;

import android.net.Uri;

/**
 * Interface to allow fragments to call an activity's dispatchEditPictureIntent method
 */
interface OnEditImageListener {
    void dispatchEditPictureIntent(Uri photoUri);
}
