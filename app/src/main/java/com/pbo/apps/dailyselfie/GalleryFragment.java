package com.pbo.apps.dailyselfie;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
    public static final int IMAGE_LOADER_ID = 0;
    public static final String[] IMAGE_FILE_PROJECTION = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
    public static final String IMAGE_SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    public static final String IMAGE_DATA = MediaStore.Images.Media.DATA;
    public static final String IMAGE_ID = MediaStore.Images.Media._ID;

    RecyclerView mGalleryView;
    GalleryAdapter mGalleryAdapter;

    CursorLoader mGalleryItemLoader;
    private OnViewImageListener mViewImageCallback;
    private OnEditImageListener mEditImageCallback;

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
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnViewImageListener and OnEditImageListener");
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

        getLoaderManager().initLoader(IMAGE_LOADER_ID, null, this);

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
            String selection = IMAGE_DATA + " LIKE '" + imagesDirectory + "%'";
            mGalleryItemLoader = new CursorLoader(getContext(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_FILE_PROJECTION,
                    selection,
                    null,
                    IMAGE_SORT_ORDER);
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
        inflater.inflate(R.menu.menu_gallery_item_select, menu);
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
                mGalleryAdapter.selectAll();
                return true;

            default:
                return false;
        }
    }

    // Give user the option to continue with the delete action or cancel
    void deleteGalleryItems(final ActionMode mode) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete");
        int totalSelectedItems = mGalleryAdapter.getSelectedItemCount();
        String message = totalSelectedItems > 1 ?
                getString(R.string.multi_delete_are_you_sure, totalSelectedItems) : getString(R.string.single_delete_are_you_sure);
        alert.setMessage(message);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Integer> selectedItemPositions = mGalleryAdapter.getSelectedItems();
                // for loop to delete items
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    mGalleryAdapter.delete(selectedItemPositions.get(i));
                }
                dialog.dismiss();
                mode.finish(); // Action picked, so close the CAB
            }
        });
        alert.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mode.finish(); // Action picked, so close the CAB
            }
        });

        alert.show();
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mGalleryAdapter.mActionMode = null;
        mGalleryAdapter.clearSelections();
    }
}
