package com.pbo.apps.dailyselfie;

/**
 * Interface to allow fragments to call an activity's viewImage method
 */
interface OnViewImageListener {
    void viewImage(String photoPath);
}
