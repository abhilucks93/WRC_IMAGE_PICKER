

package com.wildnet.wrcpicker.imagePicker.main;

import android.widget.ImageView;


public interface ImgLoader {
    void onPresentImage(ImageView imageView, String imageUri, int size);
}
