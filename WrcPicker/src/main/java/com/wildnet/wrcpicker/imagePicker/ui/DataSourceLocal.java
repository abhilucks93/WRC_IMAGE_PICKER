

package com.wildnet.wrcpicker.imagePicker.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.bean.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.bean.ImageSetBean;
import com.wildnet.wrcpicker.imagePicker.data.DataInterface;
import com.wildnet.wrcpicker.imagePicker.data.OnImagesLoadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataSourceLocal implements DataInterface, LoaderManager.LoaderCallbacks<Cursor>{

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID };

    // different loader define
    public static final int LOADER_ALL = 0;
    public static final int LOADER_CATEGORY = 1;

    OnImagesLoadListener imagesLoadedListener;
    Context mContext;
    // ImageSetBean data
    private ArrayList<ImageSetBean> mImageSetList = new ArrayList<>();

    @Override
    public void provideMediaItems(OnImagesLoadListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if(mContext instanceof FragmentActivity){
            ((FragmentActivity)mContext).getSupportLoaderManager().initLoader(LOADER_ALL,null,this);
        }else{
            throw new RuntimeException("your activity must be instance of FragmentActivity");
        }
    }

    public DataSourceLocal(Context ctx){
        this.mContext = ctx;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ALL) {//scan all
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }else if(id == LOADER_CATEGORY){//scan one dir
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[0]+" like '%"+args.getString("path")+"%'", null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageSetList.clear();
        if(data != null){
            List<ImageItemBean> allImages = new ArrayList<>();
            int count = data.getCount();
            if(count <= 0 ){
                return;
            }

            data.moveToFirst();
            do{
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                ImageItemBean item = new ImageItemBean(imagePath,imageName,imageAddedTime);
                allImages.add(item);

                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();

                ImageSetBean imageSet = new ImageSetBean();
                imageSet.name = imageParentFile.getName();
                imageSet.path = imageParentFile.getAbsolutePath();
                imageSet.cover = item;

                if(!mImageSetList.contains(imageSet)){
                    List<ImageItemBean> imageList = new ArrayList<>();
                    imageList.add(item);
                    imageSet.imageItems = imageList;
                    mImageSetList.add(imageSet);
                } else {
                    mImageSetList.get(mImageSetList.indexOf(imageSet)).imageItems.add(item);
                }

            }while(data.moveToNext());

            ImageSetBean imageSetAll = new ImageSetBean();
            imageSetAll.name= mContext.getResources().getString(R.string.all_images);
            imageSetAll.cover = allImages.get(0);
            imageSetAll.imageItems = allImages;
            imageSetAll.path = "/";

            if(mImageSetList.contains(imageSetAll)){
                mImageSetList.remove(imageSetAll);//the first item is "all images"
            }
            mImageSetList.add(0,imageSetAll);

            imagesLoadedListener.onImagesLoaded(mImageSetList);//notify the data changed

            WrcImagePicker.getInstance().setImageSets(mImageSetList);

        }

    }

    @Override public void onLoaderReset(Loader<Cursor> loader) { }


}
