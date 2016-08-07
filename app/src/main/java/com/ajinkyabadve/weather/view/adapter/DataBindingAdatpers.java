package com.ajinkyabadve.weather.view.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * binder helper class to create the custom method for data binding from layout file
 * Created by Ajinkya on 30-06-2016.
 */
public class DataBindingAdatpers {
    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
