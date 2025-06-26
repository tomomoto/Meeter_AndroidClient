package com.tom.meeter.infrastructure.components.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.BaseNetworkEntity;
import com.tom.meeter.infrastructure.components.binder.BaseViewHolderBinder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder, T extends BaseNetworkEntity>
      extends RecyclerView.Adapter<VH> {

    protected final BaseViewHolderBinder<VH, T> binder;
    protected final List<T> targets = new ArrayList<>();

    protected BaseAdapter(BaseViewHolderBinder<VH, T> binder) {
        this.binder = binder;
    }

    public void setData(List<T> newTargets) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
              new NetworkEntityDiffCallback<>(targets, newTargets));
        this.targets.clear();
        this.targets.addAll(newTargets);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        binder.bind(holder, targets.get(position));
    }

    @Override
    public int getItemCount() {
        return targets.size();
    }

    public static class NetworkEntityDiffCallback<T extends BaseNetworkEntity>
          extends DiffUtil.Callback {

        private final List<T> oldTargets, newTargets;

        public NetworkEntityDiffCallback(List<T> oldTargets, List<T> newTargets) {
            this.oldTargets = oldTargets;
            this.newTargets = newTargets;
        }

        @Override
        public int getOldListSize() {
            return oldTargets.size();
        }

        @Override
        public int getNewListSize() {
            return newTargets.size();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldTargets.get(oldItemPosition).equals(newTargets.get(newItemPosition));
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldTargets.get(oldItemPosition).getId()
                  .equals(newTargets.get(newItemPosition).getId());
        }
    }
}
