package com.tom.meeter.context.image;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.getCircleBitmap;

import android.graphics.Bitmap;

public class ImageHelper {

    private ImageHelper() {
    }

    public static Bitmap circleImage(Bitmap src) {
        return getCircleBitmap(Bitmap.createScaledBitmap(src, 150, 150, true));
    }
}
