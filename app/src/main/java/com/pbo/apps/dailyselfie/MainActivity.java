package com.pbo.apps.dailyselfie;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements OnViewImageListener, OnCropImageListener, OnEditImageListener, OnDeleteImageListener {
    public static final int IMAGE_LOADER_ID = 0;
    public static final Uri IMAGE_STORE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String IMAGE_DATA = MediaStore.Images.Media.DATA;
    public static final String IMAGE_ID = MediaStore.Images.Media._ID;
    public static final String[] IMAGE_FILE_PROJECTION = { IMAGE_DATA, IMAGE_ID };
    public static final String IMAGE_SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String CURRENT_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private static final String CURRENT_PHOTO_URI_KEY = "mCurrentPhotoUri";
    private static final boolean DEVELOPER_MODE = true;

    static final String GALLERY_FRAGMENT_TAG = "com.pbo.apps.dailyselfie.galleryfragment";
    GalleryFragment mGalleryFragment;
    static final String IMAGE_VIEWER_FRAGMENT_TAG = "com.pbo.apps.dailyselfie.imageviewerfragment";
    ImageViewerFragment mImageViewerFragment;

    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoUri;
    private FloatingActionButton mFabCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFabCamera = (FloatingActionButton) findViewById(R.id.fab);
        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        initialiseFragments(savedInstanceState);
    }

    // Set up the fragments to use in the main content view
    private void initialiseFragments(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Gallery Fragment to be placed in the activity layout
            mGalleryFragment = new GalleryFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mGalleryFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mGalleryFragment, GALLERY_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null) {
            savedInstanceState.putString(CURRENT_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
        if (mCurrentPhotoUri != null) {
            savedInstanceState.putParcelable(CURRENT_PHOTO_URI_KEY, mCurrentPhotoUri);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CURRENT_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CURRENT_PHOTO_URI_KEY)) {
            mCurrentPhotoUri = savedInstanceState.getParcelable(CURRENT_PHOTO_URI_KEY);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    // Get some handler on the device to take a photo, if such a thing exists
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = ImageFileHelper.createPhotoFile(this);

            // Continue only if we successfully created a file
            if (photoFile != null) {
                // Get the image Uri
                mCurrentPhotoUri = ImageFileHelper.getFileUri(this, photoFile);
                // Store photo path for use later when the camera intent returns
                mCurrentPhotoPath = ImageFileHelper.getFilePath(photoFile);

                // Ask a local camera to fill in the image for us
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                ImageFileHelper.grantURIPermissionsForIntent(this, takePictureIntent, mCurrentPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Sends the user to the crop activity to ensure they return a square photo
    @Override
    public void dispatchCropPictureIntent(Uri photoUri) {
        CropImage.activity(photoUri)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputUri(photoUri)
                .start(this);
    }

    // Let the user choose an app to edit the photo
    @Override
    public void dispatchEditPictureIntent(Uri photoUri) {
        Intent intentViewImage = new Intent()
                .setAction(Intent.ACTION_EDIT)
                .setDataAndType(photoUri, "image/*");
        startActivity(intentViewImage);
    }

    // Signal the cursor to update the gallery
    public void updateGalleryImage(Uri photoUri) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));
    }

    // Switch in the image viewer fragment and display referenced image file
    @Override
    public void viewImage(String photoPath) {
        if (mImageViewerFragment == null) {
            mImageViewerFragment = new ImageViewerFragment();
        }
        Bundle args = new Bundle();
        args.putString(ImageViewerFragment.ARG_IMAGE_PATH, photoPath);
        mImageViewerFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mImageViewerFragment, IMAGE_VIEWER_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();

        hideCamera();
    }

    void hideCamera() {
        if (mFabCamera != null && mFabCamera.getVisibility() != View.GONE)
            mFabCamera.setVisibility(View.GONE);
    }

    void showCamera() {
        if (mFabCamera != null && mFabCamera.getVisibility() != View.VISIBLE)
            mFabCamera.setVisibility(View.VISIBLE);
    }

    // Give user the option to continue with the delete action or cancel
    public void deleteImagesDialog(final String[] imageFilesToDelete, @Nullable final OnCompleteImageDeleteListener imageDeleteListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete");
        int totalSelectedItems = imageFilesToDelete.length;
        String message = totalSelectedItems > 1 ?
                getString(R.string.multi_delete_are_you_sure, totalSelectedItems) : getString(R.string.single_delete_are_you_sure);
        alert.setMessage(message);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImageFiles(imageFilesToDelete);
                dialog.dismiss();
                if (imageDeleteListener != null) {
                    imageDeleteListener.afterImageDelete();
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    // Delete images directly from the media store - this will delete the file and notify loaders watching the media store
    private void deleteImageFiles(String[] imageFilesToDelete) {
        // Query for the ID of the media matching the file paths
        ContentResolver contentResolver = getContentResolver();
        String selection = IMAGE_DATA + " IN (" + makeSQLPlaceholders(imageFilesToDelete.length) + ")";
        Cursor c = contentResolver.query(IMAGE_STORE_URI, new String[] { IMAGE_ID }, selection, imageFilesToDelete, null);
        if (c == null) {
            Toast.makeText(this, R.string.failed_delete_image, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            while (c.moveToNext()) {
                // We found the ID. Deleting the item via the content provider will also remove the file
                long id = c.getLong(c.getColumnIndexOrThrow(IMAGE_ID));
                Uri deleteUri = ContentUris.withAppendedId(IMAGE_STORE_URI, id);
                contentResolver.delete(deleteUri, null, null);
            }
        }
        finally {
            c.close();
        }
    }

    // Create a string containing placeholder for SQL query parameters
    private String makeSQLPlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                dispatchCropPictureIntent(mCurrentPhotoUri);
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageViewerFragment imageViewerFragment = (ImageViewerFragment) getSupportFragmentManager().
                        findFragmentByTag(IMAGE_VIEWER_FRAGMENT_TAG);
                if (imageViewerFragment != null && imageViewerFragment.isVisible()) {
                    mImageViewerFragment.refreshImage();
                    updateGalleryImage(result.getUri());
                } else {
                    updateGalleryImage(Uri.parse(mCurrentPhotoPath));
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
