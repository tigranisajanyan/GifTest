package com.example.intern.giftest.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.intern.giftest.adapter.Adapter;
import com.example.intern.giftest.effects.Utils.ConvolutionMatrix;
import com.example.intern.giftest.items.GalleryItem;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tigran on 8/28/15.
 */
public class AddEffect extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private int effectNumber;
    private ArrayList<GalleryItem> bitmaps = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Adapter adapter;

    public AddEffect(ArrayList<GalleryItem> bitmaps, int effectNumber, Adapter adapter, Context context) {
        this.bitmaps = bitmaps;
        this.context = context;
        this.adapter = adapter;
        this.effectNumber = effectNumber;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(adapter.getSelected().size());
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        if (effectNumber==1) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    bitmaps.set(i, new GalleryItem(grayscale(bitmaps.get(i).getBitmap()), bitmaps.get(i).getImagePath(), bitmaps.get(i).isSeleted(), bitmaps.get(i).isFile(), bitmaps.get(i).getWidth(), bitmaps.get(i).getHeight()));
                    publishProgress(i);
                }
            }
        }
        if (effectNumber==2) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    bitmaps.set(i, new GalleryItem(reflection(bitmaps.get(i).getBitmap()), bitmaps.get(i).getImagePath(), bitmaps.get(i).isSeleted(), bitmaps.get(i).isFile(), bitmaps.get(i).getWidth(), bitmaps.get(i).getHeight()));
                    publishProgress(i);
                }
            }
        }
        if (effectNumber==3) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    bitmaps.set(i, new GalleryItem(snowEffect(bitmaps.get(i).getBitmap()), bitmaps.get(i).getImagePath(), bitmaps.get(i).isSeleted(), bitmaps.get(i).isFile(), bitmaps.get(i).getWidth(), bitmaps.get(i).getHeight()));
                    publishProgress(i);
                }
            }
        }
        if (effectNumber==4) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    bitmaps.set(i, new GalleryItem(boost(bitmaps.get(i).getBitmap(),2,20), bitmaps.get(i).getImagePath(), bitmaps.get(i).isSeleted(), bitmaps.get(i).isFile(), bitmaps.get(i).getWidth(), bitmaps.get(i).getHeight()));
                    publishProgress(i);
                }
            }
        }
        if (effectNumber==5) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    bitmaps.set(i, new GalleryItem(engrave(bitmaps.get(i).getBitmap()), bitmaps.get(i).getImagePath(), bitmaps.get(i).isSeleted(), bitmaps.get(i).isFile(), bitmaps.get(i).getWidth(), bitmaps.get(i).getHeight()));
                    publishProgress(i);
                }
            }
        }
        if (effectNumber==6) {
            for (int i = 0; i < bitmaps.size(); i++) {
                if (bitmaps.get(i).isSeleted()) {
                    bitmaps.set(i, new GalleryItem(emboss(bitmaps.get(i).getBitmap()), bitmaps.get(i).getImagePath(), bitmaps.get(i).isSeleted(), bitmaps.get(i).isFile(), bitmaps.get(i).getWidth(), bitmaps.get(i).getHeight()));
                    publishProgress(i);
                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.setProgress(values[0] + 1);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(outputDir), "image/gif");
        context.startActivity(intent);*/
        Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
    }


    public static Bitmap boost(Bitmap src, int type, float percent) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                if (type == 1) {
                    R = (int) (R * (1 + percent));
                    if (R > 255) R = 255;
                } else if (type == 2) {
                    G = (int) (G * (1 + percent));
                    if (G > 255) G = 255;
                } else if (type == 3) {
                    B = (int) (B * (1 + percent));
                    if (B > 255) B = 255;
                }
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    public static Bitmap emboss(Bitmap src) {
        double[][] EmbossConfig = new double[][]{
                {-1, 0, -1},
                {0, 4, 0},
                {-1, 0, -1}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(EmbossConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 127;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public static Bitmap engrave(Bitmap src) {
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.setAll(0);
        convMatrix.Matrix[0][0] = -2;
        convMatrix.Matrix[1][1] = 2;
        convMatrix.Factor = 1;
        convMatrix.Offset = 95;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public Bitmap grayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap reflection(Bitmap originalImage) {
        // gap space between original and reflected
        final int reflectionGap = 4;
        // get image size
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // this will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // create a Bitmap with the flip matrix applied to it.
        // we only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

        // create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);

        // create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        // set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    public static Bitmap snowEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 50;
        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(2000);
                if (R > thresHold && G > thresHold && B > thresHold) {
                    pixels[index] = Color.rgb(255, 255, 255);
                }
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

}
