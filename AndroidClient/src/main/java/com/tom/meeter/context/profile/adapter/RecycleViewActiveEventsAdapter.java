package com.tom.meeter.context.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */
public class RecycleViewActiveEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private static final String TAG = RecycleViewActiveEventsAdapter.class.getCanonicalName();

    private final List<EventDTO> events = new ArrayList<>();

    private final DownloadAndCacheEventBinder downloadAndCacheEventBinder;

    public RecycleViewActiveEventsAdapter(
          Fragment fragment, ImageDownloader imageDownloader) {
        this.downloadAndCacheEventBinder = new DownloadAndCacheEventBinder(
              fragment, imageDownloader);
    }

    public List<EventDTO> getEvents() {
        return events;
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
        downloadAndCacheEventBinder.bind(holder, events.get(position));
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
