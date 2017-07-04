package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.graphics.Color;

import com.wildnet.wrcpicker.imagePicker.listeners.OnActionCompleteListener;

public class ClickImage {

    private WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();

    public Crop crop() {
        wrcImagePicker.setCrop(true);
        return new Crop("CAMERA");
    }

    public ClickImage preview() {
        wrcImagePicker.setPreview(true);
        return this;
    }

    public ClickImage setTheme(String themeBgColor, String themeTextColor) {
        if (themeBgColor != null && themeBgColor.length() > 0)
            wrcImagePicker.setThemeBgColor(Color.parseColor(themeBgColor));
        if (themeTextColor != null && themeTextColor.length() > 0)
            wrcImagePicker.setThemeTextColor(Color.parseColor(themeTextColor));

        return this;
    }

    public ClickImage setCustom(Boolean value) {
        wrcImagePicker.setCameraDefaultMode(!value);
        return this;
    }

    public ClickImage setImageCache(Boolean value) {
        wrcImagePicker.setCameraImageCache(value);
        return this;
    }

    public ClickImage setImageName(String value) {
        wrcImagePicker.setCameraImageName(value);
        return this;
    }


    public void build(OnActionCompleteListener onActionCompleteListener) {
        wrcImagePicker.captureImage(onActionCompleteListener);
    }
}