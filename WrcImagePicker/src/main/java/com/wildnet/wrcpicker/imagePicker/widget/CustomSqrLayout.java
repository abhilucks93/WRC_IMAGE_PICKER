

package com.wildnet.wrcpicker.imagePicker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomSqrLayout extends RelativeLayout {

    public CustomSqrLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomSqrLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSqrLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
