
package com.wildnet.wrcpicker.imagePicker.model;

import android.net.Uri;

import java.io.Serializable;

public class ImageItemBean implements Serializable {
    public Uri uri;
    public String name;
    public String path;
    public long time;

    public ImageItemBean(Uri uri, String name, long time) {
        this.uri = uri;
        this.name = name;
        this.time = time;
    }

    public ImageItemBean(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
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
