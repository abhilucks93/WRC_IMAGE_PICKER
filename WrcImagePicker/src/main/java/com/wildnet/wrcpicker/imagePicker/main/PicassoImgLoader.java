

package com.wildnet.wrcpicker.imagePicker.main;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.File;


public class PicassoImgLoader implements ImgLoader {
    @Override
    public void onPresentImage(ImageView imageView, String imageUri, int size) {
        Picasso.with(imageView.getContext())
                .load(new File(imageUri))
                .centerCrop()
                //.dontAnimate()
                //.thumbnail(0.5f)
                //.override(size, size)
                .resize(size/4*3, size/4*3)
                .placeholder(com.wildnet.wrcpicker.R.drawable.default_img)
                //.error(R.drawable.default_img)
                .into(imageView);

    }

}
