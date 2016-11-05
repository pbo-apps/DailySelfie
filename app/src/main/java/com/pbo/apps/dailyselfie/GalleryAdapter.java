package com.pbo.apps.dailyselfie;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Implementation of adapter to put images into a gallery using a RecyclerView
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private GalleryItem[] mGalleryItems;

    public GalleryAdapter(GalleryItem[] galleryItems) {
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
        viewHolder.mImageView.setImageBitmap(mGalleryItems[position].getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return mGalleryItems.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mImageView;

        ViewHolder(View galleryItem) {
            super(galleryItem);
            mImageView = (AppCompatImageView) galleryItem.findViewById(R.id.imageView);
        }
    }
}
