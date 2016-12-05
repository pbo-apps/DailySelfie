package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Fragment class to handle displaying a grid of images
 */
public class GalleryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback {

    RecyclerView mGalleryView;
    GalleryAdapter mGalleryAdapter;
    CursorLoader mGalleryItemLoader;
    private OnViewImageListener mViewImageCallback;
    private OnEditImageListener mEditImageCallback;
    private OnDeleteImageListener mDeleteImageCallback;

    public GalleryFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mViewImageCallback = (OnViewImageListener) context;
            mEditImageCallback = (OnEditImageListener) context;
            mDeleteImageCallback = (OnDeleteImageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewImageListener, OnEditImageListener and OnDeleteImageListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);

        mGalleryView = (RecyclerView) view.findViewById(R.id.gallery_view);

        mGalleryView.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.grid_layout_items_per_row)));
        mGalleryAdapter = new GalleryAdapter(getContext(), mViewImageCallback, mEditImageCallback, this);
        mGalleryView.setAdapter(mGalleryAdapter);
        mGalleryView.setItemAnimator(new DefaultItemAnimator());

        getLoaderManager().initLoader(MainActivity.IMAGE_LOADER_ID, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String imagesDirectory = "";
        try {
            imagesDirectory = ImageFileHelper.getImageStorageDirectory(getContext()).getAbsolutePath();
        } catch (IOException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (!imagesDirectory.isEmpty()) {
            String selection = MainActivity.IMAGE_DATA + " LIKE '" + imagesDirectory + "%'";
            mGalleryItemLoader = new CursorLoader(getContext(),
                    MainActivity.IMAGE_STORE_URI,
                    MainActivity.IMAGE_FILE_PROJECTION,
                    selection,
                    null,
                    MainActivity.IMAGE_SORT_ORDER);
        }

        return mGalleryItemLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGalleryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGalleryAdapter.swapCursor(null);
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.gallery_item_select_menu, menu);
        return true;
        }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteGalleryItems(mode);
                return true;

            case R.id.action_select_all:
                mGalleryAdapter.selectAll(mGalleryView);
                return true;

            default:
                return false;
        }
    }

    // Delete all selected items in the gallery
    void deleteGalleryItems(final ActionMode mode) {
        List<Integer> selectedItemPositions = mGalleryAdapter.getSelectedItems();
        mDeleteImageCallback.deleteImagesDialog(mGalleryAdapter.getImagePaths(selectedItemPositions),
                new OnCompleteImageDeleteListener() {
                    @Override
                    public void afterImageDelete() {
                        if (mode != null) {
                            mode.finish();
                        }
                    }
                });
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mGalleryAdapter.mActionMode = null;
        mGalleryAdapter.clearSelections(mGalleryView);
    }
}
