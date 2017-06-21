
package com.wildnet.wrcpicker.imagePicker.bean;

import java.io.Serializable;

public class ImageItemBean implements Serializable {
    public String path;
    public String name;
    public long time;

    public ImageItemBean(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageItemBean other = (ImageItemBean) o;
            return this.path.equalsIgnoreCase(other.path) && this.time == other.time;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
