package com.sundown.photofragment;

import android.app.Application;
import android.content.Context;

import com.sundown.photofragment.logging.Log;

/**
 * Created by Sundown on 5/20/2015.
 */
public class PhotoFragmentApp extends Application {

    private static PhotoFragmentApp instance;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.m("PhotoFragmentApp.onCreate was called");
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
