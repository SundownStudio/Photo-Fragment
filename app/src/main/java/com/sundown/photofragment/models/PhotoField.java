package com.sundown.photofragment.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.sundown.photofragment.utils.FileManager;
import com.sundown.photofragment.utils.PhotoUtils;
import com.sundown.photofragment.utils.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Sundown on 6/25/2015.
 */
public class PhotoField extends Field {


    private static final String IMAGE = "image";
    private static final String THUMB = "thumb";
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final String THUMBNAIL_PREFIX = "THM_";
    private static final String IMAGE_PREFIX = "IMG_";
    private static final String IMAGE_FILE = "IMAGE_FILE";
    private static final String THUMB_FILE = "THUMB_FILE";
    private static final int ROTATE_90_DEGREES = 90;


    private PhotoUtils photoUtils;
    private FileManager fileManager;
    private PreferenceManager preferenceManager;
    private File imageTempFile, thumbTempFile;

    public Uri getImageUri() {
        if (imageTempFile != null) return Uri.fromFile(imageTempFile);
        return null;
    }

    private Bitmap imageBitmap, thumbBitmap;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    private String imageName, thumbName;

    public String getImageName() {
        return imageName;
    }

    public String getThumbName() {
        return thumbName;
    }


    public PhotoField(PhotoUtils photoUtils, FileManager fileManager, PreferenceManager preferenceManager) {
        super(FieldType.PHOTO);
        this.photoUtils = photoUtils;
        this.fileManager = fileManager;
        this.preferenceManager = preferenceManager;
    }


    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = super.getProperties();
        properties.put(IMAGE, imageName);
        properties.put(THUMB, thumbName);
        return properties;
    }

    @Override
    public PhotoField setProperties(Map properties) {
        super.setProperties(properties);
        imageName = String.valueOf(properties.get(IMAGE));
        thumbName = String.valueOf(properties.get(THUMB));
        return this;
    }

    public void recycle(boolean clearFiles) {
        if (imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
        if (thumbBitmap != null) {
            thumbBitmap.recycle();
            thumbBitmap = null;
        }

        if (clearFiles) {
            cleanupTemporaryFiles();
        }
    }

    public void cleanupTemporaryFiles() {
        if (imageTempFile != null && imageTempFile.exists())
            imageTempFile.delete();
        if (thumbTempFile != null && thumbTempFile.exists())
            thumbTempFile.delete();
        imageBitmap = null; //DO NOT RECYCLE THESE HERE.. WE MAY STILL NEED THEM TO COMPRESS TO DB.. IF RECYCLING USE recycle() INSTEAD..
        thumbBitmap = null;
        preferenceManager.remove(IMAGE_FILE + getId());
        preferenceManager.remove(THUMB_FILE + getId());
        preferenceManager.commit();
    }

    public void loadImageFromFile() {
        if (imageTempFile != null) { //load it from file if it exists..
            imageBitmap = BitmapFactory.decodeFile(imageTempFile.getAbsolutePath());
        }
        if (thumbTempFile != null) {
            thumbBitmap = BitmapFactory.decodeFile(thumbTempFile.getAbsolutePath());
        }
    }

    public void generateTemporaryFiles() throws IOException {
        if (imageTempFile == null)
            imageTempFile = fileManager.createFile(IMAGE_PREFIX, IMAGE_EXTENSION, getId());
        if (thumbTempFile == null)
            thumbTempFile = fileManager.createFile(THUMBNAIL_PREFIX, IMAGE_EXTENSION, getId());
    }

    public void loadExistingTempFiles() {
        //load temp files from prefs if they dont exist yet..
        String imageFilepath = preferenceManager.getString(IMAGE_FILE + getId());
        String thumbFilepath = preferenceManager.getString(THUMB_FILE + getId());

        if (imageTempFile == null && imageFilepath.length() > 0)
            imageTempFile = new File(imageFilepath);

        if (thumbTempFile == null && thumbFilepath.length() > 0)
            thumbTempFile = new File(thumbFilepath);
    }

    public void resizeAndRotate(String selectedImagePath, int width, int height) throws IOException {
        if (selectedImagePath == null)
            selectedImagePath = imageTempFile.getAbsolutePath();

        //resizing image removes its Exif, so to get the orientation we need to process that first..
        int rotate = photoUtils.imageOrientationValidator(selectedImagePath);

        //now resize image to fit container because we dont need all that extra memory for big image
        imageBitmap = photoUtils.resizeImage(selectedImagePath, width, height);
        imageBitmap = photoUtils.rotateImage(imageBitmap, rotate);
    }

    public void rotateImages() {
        imageBitmap = photoUtils.rotateImage(imageBitmap, ROTATE_90_DEGREES);
        thumbBitmap = photoUtils.rotateImage(thumbBitmap, ROTATE_90_DEGREES);
    }

    public void extractThumb() {
        thumbBitmap = photoUtils.extractThumbnail(imageBitmap);
    }

    public void saveContentsToFiles() throws IOException {
        fileManager.saveImageToFile(imageTempFile, imageBitmap);
        fileManager.saveImageToFile(thumbTempFile, thumbBitmap);

        if (imageTempFile != null) {
            imageName = imageTempFile.getName();
            preferenceManager.putString(IMAGE_FILE + getId(), imageTempFile.getAbsolutePath());
        }
        if (thumbTempFile != null) {
            thumbName = thumbTempFile.getName();
            preferenceManager.putString(THUMB_FILE + getId(), thumbTempFile.getAbsolutePath());
        }
        preferenceManager.commit();
    }
}


