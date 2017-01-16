package com.pbo.apps.dailyselfie;

import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link GalleryItemCursor}
 */
@RunWith(MockitoJUnitRunner.class)
public class GalleryItemCursorTest {
    @Mock
    Cursor mMockCursor;

    @Test
    public void getImagePathWithNullCursorReturnsNull() {
        GalleryItemCursor galleryItemCursor = new GalleryItemCursor(null);

        assertThat(galleryItemCursor.getImagePath(), isEmptyOrNullString());
    }

    @Test
    public void getImagePathWithLoadedCursorReturnsCorrectImagePath() {
        final int cursorIndexPath = 0;
        when(mMockCursor.getColumnIndex(MainActivity.IMAGE_DATA))
                .thenReturn(cursorIndexPath);
        final String imagePath = "\\\\I\\am\\a\\path";
        when(mMockCursor.getString(cursorIndexPath))
                .thenReturn(imagePath);
        GalleryItemCursor galleryItemCursor = new GalleryItemCursor(mMockCursor);

        assertThat("image path", galleryItemCursor.getImagePath(), is(equalTo(imagePath)));
    }

    @Test
    public void getCountWithNullCursorReturnsZero() {
        GalleryItemCursor galleryItemCursor = new GalleryItemCursor(null);

        assertThat(galleryItemCursor.getCount(), is(equalTo(0)));
    }

    @Test
    public void getCountWithEmptyCursorReturnsZero() {
        final int cursorCount = 0;
        when(mMockCursor.getCount())
                .thenReturn(cursorCount);
        GalleryItemCursor galleryItemCursor = new GalleryItemCursor(null);

        assertThat(galleryItemCursor.getCount(), is(equalTo(cursorCount)));
    }

    @Test
    public void getCountWithLoadedCursorReturnsCorrectCount() {
        final int cursorCount = 5;
        when(mMockCursor.getCount())
                .thenReturn(cursorCount);
        GalleryItemCursor galleryItemCursor = new GalleryItemCursor(mMockCursor);

        assertThat(galleryItemCursor.getCount(), is(equalTo(cursorCount)));
    }

    @Test
    public void getImagePath() throws Exception {

    }

    @Test
    public void getImageID() throws Exception {

    }

    @Test
    public void getCount() throws Exception {

    }

}