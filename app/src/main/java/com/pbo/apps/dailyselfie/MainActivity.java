package com.pbo.apps.dailyselfie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnViewImageListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String CURRENT_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private static final String CURRENT_PHOTO_URI_KEY = "mCurrentPhotoUri";
    private static final boolean DEVELOPER_MODE = true;

    GalleryFragment mGalleryFragment;
    ImageViewerFragment mImageViewerFragment;

    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoUri;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
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
                    .add(R.id.fragment_container, mGalleryFragment).commit();

            // Create new ImageViewerFragment for use later when viewing images
            mImageViewerFragment = new ImageViewerFragment();
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
    private void dispatchCropPictureIntent() {
        CropImage.activity(mCurrentPhotoUri)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputUri(mCurrentPhotoUri)
                .start(this);
    }

    // Switch in the image viewer fragment and display referenced image file
    public void viewImage(String photoPath) {
        if (mImageViewerFragment == null) {
            mImageViewerFragment = new ImageViewerFragment();
        }
        Bundle args = new Bundle();
        args.putString(ImageViewerFragment.ARG_IMAGE_PATH, photoPath);
        mImageViewerFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mImageViewerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                dispatchCropPictureIntent();
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Signal the cursor to update the gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mCurrentPhotoPath)));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
