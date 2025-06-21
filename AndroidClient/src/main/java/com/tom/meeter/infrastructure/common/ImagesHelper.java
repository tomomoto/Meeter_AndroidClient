package com.tom.meeter.infrastructure.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.tom.meeter.R;

import java.util.Random;

import okhttp3.ResponseBody;

public class ImagesHelper {

    private static final FontAwesome FONT_AWESOME = new FontAwesome();

    public static Bitmap circleImage(ResponseBody body) {
        return circleImage(body, 150, 150);
    }

    public static Bitmap circleImage(ResponseBody body, int scaleWidth, int scaleHeight) {
        Bitmap from = from(body);
        if (from == null) {
            return null;
        }
        return getCircleBitmap(Bitmap.createScaledBitmap(from, scaleWidth, scaleHeight, true));
    }

    public static Bitmap circleImage(Bitmap src) {
        return getCircleBitmap(Bitmap.createScaledBitmap(src, 150, 150, true));
    }

    public static Bitmap from(ResponseBody body) {
        return BitmapFactory.decodeStream(body.byteStream());
    }


    public static Bitmap getCircleBitmap(Bitmap src) {
        final Bitmap output = Bitmap.createBitmap(src.getWidth(),
              src.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);

        src.recycle();

        return output;
    }

    public static int randomPicResource() {
        int min = 1; // Minimum value of the range
        int max = 12; // Maximum value of the range

        Random random = new Random();
        int randomNumber = random.nextInt(max - min + 1) + min;
        return getImageIndexBased(randomNumber);
    }

    private static int getImageIndexBased(int index) {
        return switch (index) {
            case 1 -> R.drawable.random_1;
            case 2 -> R.drawable.random_2;
            case 3 -> R.drawable.random_3;
            case 4 -> R.drawable.random_4;
            case 5 -> R.drawable.random_5;
            case 6 -> R.drawable.random_6;
            case 7 -> R.drawable.random_7;
            case 8 -> R.drawable.random_8;
            case 9 -> R.drawable.random_9;
            case 10 -> R.drawable.random_10;
            case 11 -> R.drawable.random_11;
            case 12 -> R.drawable.random_12;
            default -> R.drawable.random_1;
        };
    }

    public static BitmapDescriptor getUserIconBitmap(Context context) {
        Bitmap myBitmap = Bitmap.createBitmap(125, 175, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(FONT_AWESOME.getTypeface(context));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTextSize(120);
        myCanvas.drawText(
              String.valueOf(FontAwesome.Icon.faw_child.getCharacter()), 20, 90, paint);

        //BitmapDescriptorFactory.fromResource(R.drawable.userlocation);
        //BitmapDescriptorFactory.fromAsset(myBitmap);
        //BitmapDescriptorFactory.fromFile(myBitmap);
        //BitmapDescriptorFactory.fromPath(myBitmap);
        return BitmapDescriptorFactory.fromBitmap(myBitmap);
    }

    private void bbb(Context ctx, int imageId) {
        //PART 1
        //Required:  imageId, for example R.drawable.your_image
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), imageId);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), bitmap);
        drawable.setCircular(true); // Make it a circle
        Bitmap circularBitmap = drawable.getBitmap();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(circularBitmap);
        /*---------------------------------------------------------------------------------------*/
        //PART 2
        //Required:
        GoogleMap map = null;
        LatLng latLng = null;
        Bitmap yourBitmap = null;

        ImageView imageView = new ImageView(ctx);
        imageView.setImageBitmap(yourBitmap);
        // Or, set a rounded background using a Drawable
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(
              ctx.getResources(), yourBitmap);
        roundedDrawable.setCircular(true);
        imageView.setBackground(roundedDrawable);

        AdvancedMarkerOptions markerOptions = new AdvancedMarkerOptions()
              .position(latLng)
              .iconView(imageView);
        map.addMarker(markerOptions);
        /*---------------------------------------------------------------------------------------*/
    }
}
