package com.tom.meeter.context.user;

import static com.tom.meeter.context.image.ImageHelper.circleImage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tom.meeter.R;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<EventDTO> {

    private final Context ctx;
    private final ImageDownloader imageDownloader;

    public GridViewAdapter(
          Context context, List<EventDTO> events,
          ImageDownloader imageDownloader) {
        super(context, 0, events);
        this.ctx = context;
        this.imageDownloader = imageDownloader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                  .inflate(R.layout.card_item, parent, false);
        }


        TextView textView = itemView.findViewById(R.id.text_view);
        ImageView imageView = itemView.findViewById(R.id.image_view);

        EventDTO event = getItem(position);
        if (event != null) {
            textView.setText(event.getName());

            String photoPath = event.getPhotoPath();
            if (photoPath != null) {
                imageDownloader.downloadEventImage(
                      photoPath, ctx.getApplicationContext(),
                      (body) -> imageView.setImageBitmap(
                            circleImage(BitmapFactory.decodeStream(body.byteStream()))),
                      () -> {
                          //TODO? On auth fail in case of image downloading?
                      });

            }
        }
        return itemView;
    }
}
