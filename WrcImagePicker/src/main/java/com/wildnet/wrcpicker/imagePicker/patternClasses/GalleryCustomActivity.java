

package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.controller.ImageGridAdapter;
import com.wildnet.wrcpicker.imagePicker.controller.ImageSetAdapter;
import com.wildnet.wrcpicker.imagePicker.listeners.DataInterface;
import com.wildnet.wrcpicker.imagePicker.listeners.OnImagesLoadListener;
import com.wildnet.wrcpicker.imagePicker.main.DataSourceLocal;
import com.wildnet.wrcpicker.imagePicker.main.PicassoImgLoader;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.model.ImageSetBean;

import java.util.ArrayList;
import java.util.List;

public class GalleryCustomActivity extends FragmentActivity implements View.OnClickListener, OnImagesLoadListener {

    private static final String TAG = GalleryCustomActivity.class.getSimpleName();

    TextView mBtnOk, mCount;
    Button btnDir;
    View mFooterView;
    GridView mGridView;

    ImageSetAdapter mImageSetAdapter;
    ImageGridAdapter mAdapter;
    List<ImageSetBean> mImageSetList;//data of all ImageSets
    ListPopupWindow mFolderPopupWindow;//ImageSetBean PopupWindow
    PicassoImgLoader mImagePresenter;
    WrcImagePicker wrcImagePicker;

    int width, height;
    int maxCount = 10;
    int themeBgColor = Color.parseColor("#40c2c2c2");
    int themeTextColor = Color.parseColor("#ffffff");
    boolean singleMode = true;
    boolean showCamera = true;
    boolean isPreview = false, isCrop = false;
    ImageView mBtnBack;
    protected ArrayList<ImageItemBean> selectedImages;

    private static final int REQUEST_PERMISSION = 1;
    private static final int REQ_PREVIEW = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_custom);

        findViewById();

        checkPermission();

    }


    private void findViewById() {
        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mFooterView = findViewById(R.id.footer_panel);
        mCount = (TextView) findViewById(R.id.tv_title_count);
        btnDir = (Button) findViewById(R.id.btn_dir);
        btnDir.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mBtnBack = (ImageView) findViewById(R.id.btn_backpress);
        mBtnBack.setOnClickListener(this);
    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            initData();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

    }

    private void initData() {

        singleMode = getIntent().getBooleanExtra("singlePickMode", true);
        maxCount = getIntent().getIntExtra("maxCount", 10);
        showCamera = getIntent().getBooleanExtra("showCamera", true);
        isPreview = getIntent().getBooleanExtra("isPreview", false);
        isCrop = getIntent().getBooleanExtra("isCrop", false);

        selectedImages = new ArrayList<>();

        wrcImagePicker = WrcImagePicker.getInstance();

        DataInterface dataSource = new DataSourceLocal(this);
        dataSource.provideMediaItems(this);//select all images from local database

        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        mImageSetAdapter = new ImageSetAdapter(GalleryCustomActivity.this);
        mImageSetAdapter.refreshData(mImageSetList);

        mBtnOk.setTextColor(themeTextColor);
        mBtnOk.setBackgroundColor(themeBgColor);

        if (singleMode) {
            mBtnOk.setVisibility(View.GONE);
            mCount.setVisibility(View.GONE);
        } else {
            mBtnOk.setVisibility(View.VISIBLE);
            mCount.setVisibility(View.VISIBLE);
        }
    }

    private void createPopupFolderList(int width, int height) {
        mFolderPopupWindow = new ListPopupWindow(this);
        //mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mImageSetAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnchorView(mFooterView);
        mFolderPopupWindow.setModal(true);

        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);

        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mImageSetAdapter.setSelectIndex(i);
                // wrcImagePicker.setCurrentSelectedImageSetPosition(i);

                final int index = i;
                final AdapterView tempAdapterView = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();

                        ImageSetBean imageSet = (ImageSetBean) tempAdapterView.getAdapter().getItem(index);
                        if (null != imageSet) {
                            mAdapter.refreshData(imageSet.imageItems);
                            btnDir.setText(imageSet.name);

                        }
                        // scroll to the top
                        mGridView.smoothScrollToPosition(0);

                    }
                }, 100);

            }
        });

    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0~1.0
        getWindow().setAttributes(lp);
    }

    public void addImage(ImageItemBean image) {
        selectedImages.add(image);
    }

    public void removeImage(ImageItemBean image) {
        selectedImages.remove(image);
    }

    public int getImageCount() {
        return selectedImages.size();
    }

    public int getSelectLimit() {
        return maxCount;
    }

    public boolean isSelected(ImageItemBean item) {
        return (selectedImages.contains(item));

    }

    public void cameraClicked() {
    }

    public void imageSelected(ImageItemBean item) {
        wrcImagePicker.imageCustomPickedCallback(item);
        finish();
    }

    public void imagePreview(int position) {
        Intent intent = new Intent();
        intent.putExtra("selectedImagePos", position);
        intent.putExtra("maxCount", maxCount);
        intent.putExtra("selectedImageSet", selectedImages);
        intent.putExtra("imageSet", mAdapter.getCurrentImageSetItems());
        intent.setClass(GalleryCustomActivity.this, PreviewActivity.class);
        startActivityForResult(intent, REQ_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQ_PREVIEW:
                if (resultCode == RESULT_OK) {
                    selectedImages = (ArrayList<ImageItemBean>) data.getSerializableExtra("selectedImageSet");
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_dir) {

            if (mFolderPopupWindow == null) {
                createPopupFolderList(width, height);
            }
            backgroundAlpha(0.3f);
            mImageSetAdapter.refreshData(mImageSetList);
            mFolderPopupWindow.setAdapter(mImageSetAdapter);
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
                int index = mImageSetAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.getListView().setSelection(index);
            }

        } else if (view.getId() == R.id.btn_backpress) {

            finish();

        } else if (view.getId() == R.id.btn_ok) {

            wrcImagePicker.imagesPickedCallback(selectedImages);
            finish();
        }
    }

    @Override
    public void onImagesLoaded(List<ImageSetBean> imageSetList) {
        mImageSetList = imageSetList;

        btnDir.setText(imageSetList.get(0).name);
        mAdapter = new ImageGridAdapter(GalleryCustomActivity.this, imageSetList.get(0).imageItems, showCamera, singleMode);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (permissions.length != 3 || grantResults.length != 3) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {
                    initData();
                }
                break;
        }
    }


}
