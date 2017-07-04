

package com.wildnet.wrcpicker.imagePicker.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wildnet.wrcpicker.R;


public class CropImageActivity extends FragmentActivity implements View.OnClickListener {

    private TextView btnNegative;
    private TextView btnPositive;
    private ImageView ivShow;

    CustomCropFragment mFragment;
    WrcImagePicker2 wrcImagePicker;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        wrcImagePicker = WrcImagePicker2.getInstance();

        ivShow = (ImageView) findViewById(R.id.iv_show);

        btnPositive = (TextView) findViewById(R.id.btn_pic_positive);
        btnPositive.setOnClickListener(this);
        btnPositive.setBackgroundColor(wrcImagePicker.getThemeBgColor());
        btnPositive.setTextColor(wrcImagePicker.getThemeTextColor());



        btnNegative = (TextView) findViewById(R.id.btn_pic_negative);
        btnNegative.setOnClickListener(this);
        btnNegative.setBackgroundColor(wrcImagePicker.getThemeBgColor());
        btnNegative.setTextColor(wrcImagePicker.getThemeTextColor());


        imagePath = getIntent().getStringExtra(WrcImagePicker2.KEY_PIC_PATH);
        mFragment = new CustomCropFragment();
        Bundle data = new Bundle();
        data.putString(WrcImagePicker2.KEY_PIC_PATH, imagePath);
        mFragment.setArguments(data);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_pic_positive) {
            Bitmap bmp = mFragment.getCropBitmap(WrcImagePicker2.getInstance().cropSize);
            finish();
            WrcImagePicker2.getInstance().notifyImageCropComplete(bmp, 0);
        } else if (v.getId() == R.id.btn_pic_negative) {
            finish();
        }

    }


}
