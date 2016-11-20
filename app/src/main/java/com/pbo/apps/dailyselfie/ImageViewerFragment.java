package com.pbo.apps.dailyselfie;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
    private OnCropImageListener mCropImageCallback;

    public ImageViewerFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_IMAGE_PATH))
            mImagePath = getArguments().getString(ARG_IMAGE_PATH);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCropImageCallback = (OnCropImageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCropImageListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_viewer_layout, container, false);

        mImageView = (AppCompatImageView) view.findViewById(R.id.image_viewer_view);
        displayImage();


        return view;
    }

    // Public function to allow the activity to force a redraw of the image
    public void refreshImage() {
        displayImage();
    }

    // Post runnable to display image so that the view is measured before we work out the scale factor
    private void displayImage() {
        if (mImageView == null)
            return;

        mImageView.post(new Runnable() {
            @Override
            public void run() {
                // Get the dimensions of the View
                int targetW = mImageView.getWidth();
                int targetH = mImageView.getHeight();

                Bitmap bitmap = null;
                int scaleFactor = ImageFileHelper.calculateBitmapScaleFactor(getContext(), mImagePath, targetW, targetH);

                if (scaleFactor > 0)
                    bitmap = ImageFileHelper.scaleBitmap(getContext(), mImagePath, scaleFactor);

                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                    mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mCropImageCallback.dispatchCropPictureIntent(Uri.parse(mImagePath));
                            return true;
                        }
                    });
                }
            }
        });
    }

}
