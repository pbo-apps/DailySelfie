package com.pbo.apps.dailyselfie;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Fragment to display an image full screen
 */
public class ImageViewerFragment extends Fragment {

    public static final String ARG_IMAGE_PATH = "com.pbo.apps.dailyselfie.imageviewer.photopath";
    AppCompatImageView mImageView;
    private String mImagePath;
    private OnCropImageListener mCropImageCallback;
    private OnEditImageListener mEditImageCallback;

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
            mEditImageCallback = (OnEditImageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCropImageListener and OnEditImageListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_viewer_layout, container, false);

        mImageView = (AppCompatImageView) view.findViewById(R.id.image_viewer_view);
        displayImage();

        registerForContextMenu(view);

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
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.image_viewer_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_crop:
                mCropImageCallback.dispatchCropPictureIntent(Uri.parse(mImagePath));
                return true;
            case R.id.action_edit:
                mEditImageCallback.dispatchEditPictureIntent(Uri.parse(mImagePath));
                return true;
            case R.id.action_delete:
                // TODO: Implement delete using gallery functionality
                Toast.makeText(getContext(), "Please let me delete this image", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
