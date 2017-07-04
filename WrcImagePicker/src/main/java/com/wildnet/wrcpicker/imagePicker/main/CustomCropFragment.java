

package com.wildnet.wrcpicker.imagePicker.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.widget.CustomImageView;
import com.wildnet.wrcpicker.imagePicker.widget.CustomRectView;

public class CustomCropFragment extends Fragment{

    Activity mContext;

    CustomImageView superImageView;
    CustomRectView mRectView;

    private int screenWidth;
    private final int margin = 30;//the left and right margins of the center circular shape

    private FrameLayout rootView;

    private String picPath;//the local image path in sdcard

    ImgLoader mImagePresenter;//interface to load image,you can implement it with your own code

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_avatar_crop,null);

        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        initView(contentView);

        //get the image path from Arguments
        picPath = getArguments().getString(WrcImagePicker2.KEY_PIC_PATH);

        mImagePresenter = new UilImgLoader();

        if(TextUtils.isEmpty(picPath)){
            throw new RuntimeException("WrcImagePicker:you have to give me an image path from sdcard");
        }else{
            mImagePresenter.onPresentImage(superImageView,picPath,screenWidth);
        }

        return contentView;

    }

    /**
     * init all views
     * @param contentView
     */
    void initView(View contentView){
        superImageView = (CustomImageView) contentView.findViewById(R.id.iv_pic);
        rootView = (FrameLayout) contentView.findViewById(R.id.container);

        RelativeLayout.LayoutParams rlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //rlLayoutParams.addRule(RelativeLayout.ABOVE, R.id.photo_preview_dock);
        mRectView = new CustomRectView(mContext, screenWidth - margin*2);
        rootView.addView(mRectView, 1, rlLayoutParams);
    }


    /**
     * public method to get the crop bitmap
     * @return
     */
    public Bitmap getCropBitmap(int expectSize){
        if(expectSize <= 0){
            return null;
        }
        Bitmap srcBitmap = ((BitmapDrawable)superImageView.getDrawable()).getBitmap();
        double rotation = superImageView.getImageRotation();
        int level = (int) Math.floor((rotation + Math.PI / 4) / (Math.PI / 2));
        if (level != 0){
            srcBitmap = Util.rotate(srcBitmap,90 * level);
        }
        Rect centerRect = mRectView.getCropRect();
        RectF matrixRect = superImageView.getMatrixRect();

        Bitmap bmp = WrcImagePicker2.makeCropBitmap(srcBitmap, centerRect, matrixRect, expectSize);
        return bmp;
    }


}
