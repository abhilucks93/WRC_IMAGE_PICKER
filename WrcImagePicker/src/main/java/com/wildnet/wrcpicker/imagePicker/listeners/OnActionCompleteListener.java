package com.wildnet.wrcpicker.imagePicker.listeners;

import com.wildnet.wrcpicker.imagePicker.model.ImageItemBean;

import java.util.List;

public interface OnActionCompleteListener {
        void _actionCompleted(List<ImageItemBean> items);
    }