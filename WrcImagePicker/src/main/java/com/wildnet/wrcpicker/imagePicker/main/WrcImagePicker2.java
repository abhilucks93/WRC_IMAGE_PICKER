

package com.wildnet.wrcpicker.imagePicker.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wildnet.wrcpicker.imagePicker.enums.CropperType;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.model.ImageSetBean;
import com.wildnet.wrcpicker.imagePicker.patternClasses.ClickImage;
import com.wildnet.wrcpicker.imagePicker.patternClasses.PickImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class WrcImagePicker2 {

    public static final String TAG = WrcImagePicker2.class.getSimpleName();
    protected static final int REQ_CAMERA = 1431;
    protected static final int REQ_PREVIEW = 2347;
    protected static final String KEY_PIC_PATH = "key_pic_path";
    protected static final String KEY_PIC_SELECTED_POSITION = "key_pic_selected";
    private ImageLoader imageLoader;
    protected static WrcImagePicker2 mInstance;

    protected boolean cropMode = false;
    protected boolean cameraMode = false;
    protected static boolean showCamera = false;
    protected static int themeBgColor = Color.parseColor("#40c2c2c2");
    protected static int themeTextColor = Color.parseColor("#ffffff");
    protected static int cropSize = 60 * 2;
    protected static CropperType cropper = CropperType.ROUND;
    protected static int selectLimit = 10;//can select 9 at most,you can change it yourself

    protected WrcImagePicker2() {

    }

    public static WrcImagePicker2 getInstance() {
        if (mInstance == null) {
            synchronized (WrcImagePicker2.class) {
                if (mInstance == null) {
                    mInstance = new WrcImagePicker2();
                }
            }
        }
        return mInstance;
    }

    protected void setThemeBgColor(int themeBgColor) {
        this.themeBgColor = themeBgColor;
    }

    protected int getThemeBgColor() {
        return themeBgColor;
    }

    protected void setThemeTextColor(int themeTextColor) {
        this.themeTextColor = themeTextColor;
    }

    protected int getThemeTextColor() {
        return themeTextColor;
    }

    protected int getSelectLimit() {
        return selectLimit;
    }

    protected void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    private int selectMode = Select_Mode.MODE_MULTI;//Select mode:single or multi

    protected int getSelectMode() {
        return selectMode;
    }

    private void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    private boolean shouldShowCamera = true;//indicate whether to show the camera item

    protected boolean isShouldShowCamera() {
        return shouldShowCamera;
    }

    private void setShouldShowCamera(boolean shouldShowCamera) {
        this.shouldShowCamera = shouldShowCamera;
    }

    private String mCurrentPhotoPath;//image saving path when taking pictures

    protected String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    /**
     * Listeners of image selected changes,if you want to custom the Activity of ImagesGrid or ImagePreview,you might use it.
     */
    private List<OnImageSelectedChangeListener> mImageSelectedChangeListeners;

    protected void addOnImageSelectedChangeListener(OnImageSelectedChangeListener l) {
        if (l != null) {
            if (mImageSelectedChangeListeners == null) {
                mImageSelectedChangeListeners = new ArrayList<>();
                Log.i(TAG, "=====create new ImageSelectedListener List");
            }
            this.mImageSelectedChangeListeners.add(l);
            Log.i(TAG, "=====addOnImageSelectedChangeListener:" + l.getClass().toString());
        }
    }

    protected void removeOnImageItemSelectedChangeListener(OnImageSelectedChangeListener l) {
        if (l != null) {
            if (mImageSelectedChangeListeners == null) {
                return;
            }
            Log.i(TAG, "=====remove from mImageSelectedChangeListeners:" + l.getClass().toString());
            this.mImageSelectedChangeListeners.remove(l);
        }
    }

    private void notifyImageSelectedChanged(int position, ImageItemBean item, boolean isAdd) {
        if (item != null) {
            if ((isAdd && getSelectImageCount() > selectLimit) || (!isAdd && getSelectImageCount() == selectLimit)) {
                //do not call the listeners if reached the select limit when selecting
                Log.i(TAG, "=====ignore notifyImageSelectedChanged:isAdd?" + isAdd);
            } else {
                if (mImageSelectedChangeListeners == null) {
                    return;
                }
                Log.i(TAG, "=====notify mImageSelectedChangeListeners:item=" + item.path);
                for (OnImageSelectedChangeListener l : mImageSelectedChangeListeners) {
                    l.onImageSelectChange(position, item, mSelectedImages.size(), selectLimit);
                }
            }
        }
    }

    /**
     * listeners of image crop complete
     */
    private List<OnImageCropCompleteListener> mImageCropCompleteListeners;

    protected void addOnImageCropCompleteListener(OnImageCropCompleteListener l) {
        if (l != null) {
            if (mImageCropCompleteListeners == null) {
                mImageCropCompleteListeners = new ArrayList<>();
                Log.i(TAG, "=====create new ImageCropCompleteListener List");
            }
            this.mImageCropCompleteListeners.add(l);
            Log.i(TAG, "=====addOnImageCropCompleteListener:" + l.getClass().toString());
        }
    }

    protected void removeOnImageCropCompleteListener(OnImageCropCompleteListener l) {
        if (l != null) {
            if (mImageCropCompleteListeners == null) {
                return;
            }
            this.mImageCropCompleteListeners.remove(l);
            Log.i(TAG, "=====remove mImageCropCompleteListeners:" + l.getClass().toString());
        }
    }

    protected void notifyImageCropComplete(Bitmap bmp, int ratio) {
        if (bmp != null) {
            if (mImageCropCompleteListeners != null) {
                Log.i(TAG, "=====notify onImageCropCompleteListener  bitmap=" + bmp.toString() + "  ratio=" + ratio);
                for (OnImageCropCompleteListener l : mImageCropCompleteListeners) {
                    l.onImageCropComplete(bmp, ratio);
                }
            }
        }
    }

    /**
     * Listener when image pick completed
     */
    private OnImagePickCompleteListener mOnImagePickCompleteListener;

    protected void setOnImagePickCompleteListener(OnImagePickCompleteListener l) {
        if (l != null) {
            this.mOnImagePickCompleteListener = l;
            Log.i(TAG, "=====setOnImagePickCompleteListener:" + l.getClass().toString());
        }
    }

    protected void deleteOnImagePickCompleteListener(OnImagePickCompleteListener l) {
        if (l != null) {
            if (l.getClass().getName().equals(mOnImagePickCompleteListener.getClass().getName())) {
                mOnImagePickCompleteListener = null;
                Log.i(TAG, "=====remove mOnImagePickCompleteListener:" + l.getClass().toString());
                System.gc();
            }
        }
    }

    protected void notifyOnImagePickComplete() {
        if (mOnImagePickCompleteListener != null) {
            List<ImageItemBean> list = getSelectedImages();
            Log.i(TAG, "=====notify mOnImagePickCompleteListener:selected size=" + list.size());
            mOnImagePickCompleteListener.onImagePickComplete(list);
        }
    }

    //All Images collect by Set
    private List<ImageSetBean> mImageSets;
    private int mCurrentSelectedImageSetPosition = 0;//Item 0: all images

    Set<ImageItemBean> mSelectedImages = new LinkedHashSet<>();

    protected List<ImageSetBean> getImageSets() {
        return mImageSets;
    }

    protected List<ImageItemBean> getImageItemsOfCurrentImageSet() {
        if (mImageSets != null) {
            return mImageSets.get(mCurrentSelectedImageSetPosition).imageItems;
        } else {
            return null;
        }
    }

    protected void setImageSets(List<ImageSetBean> mImageSets) {
        this.mImageSets = mImageSets;
    }

    protected void clearImageSets() {
        if (mImageSets != null) {
            mImageSets.clear();
            mImageSets = null;
        }
    }

    protected int getCurrentSelectedImageSetPosition() {
        return mCurrentSelectedImageSetPosition;
    }

    protected void setCurrentSelectedImageSetPosition(int mCurrentSelectedImageSetPosition) {
        this.mCurrentSelectedImageSetPosition = mCurrentSelectedImageSetPosition;
    }

    protected void addSelectedImageItem(int position, ImageItemBean item) {
        mSelectedImages.add(item);
        Log.i(TAG, "=====addSelectedImageItem:" + item.path);
        notifyImageSelectedChanged(position, item, true);
    }

    protected void deleteSelectedImageItem(int position, ImageItemBean item) {
        mSelectedImages.remove(item);
        Log.i(TAG, "=====deleteSelectedImageItem:" + item.path);
        notifyImageSelectedChanged(position, item, false);
    }

    protected boolean isSelect(int position, ImageItemBean item) {
        return mSelectedImages.contains(item);
    }

    protected int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    protected void onDestroy() {
        if (mImageSelectedChangeListeners != null) {
            mImageSelectedChangeListeners.clear();
            mImageSelectedChangeListeners = null;
        }
        if (mImageCropCompleteListeners != null) {
            mImageCropCompleteListeners.clear();
            mImageCropCompleteListeners = null;
        }

        //mSelectedImages.clear();
        //mSelectedImages = null;

        clearImageSets();

        mCurrentSelectedImageSetPosition = 0;

        Log.i(TAG, "=====destroy:clear all data and listeners");
    }

    protected List<ImageItemBean> getSelectedImages() {
        List<ImageItemBean> list = new ArrayList<>();
        list.addAll(mSelectedImages);
        return list;
    }

    protected void clearSelectedImages() {
        if (mSelectedImages != null) {
            mSelectedImages.clear();
            Log.i(TAG, "=====clear all selected images");
        }
    }

    protected static Bitmap makeCropBitmap(Bitmap bitmap, Rect rectBox, RectF imageMatrixRect, int expectSize) {
        Bitmap bmp = bitmap;
        RectF localRectF = imageMatrixRect;
        float f = localRectF.width() / bmp.getWidth();
        int left = (int) ((rectBox.left - localRectF.left) / f);
        int top = (int) ((rectBox.top - localRectF.top) / f);
        int width = (int) (rectBox.width() / f);
        int height = (int) (rectBox.height() / f);

        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }

        if (left + width > bmp.getWidth()) {
            width = bmp.getWidth() - left;
        }
        if (top + height > bmp.getHeight()) {
            height = bmp.getHeight() - top;
        }

        int k = width;
        if (width < expectSize) {
            k = expectSize;
        }
        if (width > expectSize) {
            k = expectSize;
        }

        try {
            bmp = Bitmap.createBitmap(bmp, left, top, width, height);

            if (k != width && k != height) {//don't do this if equals
                bmp = Bitmap.createScaledBitmap(bmp, k, k, true);//scale the bitmap
            }

        } catch (OutOfMemoryError localOutOfMemoryError1) {
            Log.v(TAG, "OOM when create bitmap");
        }
        return bmp;
    }

    /**
     * create a file to save photo
     *
     * @param ctx
     * @return
     */
    private File createImageSaveFile(Context ctx) {
        if (Util.isStorageEnable()) {

            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!pic.exists()) {
                pic.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "IMG_" + timeStamp;
            File tmpFile = new File(pic, fileName + ".jpg");
            mCurrentPhotoPath = tmpFile.getAbsolutePath();
            Log.i(TAG, "=====camera path:" + mCurrentPhotoPath);
            return tmpFile;
        } else {
            //File cacheDir = ctx.getCacheDir();
            File cacheDir = Environment.getDataDirectory();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "IMG_" + timeStamp;
            File tmpFile = new File(cacheDir, fileName + ".jpg");
            mCurrentPhotoPath = tmpFile.getAbsolutePath();
            Log.i(TAG, "=====camera path:" + mCurrentPhotoPath);
            return tmpFile;
        }


    }

    /**
     * take picture
     */
    protected void takePicture(Activity act, int requestCode) throws IOException {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = createImageFile();
            File photoFile = createImageSaveFile(act);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        act.startActivityForResult(takePictureIntent, requestCode);

    }

    /**
     * take picture
     */
    protected void takePicture(Fragment fragment, int requestCode) throws IOException {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = createImageFile();
            File photoFile = createImageSaveFile(fragment.getContext());
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                Log.i(TAG, "=====file ready to take photo:" + photoFile.getAbsolutePath());
            }
        }
        fragment.startActivityForResult(takePictureIntent, requestCode);

    }

    /**
     * scan the photo so that the gallery can read it
     *
     * @param ctx
     * @param path
     */
    protected static void galleryAddPic(Context ctx, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
        Log.i(TAG, "=====MediaScan:" + path);
    }

    /**
     * listener for one Image Item selected observe
     */
    public interface OnImageSelectedChangeListener {
        void onImageSelectChange(int position, ImageItemBean item, int selectedItemsCount, int maxSelectLimit);
    }

    public interface OnImageCropCompleteListener {
        void onImageCropComplete(Bitmap bmp, float ratio);
    }

    public interface OnImagePickCompleteListener {
        void onImagePickComplete(List<ImageItemBean> items);
    }

    protected interface Select_Mode {
        int MODE_SINGLE = 0;
        int MODE_MULTI = 1;
    }


    protected void pickSingleImage(OnImagePickCompleteListener l) {
        if (activity != null) {
            //
            setSelectMode(Select_Mode.MODE_SINGLE);
            setShouldShowCamera(showCamera);
            setOnImagePickCompleteListener(l);
            cropMode = false;
            cameraMode = false;
            activity.startActivity(new Intent(activity, GridImagesActivity.class));
        }
    }

    protected void clickSingleImage(OnImagePickCompleteListener l) {
        if (activity != null) {
            //
            setSelectMode(Select_Mode.MODE_SINGLE);
            setShouldShowCamera(false);
            setOnImagePickCompleteListener(l);
            cropMode = false;
            cameraMode = true;
            activity.startActivity(new Intent(activity, GridImagesActivity.class));
        }
    }

    protected void pickMultiImages(OnImagePickCompleteListener l) {
        if (activity != null) {
            //
            setSelectMode(Select_Mode.MODE_MULTI);
            setShouldShowCamera(showCamera);
            setOnImagePickCompleteListener(l);
            cropMode = false;
            cameraMode = false;
            activity.startActivity(new Intent(activity, GridImagesActivity.class));
        }
    }

    protected void pickAndCrop(OnImageCropCompleteListener l) {
        if (activity != null) {
            setSelectMode(Select_Mode.MODE_SINGLE);
            setShouldShowCamera(showCamera);
            addOnImageCropCompleteListener(l);
            cropMode = true;
            cameraMode = false;
            activity.startActivity(new Intent(activity, GridImagesActivity.class));
        }
    }

    protected void clickAndCrop(OnImageCropCompleteListener l) {
        if (activity != null) {
            setSelectMode(Select_Mode.MODE_SINGLE);
            setShouldShowCamera(true);
            addOnImageCropCompleteListener(l);
            cameraMode = true;
            cropMode = true;
            activity.startActivity(new Intent(activity, GridImagesActivity.class));
        }
    }


    protected Activity activity = null;

    public WrcImagePicker2(Activity activity) {
        WrcImagePicker2 wrcImagePicker = WrcImagePicker2.getInstance();
        if (activity != null) {
            wrcImagePicker.activity = activity;
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(wrcImagePicker.activity));
        }
    }


    public PickImage pickImage() {

        return new PickImage();
    }

    public ClickImage clickImage() {

        return new ClickImage();
    }


}

