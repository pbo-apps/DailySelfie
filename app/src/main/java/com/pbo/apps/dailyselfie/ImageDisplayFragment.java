package com.pbo.apps.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Fragment class to handle setting an image into a view
 */
public class ImageDisplayFragment extends Fragment {
    RecyclerView mGalleryView;
    GalleryAdapter mGalleryAdapter;
    ArrayList<GalleryItem> mGalleryItems = new ArrayList<>();

    public ImageDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGalleryView = (RecyclerView) view.findViewById(R.id.gallery_view);

        mGalleryView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGalleryAdapter = new GalleryAdapter(mGalleryItems);
        mGalleryView.setAdapter(mGalleryAdapter);
        mGalleryView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    public void addThumbnailToGallery(String photoPath) {
        //mGalleryAdapter.addThumbnail(photoPath);
        mGalleryAdapter.setPic(getContext(), photoPath);
    }
}
