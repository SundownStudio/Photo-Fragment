package com.sundown.photofragment.tasks;

import android.os.AsyncTask;

import com.sundown.photofragment.logging.Log;
import com.sundown.photofragment.models.PhotoField;

/**
 * Created by Sundown on 9/29/2015.
 */
public class TaskRotateImage extends AsyncTask<Void, Void, Boolean> {


    public interface TaskRotateImageListener {
        void onImageRotated(boolean success);
    }

    private TaskRotateImageListener listener;
    private PhotoField model;

    public TaskRotateImage(PhotoField model, TaskRotateImageListener listener){
        this.model = model;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            model.rotateImages();
            return true;
        } catch (Exception e){
            Log.e(e);
            return false;
        }
    }


    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        listener.onImageRotated(success);
    }

}
