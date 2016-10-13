package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static java.text.DateFormat.getDateTimeInstance;

/**
 * Class to deal with all the file IO interactions required
 * when dealing with photos
 *
 * Created by Peter on 13/10/2016.
 */

class ImageFileHelper {
    private static final String SELFIE_FILE_PREFIX = "SELFIE";

    private String mCurrentPhotoPath;

    // Tries to create a file, and if successful returns a URI for it from the fileprovider
    // Returns null otherwise
    Uri createPhotoFileURI(Context context) {
        Uri photoURI = null;
        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(context, R.string.error_create_file, Toast.LENGTH_LONG).show();
        }

        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(context,
                    "com.pbo.apps.fileprovider",
                    photoFile);
        }

        return photoURI;
    }

    // Creates a temporary file in public storage with a unique filename
    // NOTE: This requires permission WRITE_EXTERNAL_STORAGE
    private File createImageFile() throws IOException {
        // Create an image file name
        getDateTimeInstance();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.UK).format(new Date());
        String imageFileName = SELFIE_FILE_PREFIX + "_" + timeStamp;
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
}
