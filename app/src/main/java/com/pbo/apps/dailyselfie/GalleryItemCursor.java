package com.pbo.apps.dailyselfie;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 *  A class to wrap our cursor and reduce calls to getColumnIndex by caching them
 */
class GalleryItemCursor {
    private static int CURSOR_INDEX_ID;
    private static int CURSOR_INDEX_PATH;

    final private Cursor mCursor;

    GalleryItemCursor(Cursor cursor) {
        mCursor = cursor;

        if (mCursor != null) {
            CURSOR_INDEX_PATH = mCursor.getColumnIndex(MainActivity.IMAGE_DATA);
            CURSOR_INDEX_ID = mCursor.getColumnIndex(MainActivity.IMAGE_ID);
        }
    }

    String getImagePath() {
        return mCursor == null ?
                null : mCursor.getString(CURSOR_INDEX_PATH);
    }

    long getImageID() throws NumberFormatException {
        return mCursor == null ?
                0 : Long.parseLong(mCursor.getString(CURSOR_INDEX_ID));
    }

    int getCount() {
        return mCursor == null ?
                0 : mCursor.getCount();
    }

    Cursor getWrappedCursor() {
        return mCursor;
    }

    void moveToPosition(int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
        }
    }
}
