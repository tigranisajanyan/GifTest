package com.example.intern.giftest.utils;

/**
 * Created by Tigran on 8/25/15.
 */
public class GifItConst {

    public static String SLASH = "/";
    public static String MY_DIR = "test_images";
    public static String GIF_NAME = "test.gif";
    public static String VIDEO_NAME = "myvideo.mp4";
    public static String VIDEO_TYPE = "video/*";
    public static String IMAGE_PATHS = "image_paths";
    public static String INDEX = "index";
    public static String FILE_PREFIX = "file://";
    public static String VIDEO_PATH = "video_path";

    public static int VIDEO_MAX_SECONDS = 30;
    public static int FRAME_SIZE = 400;
    public static int IMAGES_TO_GIF_INDEX = 1;
    public static int SHOOT_GIF_INDEX = 2;
    public static int VIDEO_TO_GIF_INDEX = 3;
    public static int GENERATED_FRAMES_MAX_COUNT = 15;



            /*GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.init(root + "/test_images/test.gif", 640, 640, 256, 100, 100);
            int[] pixels = new int[640 * 640];
            for (int i = 0; i < bitmaps.size(); i++) {
                bitmaps.get(i).getPixels(pixels, 0, 640, 0, 0, 640, 640);
                gifEncoder.addFrame(pixels);
            }
            gifEncoder.close();*/


}
