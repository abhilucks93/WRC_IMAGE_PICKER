package com.wildnet.wrcpicker.imagePicker.ui;

import android.graphics.Color;

public class PickImage {
    WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();

    public PickImage withCamera() {
        WrcImagePicker.showCamera = true;
        return this;
    }

    public Crop crop(int cropSize, Enums.CropperType cropper) {
        wrcImagePicker.cropSize = cropSize;
        wrcImagePicker.cropper = cropper;
        return new Crop("GALLERY");
    }


    public PickImage theme(String themeBgColor, String themeTextColor) {
        if (themeBgColor != null && themeBgColor.length() > 0)
            try {
                wrcImagePicker.setThemeBgColor(Color.parseColor(themeBgColor));
            } catch (Exception e) {
            }
        if (themeTextColor != null && themeTextColor.length() > 0)
            try {
                wrcImagePicker.setThemeTextColor(Color.parseColor(themeTextColor));
            } catch (Exception e) {
            }

        return this;
    }

    public void build(WrcImagePicker.OnImagePickCompleteListener onImagePickCompleteListener) {

        wrcImagePicker.pickSingleImage(onImagePickCompleteListener);
    }


    public PickModeMulti multipleImages() {

        return new PickModeMulti();
    }

}