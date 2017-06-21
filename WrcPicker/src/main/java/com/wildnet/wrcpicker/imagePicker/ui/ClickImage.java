package com.wildnet.wrcpicker.imagePicker.ui;

import android.graphics.Color;

public class ClickImage {
    WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();

    public Crop crop(int cropSize, Enums.CropperType cropper) {
        wrcImagePicker.cropSize = cropSize;
        wrcImagePicker.cropper = cropper;
        return new Crop("CAMERA");
    }


    public ClickImage theme(String themeBgColor, String themeTextColor) {
        if (themeBgColor != null && themeBgColor.length() > 0)
            wrcImagePicker.setThemeBgColor(Color.parseColor(themeBgColor));
        if (themeTextColor != null && themeTextColor.length() > 0)
            wrcImagePicker.setThemeTextColor(Color.parseColor(themeTextColor));

        return this;
    }

    public void build(WrcImagePicker.OnImagePickCompleteListener onImagePickCompleteListener) {
        wrcImagePicker.clickSingleImage(onImagePickCompleteListener);
    }
}