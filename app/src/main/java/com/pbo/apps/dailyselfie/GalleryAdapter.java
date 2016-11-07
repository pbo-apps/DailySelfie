package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Implementation of adapter to put images into a gallery using a RecyclerView
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final int mImageSize = 100;
    private Context mContext;
    private ArrayList<GalleryItem> mGalleryItems;

    GalleryAdapter(Context context, ArrayList<GalleryItem> galleryItems) {
        mContext = context;
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
        // Find the gallery item
        String photoPath = mGalleryItems.get(position).getImagePath();

        // Get the dimensions of the View
        int targetW = mImageSize;
        int targetH = mImageSize;

        Bitmap bitmap = null;
        int scaleFactor = ImageFileHelper.calculateBitmapScaleFactor(mContext, photoPath, targetW, targetH);

        if (scaleFactor > 0)
            bitmap = ImageFileHelper.scaleBitmap(mContext, photoPath, scaleFactor);

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
    }

    void addImage(String photoPath) {
        // Warn user if we've lost the photo somewhere along the way
        if (photoPath == null) {
            Toast.makeText(mContext, R.string.error_open_photo_file, Toast.LENGTH_LONG).show();
            return;
        }

        mGalleryItems.add(new GalleryItem(photoPath));
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
