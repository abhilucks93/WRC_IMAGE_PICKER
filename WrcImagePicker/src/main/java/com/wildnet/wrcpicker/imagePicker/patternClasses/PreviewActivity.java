

package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.main.WrcImagePicker2;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;

import java.util.ArrayList;

public class PreviewActivity extends FragmentActivity implements View.OnClickListener, PreviewFragment.OnImageSingleTapClickListener, PreviewFragment.OnImagePageSelectedListener, WrcImagePicker2.OnImageSelectedChangeListener {
    private static final String TAG = PreviewActivity.class.getSimpleName();

    PreviewFragment mFragment;
    TextView mTitleCount;
    CheckBox mCbSelected;
    TextView mBtnOk;

    ArrayList<ImageItemBean> mImageList;
    ArrayList<ImageItemBean> mSelectedImageList;
    int mShowItemPosition = 0;
    private int maxCount;
    //  WrcImagePicker2 wrcImagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pre);

        /*wrcImagePicker = WrcImagePicker2.getInstance();
        wrcImagePicker.addOnImageSelectedChangeListener(this);*/

        mImageList = (ArrayList<ImageItemBean>) getIntent().getSerializableExtra("imageSet");
        mShowItemPosition = getIntent().getIntExtra("selectedImagePos", 0);
        maxCount = getIntent().getIntExtra("maxCount", 10);
        mSelectedImageList = (ArrayList<ImageItemBean>) getIntent().getSerializableExtra("selectedImageSet");

        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        //mBtnOk.setTextColor("");
        //mBtnOk.setBackgroundColor("");

        mCbSelected = (CheckBox) findViewById(R.id.btn_check);
        mTitleCount = (TextView) findViewById(R.id.tv_title_count);
        mTitleCount.setText("1/" + mImageList.size());


        //back press
        findViewById(R.id.btn_backpress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImageList.size() > maxCount) {
                    if (mCbSelected.isChecked()) {
                        //holder.cbSelected.setCanChecked(false);
                        mCbSelected.toggle();
                        String toastMsg = getResources().getString(R.string.you_have_a_select_limit) + maxCount;
                        Toast.makeText(PreviewActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        //
                    }
                } else {
                    //
                }
            }

        });

        mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFragment.selectCurrent(isChecked);
            }
        });


        mFragment = new PreviewFragment();
        Bundle data = new Bundle();
        data.putSerializable("imageSet", mImageList);
        data.putInt("selectedImageSetPos", mShowItemPosition);
        mFragment.setArguments(data);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra("selectedImageSet", mSelectedImageList);
            setResult(RESULT_OK, intent);
            finish();
        }


    }


    @Override
    public void onImageSingleTap(MotionEvent e) {
        View topBar = findViewById(R.id.top_bar);
        View bottomBar = findViewById(R.id.bottom_bar);
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(PreviewActivity.this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onImagePageSelected(int position, ImageItemBean item, boolean isSelected) {
        mTitleCount.setText(position + 1 + "/" + mImageList.size());
        mCbSelected.setChecked(isSelected);
    }

    @Override
    public void onImageSelectChange(int position, ImageItemBean item, int selectedItemsCount, int maxSelectLimit) {
        if (selectedItemsCount > 0) {
            mBtnOk.setEnabled(true);
            // mBtnOk.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            mBtnOk.setText(getResources().getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }
        Log.i(TAG, "=====EVENT:onImageSelectChange");
    }


    public void addSelectedImageItem(ImageItemBean item) {
        mSelectedImageList.add(item);
    }

    public boolean isSelect(ImageItemBean item) {
        return mSelectedImageList.contains(item);
    }

    public void deleteSelectedImageItem(ImageItemBean item) {
        mSelectedImageList.remove(item);
    }
}
