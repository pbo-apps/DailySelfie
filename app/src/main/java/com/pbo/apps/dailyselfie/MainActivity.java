package com.pbo.apps.dailyselfie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String CURRENT_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private static final String CURRENT_PHOTO_URI_KEY = "mCurrentPhotoUri";

    GalleryFragment mGalleryFragment;

    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        mGalleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
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

    // Sends the user to the crop activity to ensure they return a square photo
    private void dispatchCropPictureIntent() {
        CropImage.activity(mCurrentPhotoUri)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputUri(mCurrentPhotoUri)
                .start(this);
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
