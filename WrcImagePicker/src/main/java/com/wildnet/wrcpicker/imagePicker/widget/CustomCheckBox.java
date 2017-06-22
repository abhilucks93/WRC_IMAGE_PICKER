

package com.wildnet.wrcpicker.imagePicker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class CustomCheckBox extends CompoundButton {

    private boolean canChecked = true;

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCheckBox(Context context) {
        super(context);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return CheckBox.class.getName();
    }

    public boolean isCanChecked() {
        return canChecked;
    }

    public void setCanChecked(boolean canChecked) {
        this.canChecked = canChecked;
    }


    @Override
    public void toggle() {
        if(canChecked){
            super.toggle();
        }
    }

    @Override
    public boolean performClick() {

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }


}
