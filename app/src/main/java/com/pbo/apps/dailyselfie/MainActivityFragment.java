package com.pbo.apps.dailyselfie;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    AppCompatImageView mImageView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mImageView = (AppCompatImageView) view.findViewById(R.id.image);

        return view;
    }

    public void setImage(Bitmap imageBitmap) {
        mImageView.setImageBitmap(imageBitmap);
    }
}
