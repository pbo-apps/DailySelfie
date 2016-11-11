package com.pbo.apps.dailyselfie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to display an image full screen
 */
public class ImageViewerFragment extends Fragment {

    AppCompatImageView mImageView;

    public ImageViewerFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_viewer_layout, container, false);

        mImageView = (AppCompatImageView) view.findViewById(R.id.image_viewer_view);

        //mImageView.setImageBitmap();

        return view;
    }

}
