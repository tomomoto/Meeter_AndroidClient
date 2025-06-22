package com.tom.meeter.infrastructure.components.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.infrastructure.components.binder.UserBinder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseUserAdapter<T extends RecyclerView.ViewHolder>
      extends RecyclerView.Adapter<T> {

    private final UserBinder<T> binder;
    private final List<UserDTO> users = new ArrayList<>();

    protected BaseUserAdapter(UserBinder<T> binder) {
        this.binder = binder;
    }

    public void setData(List<UserDTO> newUsers) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
              new UsersDiffCallback(users, newUsers));
        this.users.clear();
        this.users.addAll(newUsers);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        binder.bind(holder, users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UsersDiffCallback extends DiffUtil.Callback {

        private final List<UserDTO> oldUsers, newUsers;

        public UsersDiffCallback(List<UserDTO> oldUsers, List<UserDTO> newUsers) {
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
            return oldUsers.get(oldItemPosition).getId()
                  .equals(newUsers.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldUsers.get(oldItemPosition)
                  .equals(newUsers.get(newItemPosition));
        }
    }
}
