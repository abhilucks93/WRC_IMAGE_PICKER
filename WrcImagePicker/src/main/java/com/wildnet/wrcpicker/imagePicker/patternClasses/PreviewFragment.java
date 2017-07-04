

package com.wildnet.wrcpicker.imagePicker.patternClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.main.ImgLoader;
import com.wildnet.wrcpicker.imagePicker.main.UilImgLoader;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.widget.CustomTouchImageView;

import java.io.Serializable;
import java.util.List;


public class PreviewFragment extends Fragment {
    private static final String TAG = PreviewFragment.class.getSimpleName();

    Activity mContext;
    public static Activity act;

    ViewPager mViewPager;
    TouchImageAdapter mAdapter;

    List<ImageItemBean> mImageList;
    private int mCurrentItemPosition = 0;

    private static boolean enableSingleTap = true;//singleTap to do something

    ImgLoader mImagePresenter;//interface to load image,you can implements it with your own code


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        act = mContext;
        //mSelectedImages = new SparseArray<>();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_preview, null);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));


        mImageList = (List<ImageItemBean>) getArguments().getSerializable("imageSet");
        mCurrentItemPosition = getArguments().getInt("selectedImageSetPos", 0);
        mImagePresenter = new UilImgLoader();
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        mViewPager = (ViewPager) contentView.findViewById(R.id.viewpager);
        mAdapter = new TouchImageAdapter(((FragmentActivity) mContext).getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);
        ImageItemBean item = mImageList.get(mCurrentItemPosition);
        if (mContext instanceof OnImagePageSelectedListener) {
            boolean isSelected = false;
            if (((PreviewActivity) getActivity()).isSelect(item)) {
                isSelected = true;
            }
            ((OnImagePageSelectedListener) mContext).onImagePageSelected(mCurrentItemPosition, mImageList.get(mCurrentItemPosition), isSelected);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                if (mContext instanceof OnImagePageSelectedListener) {
                    boolean isSelected = false;
                    ImageItemBean item = mImageList.get(mCurrentItemPosition);
                    if (((PreviewActivity) getActivity()).isSelect(item)) {
                        isSelected = true;
                    }
                    ((OnImagePageSelectedListener) mContext).onImagePageSelected(mCurrentItemPosition, item, isSelected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

    }

    /**
     * public method:select the current show image
     */
    public void selectCurrent(boolean isCheck) {
        ImageItemBean item = mImageList.get(mCurrentItemPosition);
        boolean isSelect = ((PreviewActivity) getActivity()).isSelect(item);
        if (isCheck) {
            if (!isSelect) {
                ((PreviewActivity) getActivity()).addSelectedImageItem(item);
            }
        } else {
            if (isSelect) {
                ((PreviewActivity) getActivity()).deleteSelectedImageItem(item);
            }
        }

    }

    class TouchImageAdapter extends FragmentStatePagerAdapter {
        public TouchImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public Fragment getItem(int position) {
            SinglePreviewFragment fragment = new SinglePreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SinglePreviewFragment.KEY_URL, (Serializable) mImageList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

    }

    @SuppressLint("ValidFragment")
    public static class SinglePreviewFragment extends Fragment {
        public static final String KEY_URL = "key_url";
        private CustomTouchImageView imageView;
        private String url;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();

            ImageItemBean imageItem = (ImageItemBean) bundle.getSerializable(KEY_URL);

            url = imageItem.path;

            Log.i(TAG, "=====current show image path:" + url);

            imageView = new CustomTouchImageView(act);
            imageView.setBackgroundColor(0xff000000);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);

            imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (enableSingleTap) {
                        if (act instanceof OnImageSingleTapClickListener) {
                            ((OnImageSingleTapClickListener) act).onImageSingleTap(e);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return false;
                }

            });

            (new UilImgLoader()).onPresentImage2(imageView, url, imageView.getWidth());//display the image with your own ImageLoader

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return imageView;
        }

    }


    /**
     * Interface for SingleTap Watching
     */
    public interface OnImageSingleTapClickListener {
        void onImageSingleTap(MotionEvent e);
    }

    /**
     * Interface for swipe page watching,you can get the current item,item position and whether the item is selected
     */
    public interface OnImagePageSelectedListener {
        void onImagePageSelected(int position, ImageItemBean item, boolean isSelected);
    }


}
