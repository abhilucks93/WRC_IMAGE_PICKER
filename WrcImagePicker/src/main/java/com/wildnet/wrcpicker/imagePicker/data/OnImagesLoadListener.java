

package com.wildnet.wrcpicker.imagePicker.data;

import com.wildnet.wrcpicker.imagePicker.bean.ImageSetBean;

import java.util.List;


public interface OnImagesLoadListener {
    void onImagesLoaded(List<ImageSetBean> imageSetList);
}
