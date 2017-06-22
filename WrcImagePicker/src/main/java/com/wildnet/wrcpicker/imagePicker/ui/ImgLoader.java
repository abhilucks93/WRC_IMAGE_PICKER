

package com.wildnet.wrcpicker.imagePicker.ui;

import android.widget.ImageView;


public interface ImgLoader {
    void onPresentImage(ImageView imageView, String imageUri, int size);
}
