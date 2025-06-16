package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.getCircleBitmap;
import static com.tom.meeter.infrastructure.Image.ImagesHelper.randomPicResource;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.event.activity.EventActivity;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */

public class RecycleViewUserEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<EventDTO> events = new ArrayList<>();

    //TODO remove me when ...
    private Context ctx;
    public RecycleViewUserEventsAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public RecycleViewUserEventsAdapter() {
    }

    public void setData(List<EventDTO> events) {
        EventDiffCallback eventDiffCallback = new EventDiffCallback(this.events, events);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(eventDiffCallback);
        this.events.clear();
        this.events.addAll(events);
        diffResult.dispatchUpdatesTo(this);
        //Do we need it?
        notifyDataSetChanged();
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
        /*
        btnDelete.setOnClickListener(v -> {
            if (onDeleteButtonClickListener != null)
                onDeleteButtonClickListener.onDeleteButtonClicked(post);
        });*/
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private static class EventDiffCallback extends DiffUtil.Callback {

        private final List<EventDTO> oldPosts, newPosts;

        EventDiffCallback(List<EventDTO> oldPosts, List<EventDTO> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getId().equals(newPosts.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }

}
