package com.sundown.photofragment.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.sundown.photofragment.PhotoFragmentApp;
import com.sundown.photofragment.R;
import com.sundown.photofragment.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sundown on 5/12/2015.
 */
public class PhotoUtils {

    public static final String IMAGE_EXTENSION = ".jpg";
    public static final String THUMBNAIL_PREFIX = "THM_";
    public static final String IMAGE_PREFIX = "IMG_";
    private static PhotoUtils instance;
    public static int thumbnailDimens;
    private SimpleDateFormat dateFormat;
    private File storageDir;
    private BitmapFactory.Options options;

    public static PhotoUtils getInstance(){
        if (instance == null)
            instance = new PhotoUtils();
        return instance;
    }

    private PhotoUtils(){
        thumbnailDimens =  (int) (PhotoFragmentApp.getContext().getResources().getDimension(R.dimen.thumbnail_image_size) / PhotoFragmentApp.getContext().getResources().getDisplayMetrics().density);
        dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        options = new BitmapFactory.Options();
        //options.inSampleSize = 8;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    public File createImageFile(String prefix, int id) throws IOException {
        String imageFileName = generateFileName(prefix, id);
        return File.createTempFile(imageFileName, IMAGE_EXTENSION, storageDir);
    }

    public String generateFileName(String prefix, int id){
        return prefix + dateFormat.format(new Date()) + "_" + id;
    }


    public Bitmap resizeImage(String path, int targetWidth, int targetHeight){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        bmOptions.inJustDecodeBounds = true; // avoids memory allocation, returning null for the bitmap object but setting outWidth, outHeight and outMimeType
        BitmapFactory.decodeFile(path, bmOptions);

        int scaleFactor = calculateInSampleSize(bmOptions, targetWidth, targetHeight); //better method, gets lower outputs than Math.min(photoW / targetWidth, photoH / targetHeight)
        Log.m("PHOTO", "Second scale factor: " + scaleFactor + " targetWidth: " + targetWidth + " targetHeight: " + targetHeight);

        bmOptions.inJustDecodeBounds = false; //we want the image this time..
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(path, bmOptions);

    }



    //use this for loading pics that are loaded from somewhere else where we cant resize
    //To tell the decoder to subsample the image, loading a smaller version into memory, set inSampleSize in BitmapFactory.Options
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public int imageOrientationValidator(String path) throws IOException {
        ExifInterface ei;

        ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.m("EditLocationDialogFragment", "ROTATING 90 DEGREES");
                return 90;

            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.m("EditLocationDialogFragment", "ROTATING 180 DEGREES");
                return 180;

            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.m("EditLocationDialogFragment", "ROTATING 270 DEGREES");
                return 270;

        }

        return 0;
    }


    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public String getPathFromGallery(Uri selectedImage){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = PhotoFragmentApp.getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();

        return imgDecodableString;
    }


    public void saveImage(File destinationFile, Bitmap image) throws IOException { //todo: handle lack of space issues
        FileOutputStream out = new FileOutputStream(destinationFile);
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }

}
