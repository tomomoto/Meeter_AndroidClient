package com.tom.meeter.context.user.adapter;

import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.CardItemBinding;
import com.tom.meeter.infrastructure.common.ImagesHelper;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<EventDTO> {

    private static final String TAG = GridViewAdapter.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final Runnable onAuthFail;

    public GridViewAdapter(
          Context ctx, List<EventDTO> events,
          ImageDownloader imgDownloader, Runnable onAuthFail) {
        super(ctx, 0, events);
        this.ctx = ctx;
        this.imageDownloader = imgDownloader;
        this.onAuthFail = onAuthFail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            CardItemBinding iBinding = CardItemBinding.inflate(
                  LayoutInflater.from(parent.getContext()), parent, false);
            holder = new ViewHolder(iBinding);
            holder.view = iBinding.getRoot();
            holder.view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EventDTO event = getItem(position);
        if (event != null) {
            holder.binding.textView.setText(event.getName());
            String photoPath = event.getPhotoPath();
            if (photoPath != null) {
                imageDownloader.downloadEventImage(
                      photoPath, ctx, ImagesHelper::circleImage,
                      holder.binding.imageView::setImageBitmap,
                      onAuthFail);
            }
        } else {
            Log.w(TAG, "null event at [" + position + "].");
        }
        return holder.view;
    }

    private static class ViewHolder {
        private View view;
        private final CardItemBinding binding;

        ViewHolder(CardItemBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }
    }
}
