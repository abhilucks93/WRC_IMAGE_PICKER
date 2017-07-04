package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.graphics.Color;

import com.wildnet.wrcpicker.imagePicker.listeners.OnActionCompleteListener;
import com.wildnet.wrcpicker.imagePicker.main.PickModeMulti;
import com.wildnet.wrcpicker.imagePicker.main.WrcImagePicker2;

public class PickImage {
    private WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();

    public Crop crop() {

        return new Crop("GALLERY");
    }

    public PickImage setTheme(String themeBgColor, String themeTextColor) {
        if (themeBgColor != null && themeBgColor.length() > 0)
            wrcImagePicker.setThemeBgColor(Color.parseColor(themeBgColor));
        if (themeTextColor != null && themeTextColor.length() > 0)
            wrcImagePicker.setThemeTextColor(Color.parseColor(themeTextColor));

        return this;
    }

    public PickImage setCustom(Boolean value) {
        wrcImagePicker.setGalleryDefaultMode(!value);
        return this;
    }


    public void build(OnActionCompleteListener onActionCompleteListener) {
        wrcImagePicker.fetchImage(onActionCompleteListener);
    }

}