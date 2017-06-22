

package com.wildnet.wrcpicker.imagePicker.ui;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;


public class GlideImgLoader implements ImgLoader {
    @Override
    public void onPresentImage(ImageView imageView, String imageUri, int size) {
        Glide.with(imageView.getContext())
                .load(new File(imageUri))
                .centerCrop()
                .dontAnimate()
                .thumbnail(0.5f)
                .override(size/4*3, size/4*3)
                .placeholder(com.wildnet.wrcpicker.R.drawable.default_img)
                .error(com.wildnet.wrcpicker.R.drawable.default_img)
                .into(imageView);

    }

}
