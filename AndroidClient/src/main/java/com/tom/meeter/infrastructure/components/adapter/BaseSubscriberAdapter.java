package com.tom.meeter.infrastructure.components.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.components.binder.SubscriberBinder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubscriberAdapter<T extends RecyclerView.ViewHolder>
      extends RecyclerView.Adapter<T> {

    private final SubscriberBinder<T> binder;
    protected final List<Subscriber> subs = new ArrayList<>();

    protected BaseSubscriberAdapter(SubscriberBinder<T> binder) {
        this.binder = binder;
    }

    public void setData(List<Subscriber> newSubs) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
              new SubscriberDiffCallback(subs, newSubs));
        this.subs.clear();
        this.subs.addAll(newSubs);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        binder.bind(holder, subs.get(position));
    }

    @Override
    public int getItemCount() {
        return subs.size();
    }

    static class SubscriberDiffCallback extends DiffUtil.Callback {

        private final List<Subscriber> oldUsers, newUsers;

        public SubscriberDiffCallback(List<Subscriber> oldUsers, List<Subscriber> newUsers) {
            this.oldUsers = oldUsers;
            this.newUsers = newUsers;
        }

        @Override
        public int getOldListSize() {
            return oldUsers.size();
        }

        @Override
        public int getNewListSize() {
            return newUsers.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition).getUser().getId()
                  .equals(newUsers.get(newItemPosition).getUser().getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition)
                  .equals(newUsers.get(newItemPosition));
        }
    }
}
