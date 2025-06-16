package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.getCircleBitmap;
import static com.tom.meeter.infrastructure.Image.ImagesHelper.randomPicResource;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.event.activity.EventActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */
public class RecycleViewActiveEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private static final String TAG = RecycleViewUserEventsAdapter.class.getCanonicalName();
    private final List<EventDTO> events = new ArrayList<>();
    private Context ctx;

    public RecycleViewActiveEventsAdapter() {
    }

    //TODO remove me when image can be downloaded from the server
    public RecycleViewActiveEventsAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public List<EventDTO> getEvents() {
        return events;
    }

    public void addEvent(EventDTO event) {
        events.add(event);
    }

    public void addEvents(List<EventDTO> events) {
        this.events.addAll(events);
    }

    public void cleanEvents() {
        events.clear();
    }


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(
              EventViewBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventDTO event = events.get(position);

        Bitmap src = BitmapFactory.decodeResource(ctx.getResources(), randomPicResource());
        Bitmap scaled = Bitmap.createScaledBitmap(src, 150, 150, true);
        Bitmap circled = getCircleBitmap(scaled);

        holder.bind(event.getName(), event.getDescription(), circled,
              v -> ctx.startActivity(
                    new Intent(ctx, EventActivity.class)
                          .putExtra(EventActivity.EVENT_ID_KEY, event.getId())));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
