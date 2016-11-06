package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Implementation of adapter to put images into a gallery using a RecyclerView
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final int THUMBSIZE = 96;
    private ArrayList<GalleryItem> mGalleryItems;

    GalleryAdapter(ArrayList<GalleryItem> galleryItems) {
        mGalleryItems = galleryItems;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View galleryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item_layout, parent, false);

        return new ViewHolder(galleryView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mImageView.setImageBitmap(mGalleryItems.get(position).getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }

    public void addThumbnail(String photoPath) {
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photoPath), THUMBSIZE, THUMBSIZE);
        mGalleryItems.add(new GalleryItem(thumbImage));
        notifyItemInserted(mGalleryItems.size() - 1);
    }

    void setPic(Context context, String photoPath) {
        // Warn user if we've lost the photo somewhere along the way
        if (photoPath == null) {
            Toast.makeText(context, R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
            return;
        }

        // Get the dimensions of the View
        int targetW = THUMBSIZE; //mImageView.getWidth();
        int targetH = THUMBSIZE; //mImageView.getHeight();

        // Don't attempt to do anything if the view is one-dimensional
        if (targetW == 0 || targetH == 0)
            return;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            InputStream in = context.getContentResolver().openInputStream(
                    Uri.parse(photoPath));
            BitmapFactory.decodeStream(in, null, bmOptions);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
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
            InputStream in = context.getContentResolver().openInputStream(
                    Uri.parse(photoPath));
            bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
            return;
        }
        mGalleryItems.add(new GalleryItem(bitmap));
        notifyItemInserted(mGalleryItems.size() - 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageView;

        ViewHolder(View galleryItem) {
            super(galleryItem);
            mImageView = (AppCompatImageView) galleryItem.findViewById(R.id.image);
        }
    }
}
