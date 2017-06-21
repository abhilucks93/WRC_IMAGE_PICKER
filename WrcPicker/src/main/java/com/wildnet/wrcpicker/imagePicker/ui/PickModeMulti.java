package com.wildnet.wrcpicker.imagePicker.ui;

public class PickModeMulti {
    WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();


    public PickModeMulti selectLimit(int selectLimit) {
        wrcImagePicker.setSelectLimit(selectLimit);
        return this;
    }

    public void build(WrcImagePicker.OnImagePickCompleteListener onImagePickCompleteListener) {
        wrcImagePicker.pickMultiImages(onImagePickCompleteListener);
    }
}