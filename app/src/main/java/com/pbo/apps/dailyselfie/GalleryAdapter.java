package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of adapter to put images into a gallery using a RecyclerView
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private Context mContext;
    private GalleryItemCursor mCursor;
    private OnViewImageListener mViewImageCallback;
    private OnEditImageListener mEditImageCallback;
    private ActionMode.Callback mActionModeCallback;
    ActionMode mActionMode;
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray();

    GalleryAdapter(Context context,
                   OnViewImageListener viewImageCallback,
                   OnEditImageListener editImageCallback,
                   ActionMode.Callback actionModeCallback) {
        mContext = context;
        mViewImageCallback = viewImageCallback;
        mEditImageCallback = editImageCallback;
        mActionModeCallback = actionModeCallback;
        mCursor = new GalleryItemCursor(null);
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View galleryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item_layout, parent, false);

        return new ViewHolder(galleryView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
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

        viewHolder.mItemState.setActivated(mSelectedItems.get(position, false));
        viewHolder.mBitmapLoaderTask.execute(photoID);

        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActionMode == null)
                    mViewImageCallback.viewImage(photoPath);
                else
                    updateSelection(viewHolder.getAdapterPosition(), viewHolder.mItemState);
            }
        });
        viewHolder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mActionMode == null) {
                    mActionMode = ((AppCompatActivity) mContext).startSupportActionMode(mActionModeCallback);
                    updateSelection(viewHolder.getAdapterPosition(), viewHolder.mItemState);
                }
                return true;
            }
        });
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

    // Called when a user clicks an item in action mode
    private void updateSelection(int index, View stateIndicator) {
        stateIndicator.setActivated(!stateIndicator.isActivated());
        this.toggleSelection(index);
        int selectedItems = this.getSelectedItemCount();
        if (selectedItems > 0) {
            String title = mContext.getString(
                    R.string.selected_count,
                    selectedItems);
            mActionMode.setTitle(title);
        } else {
            mActionMode.finish();
        }
    }

    // Toggle the selection state of the view at the given position
    private void toggleSelection(int pos) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        }
        else {
            mSelectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    // Clear all currently selected views
    void clearSelections() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    // Get the number of items currently selected
    int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    // Return a list of all items marked as selected
    List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    // Delete item at the given position
    void delete(Integer position) {
        mCursor.moveToPosition(position);
        Uri photoUri = Uri.parse(mCursor.getImagePath());
        if (ImageFileHelper.deleteFile(photoUri)) {
            ((MainActivity) mContext).updateGalleryImage(photoUri);
            notifyItemRemoved(position);
        }
    }

    // ViewHolder implementation to reduce view creation/deletion
    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mProgressIcon;
        RelativeLayout mItemState;
        AppCompatImageView mImageView;
        BitmapLoaderTask mBitmapLoaderTask;

        ViewHolder(View galleryItem) {
            super(galleryItem);
            mImageView = (AppCompatImageView) galleryItem.findViewById(R.id.image);
            mProgressIcon = (RelativeLayout) galleryItem.findViewById(R.id.progress_icon);
            mItemState = (RelativeLayout) galleryItem.findViewById(R.id.gallery_item_state);
        }
    }
}
