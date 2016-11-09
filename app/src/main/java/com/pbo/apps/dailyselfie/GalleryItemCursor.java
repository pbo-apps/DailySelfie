package com.pbo.apps.dailyselfie;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.MediaStore;

/**
 *  A class to wrap our cursor and reduce calls to getColumnIndex by caching them
 */
class GalleryItemCursor extends CursorWrapper {
    private static int CURSOR_INDEX_PATH;

    GalleryItemCursor(Cursor cursor) {
        super(cursor);

        if (cursor != null)
            CURSOR_INDEX_PATH = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
    }

    String getImagePath() {
        return getWrappedCursor() == null ?
                null : ImageFileHelper.getFilePath(getWrappedCursor().getString(CURSOR_INDEX_PATH));
    }

    @Override
    public int getCount() {
        return getWrappedCursor() == null ?
                0 : getWrappedCursor().getCount();
    }
}
