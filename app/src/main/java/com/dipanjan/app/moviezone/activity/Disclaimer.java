package com.dipanjan.app.moviezone.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dipanjan.app.moviezone.app.AppController;
import com.dipanjan.app.moviezone.util.AnalyticsTAGs;

import info.dipanjan.app.R;

/**
 * Created by LENOVO on 16-01-2019.
 */

public class Disclaimer extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // AppController.getInstance().trackEvent(AnalyticsTAGs.Category.CATEGORY_MENU_CLICK,AnalyticsTAGs.Events.EVENT_OPEN_DISCLAIMER,AnalyticsTAGs.Events.EVENT_OPEN_DISCLAIMER);
        setContentView(R.layout.disclaimer);
    }
}
