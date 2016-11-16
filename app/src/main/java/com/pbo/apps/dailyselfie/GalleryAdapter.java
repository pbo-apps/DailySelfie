package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Implementation of adapter to put images into a gallery using a RecyclerView
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
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
        final String photoPath = mCursor.getImagePath();
        long photoID;
        try {
            photoID = mCursor.getImageID();
        } catch (NumberFormatException e) {
            photoID = 0;
        }

        // Either create a loader if this view hasn't got one yet, or cancel the existing load so we
        // can start loading the new image
        if (viewHolder.mBitmapLoaderTask == null) {
            viewHolder.mBitmapLoaderTask = new BitmapLoaderTask(mContext,
                    viewHolder.mImageView,
                    viewHolder.mProgressIcon);
        } else if (viewHolder.mBitmapLoaderTask.getStatus() == AsyncTask.Status.RUNNING
                ||
                viewHolder.mBitmapLoaderTask.getStatus() == AsyncTask.Status.PENDING
                ||
                viewHolder.mBitmapLoaderTask.getStatus() == AsyncTask.Status.FINISHED) {
            viewHolder.mBitmapLoaderTask.cancel(true);
            viewHolder.mBitmapLoaderTask = null;
            viewHolder.mProgressIcon.setVisibility(View.VISIBLE);
            viewHolder.mBitmapLoaderTask = new BitmapLoaderTask(mContext,
                    viewHolder.mImageView,
                    viewHolder.mProgressIcon);
        }

        viewHolder.mBitmapLoaderTask.execute(photoID);

        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).viewImage(photoPath);
            }
        });
        viewHolder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startImageActivity(photoPath, Intent.ACTION_EDIT);
                return true;
            }
        });
    }

    // Start an activity to do something with this image file
    private void startImageActivity(String photoPath, String action) {
        Intent intentViewImage = new Intent()
                .setAction(action)
                .setDataAndType(Uri.parse(photoPath), "image/*");
        mContext.startActivity(intentViewImage);
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
        RelativeLayout mProgressIcon;
        AppCompatImageView mImageView;
        BitmapLoaderTask mBitmapLoaderTask;

        ViewHolder(View galleryItem) {
            super(galleryItem);
            mImageView = (AppCompatImageView) galleryItem.findViewById(R.id.image);
            mProgressIcon = (RelativeLayout) galleryItem.findViewById(R.id.progress_icon);
        }
    }
}
