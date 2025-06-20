package com.tom.meeter.infrastructure.adapter;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.circleImage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.CardItemBinding;

import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.EventViewHolder> {

    private static final String TAG = EventsRecyclerViewAdapter.class.getCanonicalName();

    private final Context context;
    private final List<EventDTO> events;
    private final ImageDownloader imageDownloader;
    private final Runnable onAuthFail;
    private final OnEventClickListener listener;

    public EventsRecyclerViewAdapter(
          Context context, List<EventDTO> events,
          ImageDownloader imageDownloader, Runnable onAuthFail,
          OnEventClickListener listener) {
        this.context = context;
        this.events = events;
        this.imageDownloader = imageDownloader;
        this.onAuthFail = onAuthFail;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(
              CardItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventDTO event = events.get(position);
        if (event != null) {
            holder.binding.textView.setText(event.getName());
            String photoPath = event.getPhotoPath();
            if (photoPath != null) {
                imageDownloader.downloadEventImage(
                      photoPath,
                      context,
                      (photo) -> holder.binding.imageView.setImageBitmap(circleImage(photo)),
                      onAuthFail
                );
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        } else {
            Log.w(TAG, "null event at [" + position + "]");
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        final CardItemBinding binding;

        EventViewHolder(CardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
