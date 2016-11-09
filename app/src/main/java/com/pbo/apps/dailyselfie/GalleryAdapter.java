package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Implementation of adapter to put images into a gallery using a RecyclerView
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final int mImageSize = 100;
    private Context mContext;
    private GalleryItemCursor mCursor;

    GalleryAdapter(Context context) {
        mContext = context;
        mCursor = new GalleryItemCursor(null);
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View galleryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item_layout, parent, false);

        return new ViewHolder(galleryView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        // Find the gallery item
        String photoPath = mCursor.getImagePath();

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
        return mCursor == null ? 0 : mCursor.getCount();
    }

    // Replace the existing cursor in the cursor wrapper
    Cursor swapCursor(Cursor cursor) {
        if (mCursor.getWrappedCursor() == cursor) {
            return null;
        }
        Cursor oldCursor = mCursor.getWrappedCursor();
        mCursor = new GalleryItemCursor(cursor);
        if (cursor != null) {
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    // ViewHolder implementation to reduce view creation/deletion
    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageView;

        ViewHolder(View galleryItem) {
            super(galleryItem);
            mImageView = (AppCompatImageView) galleryItem.findViewById(R.id.image);
        }
    }
}
