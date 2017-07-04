package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;

/**
 * Created by abhishekagarwal on 6/27/17.
 */

public class GalleryDefaultActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_GALLERY_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    WrcImagePicker wrcImagePicker = WrcImagePicker.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            callGallery();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_GALLERY_PERMISSION,
                            R.string.gallery_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_GALLERY_PERMISSION);
        }
    }

    private void callGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GALLERY_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.gallery_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                } else {
                    callGallery();
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 100:
                if (resultCode == RESULT_OK) {
                    wrcImagePicker.imagePickedCallback(new ImageItemBean(data.getData(), null, System.currentTimeMillis()));
                } else {
                    wrcImagePicker.imagePickedCallback(null);
                }
                finish();
                break;
        }
    }
}
