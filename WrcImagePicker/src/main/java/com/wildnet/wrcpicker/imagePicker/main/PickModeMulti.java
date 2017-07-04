package com.wildnet.wrcpicker.imagePicker.main;

public class PickModeMulti {
    WrcImagePicker2 wrcImagePicker = WrcImagePicker2.getInstance();


    public PickModeMulti selectLimit(int selectLimit) {
        wrcImagePicker.setSelectLimit(selectLimit);
        return this;
    }

    public void build(WrcImagePicker2.OnImagePickCompleteListener onImagePickCompleteListener) {
        wrcImagePicker.pickMultiImages(onImagePickCompleteListener);
    }
}