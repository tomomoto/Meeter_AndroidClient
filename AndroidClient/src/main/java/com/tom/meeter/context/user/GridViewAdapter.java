package com.tom.meeter.context.user;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.getCircleBitmap;
import static com.tom.meeter.infrastructure.Image.ImagesHelper.randomPicResource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tom.meeter.R;
import com.tom.meeter.context.network.dto.EventDTO;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<EventDTO> {

    private final Context ctx;

    public GridViewAdapter(Context context, List<EventDTO> list) {
        super(context, 0, list);
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                  .inflate(R.layout.card_item, parent, false);
        }

        EventDTO event = getItem(position);

        TextView textView = itemView.findViewById(R.id.text_view);
        ImageView imageView = itemView.findViewById(R.id.image_view);

        if (event != null) {
            textView.setText(event.getName());

            Bitmap src = BitmapFactory.decodeResource(ctx.getResources(), randomPicResource());
            Bitmap scaled = Bitmap.createScaledBitmap(src, 150, 150, true);
            Bitmap circled = getCircleBitmap(scaled);

            imageView.setImageBitmap(circled);
        }

        return itemView;
    }
}
