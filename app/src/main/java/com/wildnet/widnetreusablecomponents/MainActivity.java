package com.wildnet.widnetreusablecomponents;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wildnet.wrcpicker.imagePicker.listeners.OnActionCompleteListener;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.patternClasses.WrcImagePicker;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnActionCompleteListener {

    Button customCamera, customGallery, defaultGallery, defaultCamera, customSingle, customMultiple;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();


    }

    private void findViewById() {
        customCamera = (Button) findViewById(R.id.bt_custom_camera);
        customCamera.setOnClickListener(this);
        defaultCamera = (Button) findViewById(R.id.bt_default_camera);
        defaultCamera.setOnClickListener(this);
        defaultGallery = (Button) findViewById(R.id.bt_default_gallery);
        defaultGallery.setOnClickListener(this);
        customGallery = (Button) findViewById(R.id.bt_custom_gallery);
        customGallery.setOnClickListener(this);
        customSingle = (Button) findViewById(R.id.bt_custom_single);
        customSingle.setOnClickListener(this);
        customMultiple = (Button) findViewById(R.id.bt_custom_multiple);
        customMultiple.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imageView);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bt_custom_camera:

                WrcImagePicker.getInstance(this).clickImage().setCustom(true).build(this);
                break;

            case R.id.bt_default_camera:

                WrcImagePicker.getInstance(this).clickImage().build(this);
                break;

            case R.id.bt_default_gallery:

                WrcImagePicker.getInstance(this).pickImage().build(new OnActionCompleteListener() {
                    @Override
                    public void _actionCompleted(List<ImageItemBean> items) {

                    }
                });
                break;

            case R.id.bt_custom_gallery:

                WrcImagePicker.getInstance(this).pickImage().setCustom(true).build(this);
                break;

            case R.id.bt_custom_single:

                WrcImagePicker.getInstance(this).customPick().singleMode().build(this);
                break;

            case R.id.bt_custom_multiple:

                WrcImagePicker.getInstance(this).customPick().multipleMode().maxCount(5).build(this);
                break;
        }
    }

    @Override
    public void _actionCompleted(final List<ImageItemBean> items) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//stuff that updates ui
                if (items.get(0).getUri() != null)
                    imageView.setImageURI(items.get(0).getUri());
                else if (items.get(0).getPath() != null)
                    imageView.setImageURI(Uri.parse(items.get(0).getPath()));
            }
        });


    }
}
