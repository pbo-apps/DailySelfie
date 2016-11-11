package com.pbo.apps.dailyselfie;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to display an image full screen
 */
public class ImageViewerFragment extends Fragment {

    public static final String ARG_IMAGE_PATH = "com.pbo.apps.dailyselfie.imageviewer.photopath";
    AppCompatImageView mImageView;
    private String mImagePath;

    public ImageViewerFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_IMAGE_PATH))
            mImagePath = getArguments().getString(ARG_IMAGE_PATH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_viewer_layout, container, false);

        mImageView = (AppCompatImageView) view.findViewById(R.id.image_viewer_view);

        // Get the dimensions of the View
        int targetW = container.getWidth();
        int targetH = container.getHeight();

        Bitmap bitmap = null;
        int scaleFactor = ImageFileHelper.calculateBitmapScaleFactor(getContext(), mImagePath, targetW, targetH);

        if (scaleFactor > 0)
            bitmap = ImageFileHelper.scaleBitmap(getContext(), mImagePath, scaleFactor);

        if (bitmap != null)
            mImageView.setImageBitmap(bitmap);

        return view;
    }

}
