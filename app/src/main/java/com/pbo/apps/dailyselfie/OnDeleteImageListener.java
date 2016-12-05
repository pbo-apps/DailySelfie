package com.pbo.apps.dailyselfie;

import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;

/**
 * Interface to allow fragments to call an activity's deleteImagesDialog method
 */
interface OnDeleteImageListener {
    void deleteImagesDialog(final String[] imageFilesToDelete, @Nullable final ActionMode mode);
}
