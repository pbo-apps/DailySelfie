package com.pbo.apps.dailyselfie;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 *  A class to wrap our cursor and reduce calls to getColumnIndex by caching them
 */
class GalleryItemCursor extends CursorWrapper {
    private static int CURSOR_INDEX_ID;
    private static int CURSOR_INDEX_PATH;

    GalleryItemCursor(Cursor cursor) {
        super(cursor);

        if (cursor != null) {
            CURSOR_INDEX_PATH = cursor.getColumnIndex(MainActivity.IMAGE_DATA);
            CURSOR_INDEX_ID = cursor.getColumnIndex(MainActivity.IMAGE_ID);
        }
    }

    String getImagePath() {
        return getWrappedCursor() == null ?
                null : ImageFileHelper.getFilePath(getWrappedCursor().getString(CURSOR_INDEX_PATH));
    }

    long getImageID() throws NumberFormatException {
        return getWrappedCursor() == null ?
                0 : Long.parseLong(getWrappedCursor().getString(CURSOR_INDEX_ID));
    }

    @Override
    public int getCount() {
        return getWrappedCursor() == null ?
                0 : getWrappedCursor().getCount();
    }
}
