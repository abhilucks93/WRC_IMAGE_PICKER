

package com.wildnet.wrcpicker.imagePicker.ui;

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
import com.wildnet.wrcpicker.imagePicker.bean.ImageItemBean;

import java.io.Serializable;
import java.util.List;

public class PreviewActivity extends FragmentActivity implements View.OnClickListener, PreviewFragment.OnImageSingleTapClickListener, PreviewFragment.OnImagePageSelectedListener, WrcImagePicker.OnImageSelectedChangeListener {
    private static final String TAG = PreviewActivity.class.getSimpleName();

    PreviewFragment mFragment;
    TextView mTitleCount;
    CheckBox mCbSelected;
    TextView mBtnOk;

    List<ImageItemBean> mImageList;
    int mShowItemPosition = 0;
    WrcImagePicker wrcImagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pre);

        wrcImagePicker = WrcImagePicker.getInstance();
        wrcImagePicker.addOnImageSelectedChangeListener(this);

        mImageList = WrcImagePicker.getInstance().getImageItemsOfCurrentImageSet();
        mShowItemPosition = getIntent().getIntExtra(WrcImagePicker.KEY_PIC_SELECTED_POSITION, 0);

        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnOk.setTextColor(wrcImagePicker.themeTextColor);
        mBtnOk.setBackgroundColor(wrcImagePicker.themeBgColor);

        mCbSelected = (CheckBox) findViewById(R.id.btn_check);
        mTitleCount = (TextView) findViewById(R.id.tv_title_count);
        mTitleCount.setText("1/" + mImageList.size());

        int selectedCount = WrcImagePicker.getInstance().getSelectImageCount();

        onImageSelectChange(0, null, selectedCount, wrcImagePicker.getSelectLimit());

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
                if (wrcImagePicker.getSelectImageCount() > wrcImagePicker.getSelectLimit()) {
                    if (mCbSelected.isChecked()) {
                        //holder.cbSelected.setCanChecked(false);
                        mCbSelected.toggle();
                        String toast = getResources().getString(R.string.you_have_a_select_limit, wrcImagePicker.getSelectLimit());
                        Toast.makeText(PreviewActivity.this, toast, Toast.LENGTH_SHORT).show();
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
        data.putSerializable(WrcImagePicker.KEY_PIC_PATH, (Serializable) mImageList);
        data.putInt(WrcImagePicker.KEY_PIC_SELECTED_POSITION, mShowItemPosition);
        mFragment.setArguments(data);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            setResult(RESULT_OK);// select complete
            finish();
        } else if (v.getId() == R.id.btn_pic_negative) {
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
            mBtnOk.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            mBtnOk.setText(getResources().getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }
        Log.i(TAG, "=====EVENT:onImageSelectChange");
    }

    @Override
    protected void onDestroy() {
        wrcImagePicker.removeOnImageItemSelectedChangeListener(this);
        Log.i(TAG, "=====removeOnImageItemSelectedChangeListener");
        super.onDestroy();
    }

}
