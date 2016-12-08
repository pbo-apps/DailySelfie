package com.pbo.apps.dailyselfie;

import android.support.annotation.Nullable;

/**
 * Interface to allow fragments to call an activity's deleteImagesDialog method
 */
interface OnDeleteImageListener {
    void deleteImagesDialog(final String[] imageFilesToDelete, @Nullable final OnCompleteImageDeleteListener imageDeleteListener);
}
