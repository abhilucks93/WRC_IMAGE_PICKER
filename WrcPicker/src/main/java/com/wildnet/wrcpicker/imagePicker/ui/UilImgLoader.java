

package com.wildnet.wrcpicker.imagePicker.ui;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;


public class UilImgLoader implements ImgLoader {
    @Override
    public void onPresentImage(ImageView imageView, String imageUri, int size) {
        ImageDownloader.Scheme scheme = ImageDownloader.Scheme.FILE;

        ImageLoader.getInstance().displayImage(scheme.wrap(imageUri), imageView, new DisplayImageOptions.Builder()
                .showImageOnLoading(com.wildnet.wrcpicker.R.drawable.default_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnFail(com.wildnet.wrcpicker.R.drawable.default_img)
                .showImageForEmptyUri(com.wildnet.wrcpicker.R.drawable.default_img)
                .showImageOnLoading(com.wildnet.wrcpicker.R.drawable.default_img)
                .build());
    }

    public void onPresentImage2(ImageView imageView, String imageUri, int size) {
        ImageDownloader.Scheme scheme = ImageDownloader.Scheme.FILE;

        ImageLoader.getInstance().displayImage(scheme.wrap(imageUri), imageView, new DisplayImageOptions.Builder()
                .showImageOnLoading(com.wildnet.wrcpicker.R.drawable.default_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnFail(com.wildnet.wrcpicker.R.drawable.default_img)
                .showImageForEmptyUri(com.wildnet.wrcpicker.R.drawable.default_img)
                .showImageOnLoading(com.wildnet.wrcpicker.R.drawable.default_img)
                .build());
    }
}
