package com.tom.meeter.context.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;
import com.tom.meeter.infrastructure.adapter.OnEventClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */
public class RecycleViewUserEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private final List<EventDTO> events = new ArrayList<>();

    private final DownloadAndCacheEventBinder downloadAndCacheEventBinder;

    public RecycleViewUserEventsAdapter(
          Fragment fragment, ImageDownloader imageDownloader,
          OnEventClickListener onEventClickListener) {
        this.downloadAndCacheEventBinder = new DownloadAndCacheEventBinder(
              fragment, imageDownloader, onEventClickListener);
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
        downloadAndCacheEventBinder.bind(holder, events.get(position));
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
