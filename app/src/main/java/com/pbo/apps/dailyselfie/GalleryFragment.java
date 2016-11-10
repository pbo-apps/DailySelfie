package com.pbo.apps.dailyselfie;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;

/**
 * Fragment class to handle displaying a grid of images
 */
public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int IMAGE_LOADER_ID = 0;
    static final String[] IMAGE_FILE_PROJECTION = { MediaStore.Images.Media.DATA };
    public static final String IMAGE_SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    RecyclerView mGalleryView;
    GalleryAdapter mGalleryAdapter;

    CursorLoader mGalleryItemLoader;

    public GalleryFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_layout, container, false);

        mGalleryView = (RecyclerView) view.findViewById(R.id.gallery_view);

        mGalleryView.setLayoutManager(new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.grid_layout_items_per_row)));
        mGalleryAdapter = new GalleryAdapter(getContext());
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
            String selection = MediaStore.Images.Media.DATA + " LIKE '" + imagesDirectory + "%'";
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
}
