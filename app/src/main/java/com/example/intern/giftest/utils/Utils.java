package com.example.intern.giftest.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.decoder.PhotoUtils;
import com.example.intern.giftest.activity.GalleryActivity;
import com.example.intern.giftest.gifutils.GifDecoder;
import com.example.intern.giftest.items.GalleryItem;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

import pl.droidsonroids.gif.GifDrawable;


public class Utils {

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(File file1,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file1.toString(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file1.toString(), options);
    }

    public static double getBitmapWidth() {

        DisplayMetrics metrics = GalleryActivity.getContext().getResources().getDisplayMetrics();
        double halfWidth = metrics.widthPixels / 3;

        return halfWidth;
    }

    public static double getBitmapHeight(String file) throws IOException {


        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, bounds);
        int width = bounds.outWidth;
        int height = bounds.outHeight;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        if (rotationAngle == 90 || rotationAngle == 270) {
            width = bounds.outHeight;
            height = bounds.outWidth;
        }

        DisplayMetrics metrics = GalleryActivity.getContext().getResources().getDisplayMetrics();
        double halfWidth = metrics.widthPixels / 3;
        double a = width / halfWidth;
        double halfHeight = height / a;

        return halfHeight;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    public static void initImageLoader(Context context) {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                    CACHE_DIR);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .considerExifParams(true)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .considerExifParams(true)
                    .decodingOptions(new BitmapFactory.Options())
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    /*.memoryCacheExtraOptions(1000, 1000) // width, height
                    .discCacheExtraOptions(1000, 1000, new BitmapProcessor() {
                        @Override
                        public Bitmap process(Bitmap bitmap) {
                            return null;
                        }
                    })*/
                    //.threadPoolSize(3)
                    //.threadPriority(Thread.MIN_PRIORITY + 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new UsingFreqLimitedMemoryCache(3 * 1024 * 1024)) // 3 Mb
                    .discCache(new UnlimitedDiscCache(cacheDir))
                    .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();

            ImageLoader.getInstance().init(config);

        } catch (Exception e) {
        }
    }

    public static ArrayList<GalleryItem> getGalleryPhotos(Activity activity) {
        ArrayList<GalleryItem> galleryList = new ArrayList();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = activity.managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    GalleryItem item = new GalleryItem();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    File file = new File(imagecursor.getString(dataColumnIndex));
                    if (file.exists()) {
                        item.setImagePath(imagecursor.getString(dataColumnIndex));
                        item.setHeight((int) Utils.getBitmapHeight(item.getImagePath()));
                        item.setWidth((int) Utils.getBitmapWidth());
                        item.setType(GalleryItem.Type.IMAGE);

                        galleryList.add(item);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    public static ArrayList<GalleryItem> getGalleryVideos(Activity activity) {
        ArrayList<GalleryItem> galleryList = new ArrayList();
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        try {
            final String[] columns = {MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media._ID};
            final String orderBy = MediaStore.Video.Media._ID;

            Cursor imagecursor = activity.managedQuery(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    GalleryItem item = new GalleryItem();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Video.Media.DATA);

                    String path = imagecursor.getString(dataColumnIndex);

                    File file = new File(path);
                    if (file.exists()) {

                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(path);

                        int orientation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                        int w = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                        int h = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

                        int width;
                        int height;

                        if (orientation == 0 || orientation == 180) {
                            width = w > h ? w : h;
                            height = w < h ? w : h;
                        } else {
                            width = w > h ? h : w;
                            height = w < h ? h : w;
                        }

                        double halfWidth = metrics.widthPixels / 3;
                        double a = width / halfWidth;
                        double halfHeight = height / a;

                        item.setImagePath(imagecursor.getString(dataColumnIndex));
                        item.setHeight((int) halfHeight);
                        item.setWidth((int) halfWidth);
                        item.setType(GalleryItem.Type.VIDEO);

                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imagecursor.getString(dataColumnIndex),
                                MediaStore.Images.Thumbnails.MINI_KIND);

                        Bitmap mutableBitmap = thumb.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas canvas = new Canvas(mutableBitmap);

                        item.setBitmap(mutableBitmap);
                        galleryList.add(item);
                        retriever.release();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }


    public static void clearDir(File dir) {
        try {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isDirectory())
                        clearDir(f);
                    f.delete();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void craeteDir(String fileName) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + fileName);
        if (!myDir.exists()) {
            myDir.mkdirs();
        } else {
            clearDir(myDir);
            File file = new File(myDir.toString());
            file.delete();
            myDir.mkdirs();
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String realPath = cursor.getString(idx);
                cursor.close();
                return realPath;
            } else {
                return contentUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] fileToByteArray(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public static ArrayList<Bitmap> getGifFramesPath(String path) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        byte[] bytes = Utils.fileToByteArray(path);
        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(bytes);
        gifDecoder.advance();

        for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
            Bitmap bitmap = gifDecoder.getNextFrame();
            bitmaps.add(bitmap);
            gifDecoder.advance();
        }
        return bitmaps;
    }

    public static ArrayList<Bitmap> getGifFramesFromResources(Context context, int resId) {

        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(context.getResources(), resId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Bitmap> bitmaps = new ArrayList<>();


        for (int i = 0; i < gifDrawable.getNumberOfFrames(); i++) {
            Bitmap bitmap = gifDrawable.seekToFrameAndGet(i);
            bitmaps.add(bitmap);

        }
        return bitmaps;
    }

    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static void getMidPoint(PointF lineStart, PointF lineEnd, PointF outPoint) {
        outPoint.set((lineStart.x + lineEnd.x) / 2, (lineStart.y + lineEnd.y) / 2);
    }

    public static float getAngleBetweenLines(PointF lineStart1, PointF lineEnd1, PointF lineStart2, PointF lineEnd2) {
        float dx1 = lineStart1.x - lineEnd1.x;
        float dy1 = lineStart1.y - lineEnd1.y;

        float dx2 = lineStart2.x - lineEnd2.x;
        float dy2 = lineStart2.y - lineEnd2.y;

        double radians = Math.atan2(dy2, dx2) - Math.atan2(dy1, dx1);

        return (float) Math.toDegrees(radians);
    }

    public static Bitmap readBitmapFromBufferFile(Context context, String bytesFilePath) {
        if (!TextUtils.isEmpty(bytesFilePath)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("pics_art_video_editor", Context.MODE_PRIVATE);
            int bufferSize = sharedPreferences.getInt("buffer_size", 0);
            int width = sharedPreferences.getInt("frame_width", 0);
            int height = sharedPreferences.getInt("frame_height", 0);
            ByteBuffer buffer = PhotoUtils.readBufferFromFile(bytesFilePath, bufferSize);
            return PhotoUtils.fromBufferToBitmap(width, height, buffer);
        }
        return null;
    }

    public static boolean downloadFile(String outputFile, String fileUrl) {
        try {
            URL url = new URL(fileUrl);

            URLConnection ucon = url.openConnection();
            ucon.setReadTimeout(5000);
            ucon.setConnectTimeout(10000);

            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

            File file = new File(outputFile);

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            int len;
            while ((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }

            outStream.flush();
            outStream.close();
            inStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
