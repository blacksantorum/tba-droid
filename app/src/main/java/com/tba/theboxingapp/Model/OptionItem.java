package com.tba.theboxingapp.Model;

import android.widget.ImageView;

/**
 * Created by christibbs on 9/20/14.
 */
public class OptionItem {
    public String title;
    public String subtitle;
    public int iconResource;

    public OptionItem(String title, String subtitle,int iconResource)
    {
        this.title = title;
        this.subtitle = subtitle;
        this.iconResource = iconResource;
    }
}
