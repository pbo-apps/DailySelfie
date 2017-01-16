package com.pbo.apps.dailyselfie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * Unit test class for {@link GalleryItemCursor}
 */
@RunWith(MockitoJUnitRunner.class)
public class GalleryItemCursorTest {
    @Test
    public void getImagePathWithNullCursorReturnsNull() {
        GalleryItemCursor galleryItemCursor = new GalleryItemCursor(null);

        assertThat(galleryItemCursor.getImagePath(), isEmptyOrNullString());
    }
}