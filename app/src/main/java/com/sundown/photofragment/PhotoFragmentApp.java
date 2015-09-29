package com.sundown.photofragment;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sundown on 5/20/2015.
 */
public class PhotoFragmentApp extends Application {

    private static PhotoFragmentApp instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
