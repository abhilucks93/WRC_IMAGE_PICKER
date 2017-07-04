package com.wildnet.wrcpicker.imagePicker.patternClasses;

import com.wildnet.wrcpicker.imagePicker.listeners.OnActionCompleteListener;

public class Crop {
    String tag = "";
    private WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();

    protected Crop(String tag) {
        this.tag = tag;
    }

    public void build(OnActionCompleteListener onActionCompleteListener) {
        if (tag.equals("CAMERA")) {
            wrcImagePicker.captureImage(onActionCompleteListener);
        } else if (tag.equals("GALLERY")) {
            wrcImagePicker.fetchImage(onActionCompleteListener);
        }else if (tag.equals("CUSTOM")) {
            wrcImagePicker.customPickImage(onActionCompleteListener);
        }
    }

}