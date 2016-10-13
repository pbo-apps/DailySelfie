package com.pbo.apps.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImageDisplayFragment extends Fragment {
    AppCompatImageView mImageView;

    public ImageDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceStatem) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // TODO: Figure out why the image view has 0 height and width
        mImageView = (AppCompatImageView) view.findViewById(R.id.image);

        return view;
    }

    public void setImage(Bitmap imageBitmap) {
        mImageView.setImageBitmap(imageBitmap);
    }

    void setPic(String photoPath) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Don't attempt to do anything if the view is one-dimensional
        if (targetW == 0 || targetH == 0)
            return;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            InputStream in = getContext().getContentResolver().openInputStream(
                    Uri.parse(photoPath));
            BitmapFactory.decodeStream(in, null, bmOptions);
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
            return;
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap;
        try {
            InputStream in = getContext().getContentResolver().openInputStream(
                    Uri.parse(photoPath));
            bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
            return;
        }
        mImageView.setImageBitmap(bitmap);
    }
}
