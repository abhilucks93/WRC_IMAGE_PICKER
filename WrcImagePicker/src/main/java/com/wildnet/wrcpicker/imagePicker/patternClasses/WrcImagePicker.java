

package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.listeners.OnActionCompleteListener;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;

import java.util.ArrayList;


public class WrcImagePicker {

    public static final String TAG = WrcImagePicker.class.getSimpleName();


    OnActionCompleteListener onActionCompleteListener;
    private static WrcImagePicker mInstance;
    private Activity context;

    private int themeBgColor = Color.parseColor("#40c2c2c2");
    private int themeTextColor = Color.parseColor("#ffffff");
    private boolean isCrop = false;
    private boolean isPreview = false;


    //=============================================================================

    static WrcImagePicker getInstance() {

        if (mInstance == null) {
            synchronized (WrcImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new WrcImagePicker();

                }
            }
        }
        return mInstance;
    }

    public static WrcImagePicker getInstance(Activity context) {
        if (mInstance == null) {
            synchronized (WrcImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new WrcImagePicker();

                }
            }
        }
        mInstance.setContext(context);
        mInstance.setCrop(false);
        mInstance.setPreview(false);
        return mInstance;
    }


    //=============================================================================

    private Boolean cameraDefaultMode = true;
    private Boolean galleryDefaultMode = true;
    private Boolean cameraImageCache = true;
    private String cameraImageName;

    public ClickImage clickImage() {
        cameraDefaultMode = true;
        cameraImageCache = true;
        cameraImageName = "image";
        return new ClickImage();
    }

    void captureImage(OnActionCompleteListener onActionCompleteListener) {
        setOnActionCompleteListener(onActionCompleteListener);
        if (cameraDefaultMode) {
            context.startActivity(new Intent(context, CameraDefaultActivity.class));
        } else {
            context.startActivity(new Intent(context, CameraCustomActivity.class));
        }

    }


    //=============================================================================

    public PickImage pickImage() {

        return new PickImage();
    }

    void fetchImage(OnActionCompleteListener onActionCompleteListener) {
        setOnActionCompleteListener(onActionCompleteListener);
        if (galleryDefaultMode) {
            context.startActivity(new Intent(context, GalleryDefaultActivity.class));
        } else {
            context.startActivity(new Intent(context, GalleryCustomActivity.class)
                    .putExtra("singlePickMode", singlePickMode)
                    .putExtra("isPreview", isPreview)
                    .putExtra("isCrop", isCrop)
                    .putExtra("showCamera", false)
                    .putExtra("maxCount", maxCount));
        }

    }


//=============================================================================

    private Boolean singlePickMode = true;
    private int maxCount = 10;

    public CustomPick customPick() {
        maxCount = 10;
        return new CustomPick();
    }

    void customPickImage(OnActionCompleteListener onActionCompleteListener) {
        setOnActionCompleteListener(onActionCompleteListener);
        context.startActivity(new Intent(context, GalleryCustomActivity.class)
                .putExtra("singlePickMode", singlePickMode)
                .putExtra("isPreview", isPreview)
                .putExtra("isCrop", isCrop)
                .putExtra("showCamera", singlePickMode)
                .putExtra("maxCount", maxCount));
    }


    //=============================================================================

    void imagePickedCallback(ImageItemBean bean) {
        if (bean != null) {
            Log.d(TAG, "success");
            ArrayList<ImageItemBean> beans = new ArrayList<>();
            beans.add(bean);
            onActionCompleteListener._actionCompleted(beans);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }

    void imagesPickedCallback(ArrayList<ImageItemBean> beans) {
        if (beans != null) {
            Log.d(TAG, "success");
            Toast.makeText(context, "" + beans.size() + "Images selected", Toast.LENGTH_SHORT).show();
            onActionCompleteListener._actionCompleted(beans);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }

    void imageCustomPickedCallback(ImageItemBean bean) {
        if (bean != null) {
            Log.d(TAG, "success");
            ArrayList<ImageItemBean> beans = new ArrayList<>();
            beans.add(bean);
            onActionCompleteListener._actionCompleted(beans);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }

    void imageCroppedCallback(ImageItemBean bean) {
        if (bean != null) {
            Log.d(TAG, "success");
            ArrayList<ImageItemBean> beans = new ArrayList<>();
            beans.add(bean);
            onActionCompleteListener._actionCompleted(beans);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }

    void imagePreviewCallback(ImageItemBean bean) {
        if (bean != null) {
            Log.d(TAG, "success");
            ArrayList<ImageItemBean> beans = new ArrayList<>();
            beans.add(bean);
            onActionCompleteListener._actionCompleted(beans);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }


    //=============================================================================

    void setContext(Activity context) {
        this.context = context;
    }

    void setCrop(boolean crop) {
        isCrop = crop;
    }

    void setPreview(boolean preview) {
        isPreview = preview;
    }

    private void setOnActionCompleteListener(OnActionCompleteListener onActionCompleteListener) {
        this.onActionCompleteListener = onActionCompleteListener;
    }

    void setThemeBgColor(int themeBgColor) {
        this.themeBgColor = themeBgColor;
    }

    void setThemeTextColor(int themeTextColor) {
        this.themeTextColor = themeTextColor;
    }

    void setCameraDefaultMode(Boolean cameraDefaultMode) {
        this.cameraDefaultMode = cameraDefaultMode;
    }

    void setGalleryDefaultMode(Boolean galleryDefaultMode) {
        this.galleryDefaultMode = galleryDefaultMode;
    }

    void setCameraImageCache(Boolean cameraImageCache) {
        this.cameraImageCache = cameraImageCache;
    }

    void setCameraImageName(String cameraImageName) {
        this.cameraImageName = cameraImageName;
    }

    void setSinglePickMode(Boolean singlePickMode) {
        this.singlePickMode = singlePickMode;
    }

    void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }


}

