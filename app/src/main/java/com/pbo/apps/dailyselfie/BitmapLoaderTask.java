package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Background task to load a bitmap into an image view asynchronously
 */

class BitmapLoaderTask extends AsyncTask<Long, Void, Bitmap> {
    private final Context mContext;
    private final WeakReference<ImageView> mImageViewReference;

    BitmapLoaderTask(Context context, ImageView imageView) {
        mContext = context;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        mImageViewReference = new WeakReference<>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Long... params) {
        Bitmap bitmap = null;

        if (params.length > 0)
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    mContext.getContentResolver(), params[0],
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null );

        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        final ImageView imageView = mImageViewReference.get();
        if (imageView != null) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.color.colorAccent);
            }
        }
    }
}
