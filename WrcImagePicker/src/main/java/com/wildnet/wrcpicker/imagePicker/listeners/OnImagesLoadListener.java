

package com.wildnet.wrcpicker.imagePicker.listeners;

import com.wildnet.wrcpicker.imagePicker.model.ImageSetBean;

import java.util.List;


public interface OnImagesLoadListener {
    void onImagesLoaded(List<ImageSetBean> imageSetList);
}
