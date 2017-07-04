

package com.wildnet.wrcpicker.imagePicker.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.wildnet.wrcpicker.R;
import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;
import com.wildnet.wrcpicker.imagePicker.patternClasses.PreviewActivity;

public class GridImagesActivity extends FragmentActivity implements View.OnClickListener,WrcImagePicker2.OnImageSelectedChangeListener {
    private static final String TAG = GridImagesActivity.class.getSimpleName();

    private TextView mBtnOk,mCount;

    GridImagesFragment mFragment;
    WrcImagePicker2 wrcImagePicker;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_custom);

        wrcImagePicker = WrcImagePicker2.getInstance();
        //most of the time you need to clear the last selected images or you can comment out this line
        wrcImagePicker.clearSelectedImages();

        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnOk.setTextColor(wrcImagePicker.themeTextColor);
        mBtnOk.setBackgroundColor(wrcImagePicker.themeBgColor);

        mCount = (TextView) findViewById(R.id.tv_title_count);

        if(wrcImagePicker.getSelectMode() == WrcImagePicker2.Select_Mode.MODE_SINGLE){
            mBtnOk.setVisibility(View.GONE);
            mCount.setVisibility(View.GONE);
        }else{
            mBtnOk.setVisibility(View.VISIBLE);
            mCount.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_backpress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //final boolean isCrop = getIntent().getBooleanExtra("isCrop",false);
        final boolean isCrop = wrcImagePicker.cropMode;
        imagePath = getIntent().getStringExtra(WrcImagePicker2.KEY_PIC_PATH);
        mFragment = new GridImagesFragment();
        /*Bundle data = new Bundle();
        data.putString(WrcImagePicker.KEY_PIC_PATH,imagePath);
        mFragment.setArguments(data);*/

        mFragment.setOnImageItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                position = wrcImagePicker.isShouldShowCamera() ? position-1 : position;

                if(wrcImagePicker.getSelectMode() == WrcImagePicker2.Select_Mode.MODE_MULTI){
                    go2Preview(position);
                }else if(wrcImagePicker.getSelectMode() == WrcImagePicker2.Select_Mode.MODE_SINGLE){
                    if(isCrop){
                        Intent intent = new Intent();
                        intent.setClass(GridImagesActivity.this,CropImageActivity.class);
                        intent.putExtra(WrcImagePicker2.KEY_PIC_PATH, wrcImagePicker.getImageItemsOfCurrentImageSet().get(position).path);
                        startActivity(intent);
                    }else{
                        wrcImagePicker.clearSelectedImages();
                        wrcImagePicker.addSelectedImageItem(position, wrcImagePicker.getImageItemsOfCurrentImageSet().get(position));
                        setResult(RESULT_OK);

                        finish();
                        wrcImagePicker.notifyOnImagePickComplete();
                    }

                }

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();

        wrcImagePicker.addOnImageSelectedChangeListener(this);

        int selectedCount = wrcImagePicker.getSelectImageCount();
        onImageSelectChange(0, null, selectedCount, wrcImagePicker.getSelectLimit());

    }

    /**

     * @param position
     */
    private void go2Preview(int position) {
        Intent intent = new Intent();
        intent.putExtra(WrcImagePicker2.KEY_PIC_SELECTED_POSITION, position);
        intent.setClass(GridImagesActivity.this, PreviewActivity.class);
        startActivityForResult(intent, WrcImagePicker2.REQ_PREVIEW);
    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_ok){
            finish();
            wrcImagePicker.notifyOnImagePickComplete();
        }else if(v.getId() == R.id.btn_pic_negative){
            finish();
        }
    }


    @Override
    public void onImageSelectChange(int position, ImageItemBean item, int selectedItemsCount, int maxSelectLimit) {
        if(selectedItemsCount > 0){
            mBtnOk.setEnabled(true);
            mCount.setText("" + selectedItemsCount+"/"+maxSelectLimit+" selected");
            }else{
            mCount.setText("0/"+maxSelectLimit+" selected");
            mBtnOk.setEnabled(false);
        }
        Log.i(TAG, "=====EVENT:onImageSelectChange");
    }

    @Override
    protected void onDestroy() {
        wrcImagePicker.removeOnImageItemSelectedChangeListener(this);
        wrcImagePicker.clearImageSets();
        Log.i(TAG, "=====removeOnImageItemSelectedChangeListener");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == Activity.RESULT_OK){

           if(requestCode == WrcImagePicker2.REQ_PREVIEW){
                setResult(RESULT_OK);
                finish();
                wrcImagePicker.notifyOnImagePickComplete();
            }

        }

    }


}
