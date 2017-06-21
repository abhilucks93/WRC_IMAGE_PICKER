package com.wildnet.wrcpicker.imagePicker.ui;

public class Crop {
    String tag = "";
    WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();

    protected Crop(String tag) {
        this.tag = tag;
    }

    public void build(WrcImagePicker.OnImageCropCompleteListener onImageCropCompleteListener) {
        if (tag.equals("CAMERA")) {
            wrcImagePicker.clickAndCrop(onImageCropCompleteListener);
        } else if (tag.equals("GALLERY")) {
            wrcImagePicker.pickAndCrop(onImageCropCompleteListener);
        }
    }

}