

package com.wildnet.wrcpicker.imagePicker.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.model.ImageSetBean;
import com.wildnet.wrcpicker.imagePicker.listeners.DataInterface;
import com.wildnet.wrcpicker.imagePicker.listeners.OnImagesLoadListener;
import com.wildnet.wrcpicker.imagePicker.widget.CustomCheckBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GridImagesFragment extends Fragment implements OnImagesLoadListener, WrcImagePicker2.OnImageSelectedChangeListener, WrcImagePicker2.OnImageCropCompleteListener {
    private static final String TAG = GridImagesFragment.class.getSimpleName();

    Activity mContext;

    GridView mGridView;
    ImageGridAdapter mAdapter;
    int imageGridSize;

    Button btnDir;//button to change ImageSetBean
    private View mFooterView;
    private ListPopupWindow mFolderPopupWindow;//ImageSetBean PopupWindow
    private ImageSetAdapter mImageSetAdapter;
    List<ImageSetBean> mImageSetList;//data of all ImageSets

    ImgLoader mImagePresenter;
    WrcImagePicker2 wrcImagePicker;

    private OnItemClickListener mOnItemClickListener;//Grid Item click Listener

    private static final int ITEM_TYPE_CAMERA = 0;//the first Item may be Camera
    private static final int ITEM_TYPE_NORMAL = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        wrcImagePicker = WrcImagePicker2.getInstance();
        //wrcImagePicker.clear();

        wrcImagePicker.addOnImageSelectedChangeListener(this);

        if (wrcImagePicker.cropMode) {
            wrcImagePicker.addOnImageCropCompleteListener(this);
        }

        if (wrcImagePicker.cameraMode) {
            try {
                wrcImagePicker.takePicture(GridImagesFragment.this, WrcImagePicker2.REQ_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //wrcImagePicker.clearSelectedImages();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_images_grid, null);

        mFooterView = contentView.findViewById(R.id.footer_panel);
        imageGridSize = (mContext.getWindowManager().getDefaultDisplay().getWidth() - Util.dp2px(mContext, 2) * 2) / 3;
        btnDir = (Button) contentView.findViewById(R.id.btn_dir);
        mGridView = (GridView) contentView.findViewById(R.id.gridview);

        /*mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //int firstVisibleItem

                if(scrollState == SCROLL_STATE_IDLE){
                    int lastPostion =view.getLastVisiblePosition();
                    int totalItemCount = view.getCount();

                    int preSize = totalItemCount - lastPostion <=6?totalItemCount - lastPostion:6;
                    Log.i(TAG,"=====lastVisibleItem:"+lastPostion+"   preLoad:"+preSize);
                    for(int i = 0;i<preSize-1;i++){
                        String  fileScheme = ImageDownloader.Scheme.FILE.wrap(mImageSetList.get(0).imageItems.get(lastPostion+i).path);
                        ImageSize size = new ImageSize(imageGridSize,imageGridSize);
                        ImageLoader.getInstance().loadImage(fileScheme, size, new ImageLoadingListener() {
                            @Override public void onLoadingStarted(String imageUri, View view) { }

                            @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            }

                            @Override  public void onLoadingCancelled(String imageUri, View view) { }
                        });
                    }

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        }));//stop loading if fling or scrolling if using UIL*/

        mImagePresenter = new PicassoImgLoader();

        DataInterface dataSource = new DataSourceLocal(mContext);
        dataSource.provideMediaItems(this);//select all images from local database

        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels;

        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });

        mImageSetAdapter = new ImageSetAdapter(mContext);
        mImageSetAdapter.refreshData(mImageSetList);

        return contentView;

    }


    public void setOnImageItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    @Override
    public void onImageSelectChange(int position, ImageItemBean item, int selectedItemsCount, int maxSelectLimit) {
        mAdapter.refreshData(WrcImagePicker2.getInstance().getImageItemsOfCurrentImageSet());
        Log.i(TAG, "=====EVENT:onImageSelectChange");
    }

    @Override
    public void onImageCropComplete(Bitmap bmp, float ratio) {
        getActivity().finish();
    }

    /**
     * Adapter of image GridView
     */
    class ImageGridAdapter extends BaseAdapter {
        List<ImageItemBean> images;
        Context mContext;

        public ImageGridAdapter(Context ctx, List<ImageItemBean> images) {

            this.images = images;
            this.mContext = ctx;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (shouldShowCamera()) {
                return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
            }
            return ITEM_TYPE_NORMAL;
        }

        @Override
        public int getCount() {
            return shouldShowCamera() ? images.size() + 1 : images.size();
        }

        @Override
        public ImageItemBean getItem(int position) {
            if (shouldShowCamera()) {
                if (position == 0) {
                    return null;
                }
                return images.get(position - 1);
            } else {
                return images.get(position);
            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int itemViewType = getItemViewType(position);
            if (itemViewType == ITEM_TYPE_CAMERA) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_camera, parent, false);
                convertView.setTag(null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            wrcImagePicker.takePicture(GridImagesFragment.this, WrcImagePicker2.REQ_CAMERA);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                final ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.image_grid_item, null);
                    holder = new ViewHolder();
                    holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_thumb);
                    holder.cbSelected = (CustomCheckBox) convertView.findViewById(R.id.iv_thumb_check);
                    holder.cbPanel = convertView.findViewById(R.id.thumb_check_panel);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (shouldSelectMulti()) {//Multi Select mode will show a CheckBox at the Top Right corner
                    holder.cbSelected.setVisibility(View.VISIBLE);
                } else {
                    holder.cbSelected.setVisibility(View.GONE);
                }

                final ImageItemBean item = getItem(position);

                holder.cbSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wrcImagePicker.getSelectImageCount() > wrcImagePicker.getSelectLimit()) {
                            if (holder.cbSelected.isChecked()) {
                                //had better use ImageView instead of CheckBox
                                holder.cbSelected.toggle();//do this because CheckBox will auto toggle when clicking,must inverse
                                String toast = getResources().getString(R.string.you_have_a_select_limit, wrcImagePicker.getSelectLimit());
                                Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
                            } else {
                                //
                            }
                        } else {
                            //
                        }
                    }
                });

                holder.cbSelected.setOnCheckedChangeListener(null);//first set null or will have a bug when Recycling the view
                if (wrcImagePicker.isSelect(position, item)) {
                    holder.cbSelected.setChecked(true);
                    holder.ivPic.setSelected(true);
                } else {
                    holder.cbSelected.setChecked(false);
                }

                ViewGroup.LayoutParams params = holder.ivPic.getLayoutParams();
                params.width = params.height = imageGridSize;

                final View imageItemView = convertView.findViewById(R.id.iv_thumb);
                imageItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mGridView, imageItemView, position, position);
                    }
                });

                holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            wrcImagePicker.addSelectedImageItem(position, item);
                        } else {
                            wrcImagePicker.deleteSelectedImageItem(position, item);
                        }

                    }

                });

                //load the image to ImageView
                mImagePresenter.onPresentImage(holder.ivPic, getItem(position).path, imageGridSize);

            }

            return convertView;

        }

        class ViewHolder {
            ImageView ivPic;
            CustomCheckBox cbSelected;
            View cbPanel;
        }

        public void refreshData(List<ImageItemBean> items) {
            if (items != null && items.size() > 0) {
                images = items;
            }
            notifyDataSetChanged();
        }

    }

    private boolean shouldSelectMulti() {
        return wrcImagePicker.getSelectMode() == WrcImagePicker2.Select_Mode.MODE_MULTI;
    }

    private boolean shouldShowCamera() {
        return wrcImagePicker.isShouldShowCamera();
    }

    @Override
    public void onImagesLoaded(List<ImageSetBean> imageSetList) {

        mImageSetList = imageSetList;

        btnDir.setText(imageSetList.get(0).name);
        mAdapter = new ImageGridAdapter(mContext, imageSetList.get(0).imageItems);
        mGridView.setAdapter(mAdapter);

    }


    private void createPopupFolderList(int width, int height) {
        mFolderPopupWindow = new ListPopupWindow(mContext);
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
                wrcImagePicker.setCurrentSelectedImageSetPosition(i);

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
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0~1.0
        mContext.getWindow().setAttributes(lp);
    }

    /**
     * ImageSetBean adapter
     */
    class ImageSetAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        private List<ImageSetBean> mImageSets = new ArrayList<>();

        int mImageSize;

        int lastSelected = 0;

        public ImageSetAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.image_cover_size);
        }

        public void refreshData(List<ImageSetBean> folders) {
            if (folders != null && folders.size() > 0) {
                mImageSets = folders;
            } else {
                mImageSets.clear();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImageSets.size();
        }

        @Override
        public ImageSetBean getItem(int i) {
            return mImageSets.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_folder, viewGroup, false);
                holder = new ViewHolder(view);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.bindData(getItem(i));

            if (lastSelected == i) {
                holder.indicator.setVisibility(View.VISIBLE);
            } else {
                holder.indicator.setVisibility(View.INVISIBLE);
            }

            return view;
        }

        public void setSelectIndex(int i) {
            if (lastSelected == i) {
                return;
            }
            lastSelected = i;
            notifyDataSetChanged();
        }

        public int getSelectIndex() {
            return lastSelected;
        }

        class ViewHolder {
            ImageView cover;
            TextView name;
            TextView size;
            ImageView indicator;

            ViewHolder(View view) {
                cover = (ImageView) view.findViewById(R.id.cover);
                name = (TextView) view.findViewById(R.id.name);
                size = (TextView) view.findViewById(R.id.size);
                indicator = (ImageView) view.findViewById(R.id.indicator);
                view.setTag(this);
            }

            void bindData(ImageSetBean data) {
                name.setText(data.name);
                size.setText(data.imageItems.size() + mContext.getResources().getString(R.string.piece));
                mImagePresenter.onPresentImage(cover, data.cover.path, imageGridSize);
            }

        }

    }


    @Override
    public void onDestroy() {
        wrcImagePicker.removeOnImageItemSelectedChangeListener(this);
        if (wrcImagePicker.cropMode) {
            wrcImagePicker.removeOnImageCropCompleteListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WrcImagePicker2.REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            if (!TextUtils.isEmpty(wrcImagePicker.getCurrentPhotoPath())) {
                WrcImagePicker2.galleryAddPic(mContext, wrcImagePicker.getCurrentPhotoPath());
                getActivity().finish();
                //wrcImagePicker.notifyPictureTaken();

                if (wrcImagePicker.cropMode) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, CropImageActivity.class);
                    intent.putExtra(WrcImagePicker2.KEY_PIC_PATH, wrcImagePicker.getCurrentPhotoPath());
                    startActivityForResult(intent, WrcImagePicker2.REQ_CAMERA);
                } else {
                    ImageItemBean item = new ImageItemBean(wrcImagePicker.getCurrentPhotoPath(), "", -1);
                    wrcImagePicker.clearSelectedImages();
                    wrcImagePicker.addSelectedImageItem(-1, item);
                    wrcImagePicker.notifyOnImagePickComplete();
                }

            } else {
                Log.i(TAG, "didn't save to your path");
            }
        } else {
            if (wrcImagePicker.cameraMode)
                mContext.finish();
        }

    }

}
