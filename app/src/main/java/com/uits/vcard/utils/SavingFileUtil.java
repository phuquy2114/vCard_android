package com.uits.vcard.utils;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class SavingFileUtil {
    /**
     * Suffix of photo
     */
    public enum PhotoSuffix {
        PNG(".png"), JPEG(".jpg");

        private final String value;

        PhotoSuffix(String paramValue) {
            this.value = paramValue;
        }

        public String getValue() {
            return value;
        }

    }

    public static final String TAG = SavingFileUtil.class.getSimpleName();
    public static final String APP_NAME = "MLM";
    public static final String TAKE_APP_NAME = "MLM_Pro";
    public static final int MEDIA_TYPE_IMAGE = 100;
    public static final int MEDIA_TYPE_VIDEO = 200;
    public static final int MEDIA_TYPE_AUDIO = 300;

    private SavingFileUtil() {
        // no instance
    }

    /**
     * Create a file Uri for saving an image
     */
    public static Uri getOutputMediaFileUriImage() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * Create a file Uri for saving an image
     */
    public static Uri getOutputMediaFileUriVideo() {
        return Uri.fromFile(getOutputMediaFileVideo());
    }

    /**
     * Image Line
     *
     * @return
     */
    public static File getOutputMediaFile() {
        return getOutputMediaFile(APP_NAME, MEDIA_TYPE_IMAGE);
    }

    /**
     * image
     *
     * @return
     */
    public static File getOutputMediaFileCamera() {
        return getOutputMediaFile(TAKE_APP_NAME, MEDIA_TYPE_IMAGE);
    }

    /**
     * image
     *
     * @return
     */
    public static File getOutputMediaFileAudio() {
        return getOutputMediaFile(APP_NAME, MEDIA_TYPE_AUDIO);
    }

    /**
     * L
     * video
     *
     * @return
     */
    public static File getOutputMediaFileVideo() {
        return getOutputMediaFile(TAKE_APP_NAME, MEDIA_TYPE_VIDEO);
    }

    /**
     * Create a File for saving an image
     */
    public static File getOutputMediaFile(String folderName, int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), folderName);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + folderName + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            return new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".3gpp");
        }
    }

    /**
     * Create a File for saving an image Album
     */
    public static String getOutputMediaFileImage(long name) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), TAKE_APP_NAME);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        // Create a media file
        return mediaStorageDir.getPath() + "/" + name + ".jpg";
    }

    /**
     * Create a File for saving an image Avatar
     *
     * @param user_id
     * @return
     */
    public static String getOutputMediaFileAvatar(int user_id) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), APP_NAME);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name avatar
        return mediaStorageDir.getPath() + "/IMG_AVATAR_" + user_id + ".jpg";
    }

    /**
     * write bitmap to file
     *
     * @param bitmap
     * @param file
     * @throws IOException
     */
    public static void writeBitmapToFile(Bitmap bitmap, File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
    }

    /**
     * delete file
     *
     * @param context
     * @param path
     */
    public static void deleteFile(Context context, String path, int name) {
        File fileDelete = new File(path);
        if (fileDelete.exists()) {
            if (path.contains("/IMG_AVATAR_" + name + ".jpg")) {
                if (fileDelete.delete()) {
                    Log.d(TAG, "file Deleted :" + path);
                    String where = MediaStore.Images.Media.DATA + "=?";
                    String[] selectionArgs = {path};
                    context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            where, selectionArgs);
                } else {
                    Log.d(TAG, "file not Deleted :" + path);
                }
            }
        }
    }

    /**
     * This method is used to save a bitmap to file.
     *
     * @param bitmap to save.
     * @return return file path.
     */
    public static String writeBitmapToFile(@NonNull Context context, @NonNull Bitmap bitmap, PhotoSuffix photoSuffix) {

        File file = getOutputMediaFile();
        if (file == null) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (photoSuffix == PhotoSuffix.PNG) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        }
        byte[] bitmapData = bos.toByteArray();

        try {
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Save bitmap to file error: " + e);
            return null;
        }

        // Mounted this photo to media
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);

        return file.getAbsolutePath();
    }

    /**
     * Convert File to Byte
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);
        System.out.println("\nDEBUG: FileInputStream is " + file);

        // Get the size of the file
        long length = file.length();
        System.out.println("DEBUG: Length of " + file + " is " + length + "\n");

        /*
         * You cannot create an array using a long type. It needs to be an int
         * type. Before converting to an int type, check to ensure that file is
         * not loarger than Integer.MAX_VALUE;
         */
        if (length > Integer.MAX_VALUE) {
            System.out.println("File is too large to process");
            return null;
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    /**
     * set Url on Sdcard
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null) {
                return null;
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * get Uri form File
     *
     * @param context
     * @param imageFile
     * @return
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Add download image to gallery
     */
    public static void addDownloadImageToGallery(Context context, File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpg"}, null);
    }

    /**
     * https://teamtreehouse.com/community/how-to-rotate-images-to-the-correct-orientation-portrait-by-editing-the-exif-data-once-photo-has-been-taken
     * rotation Image
     *
     * @param img
     * @param context
     * @param selectedImage
     * @return
     * @throws IOException
     */
    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {
        if (selectedImage.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("orientation: %s", "" + orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    /**
     * get duration file size
     * http://stackoverflow.com/questions/8042259/get-mp3-duration-in-android
     *
     * @param context
     * @param filePath
     * @return
     */
    public static long getDurationFilePath(Context context, String filePath) {
        Log.d(TAG, "getDurationFilePath: " + filePath);
        try {
            Uri uri = Uri.parse(filePath);
            // load data file
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(context, uri);

            String out = "";
            // get mp3 info
            // convert duration to minute:seconds
            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.v("time", duration);
            long dur = Long.parseLong(duration);
            String seconds = String.valueOf((dur % 60000) / 1000);
            Log.v("seconds", seconds);
            String minutes = String.valueOf(dur / 60000);
            out = minutes + ":" + seconds;
            Log.v("minutes", minutes);
            // close object
            metaRetriever.release();
            return dur;
        } catch (Exception e) {
            Log.e(TAG, "getDurationFilePath: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    /***
     * function to getTimeDuration of audio from path
     */
    public static long getTimeDuration(Context context, String path) {
        Log.d(TAG, "getTimeDuration: " + path);
        try {
            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            return mMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
