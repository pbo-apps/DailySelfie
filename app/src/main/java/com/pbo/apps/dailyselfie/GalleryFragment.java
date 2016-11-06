package com.pbo.apps.dailyselfie;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Fragment class to handle displaying a grid of images
 */
public class GalleryFragment extends Fragment {
    RecyclerView mGalleryView;
    GalleryAdapter mGalleryAdapter;
    ArrayList<GalleryItem> mGalleryItems;

    public GalleryFragment() {
        if (mGalleryItems == null) {
            mGalleryItems = new ArrayList<>();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mGalleryView = (RecyclerView) view.findViewById(R.id.gallery_view);

        mGalleryView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGalleryAdapter = new GalleryAdapter(getContext(), mGalleryItems);
        mGalleryView.setAdapter(mGalleryAdapter);
        mGalleryView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    public void addThumbnailToGallery(String photoPath) {
        mGalleryAdapter.addImage(photoPath);
    }
}
