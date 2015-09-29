package com.sundown.photofragment.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sundown.photofragment.PhotoFragmentApp;

/**
 * Created by Sundown on 4/7/2015.
 */
public final class PreferenceManager {

    public static final String PREF_FILE_NAME="prefs";

    private static PreferenceManager instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    private PreferenceManager(Context context){
        prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized PreferenceManager getInstance() {
        if (instance == null) {
            instance = new PreferenceManager(PhotoFragmentApp.getContext());
        }
        return instance;
    }

    public void putString(final String key, final String value){editor.putString(key, value);}

    public String getString(final String key){return prefs.getString(key, "");}

    public void putInt(final String key, final int value){editor.putInt(key, value);}

    public int getInt(final String key){return prefs.getInt(key, 0);}

    public void remove(final String key){
        editor.remove(key);
    }


    public void apply(){
        editor.apply(); //commit() writes its preferences to storage synchronously
        //however apply() starts an asynchronous commit to disk and you wont be notified
        //of any failures. however its a bit faster than commit();
    }

    public void commit(){ //this will block until it is done i believe
        editor.commit();
    }

}