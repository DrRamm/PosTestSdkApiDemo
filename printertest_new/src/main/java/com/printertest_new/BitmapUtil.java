package com.printertest_new;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by swex on 10/17/17.
 */

public class BitmapUtil {
    public static Bitmap byteArrayToBitmap(byte[] img, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setDensity(0); //не замечено что на что-то влияет, но оставил так пока
        Canvas canvas = new Canvas(bmp);
        canvas.setDensity(0);
        canvas.drawColor(Color.WHITE);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int index = h * (width / 8) + w / 8;
                if (index > img.length - 1) {
                    return bmp;
                }
                int mask = (1 << (7 - w % 8));
                int b = img[index];
                int isBlack = (b & mask);
                if (isBlack > 0) {
                    bmp.setPixel(w, h, Color.BLACK);
                }
            }
        }
        return bmp;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        byte result[] = new byte[(bitmap.getWidth() * bitmap.getHeight()) / 8];
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int R, G, B;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int index = h * (width / 8) + w / 8;
                int pixel = bitmap.getPixel(w, h);

                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                if (gray < 128) {
                    result[index] |= 1 << (7 - (w % 8));
                }
            }
        }
        return result;
    }

    public static Bitmap byteArrayToBitmap(byte[] img, int width) {
        return byteArrayToBitmap(img, width, img.length / (width / 8));
    }

}
