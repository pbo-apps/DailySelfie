package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

    void addImage(Context context, String photoPath) {
        // Warn user if we've lost the photo somewhere along the way
        if (photoPath == null) {
            Toast.makeText(context, R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
            return;
        }

        // Get the dimensions of the View
        int targetW = THUMBSIZE; //mImageView.getWidth();
        int targetH = THUMBSIZE; //mImageView.getHeight();

        Bitmap bitmap = null;
        int scaleFactor = ImageFileHelper.calculateScaledBitmapOption(context, photoPath, targetW, targetH);

        if (scaleFactor > 0)
            bitmap = ImageFileHelper.scaleBitmap(context, photoPath, scaleFactor);

        if (bitmap != null) {
            mGalleryItems.add(new GalleryItem(bitmap));
            notifyItemInserted(mGalleryItems.size() - 1);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageView;

        ViewHolder(View galleryItem) {
            super(galleryItem);
            mImageView = (AppCompatImageView) galleryItem.findViewById(R.id.image);
        }
    }
}
