package com.tom.meeter.context.image;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.getCircleBitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.ResponseBody;

public class ImageHelper {

    private ImageHelper() {
    }

    public static Bitmap circleImage(ResponseBody body) {
        Bitmap from = from(body);
        if (from == null) {
            return null;
        }
        return getCircleBitmap(Bitmap.createScaledBitmap(from, 150, 150, true));
    }

    public static Bitmap circleImage(Bitmap src) {
        return getCircleBitmap(Bitmap.createScaledBitmap(src, 150, 150, true));
    }

    public static Bitmap from(ResponseBody body) {
        return BitmapFactory.decodeStream(body.byteStream());
    }
}
