package com.tom.meeter.context.user.adapter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.ActivityUserSubscriberItemBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseUserAdapter;
import com.tom.meeter.infrastructure.components.binder.UserBinder;
import com.tom.meeter.infrastructure.components.viewholder.UserViewHolder;

public class UsersAdapter extends BaseUserAdapter<UserViewHolder> {

    private static final String TAG = UsersAdapter.class.getCanonicalName();

    public UsersAdapter(UserBinder<UserViewHolder> binder) {
        super(binder);
        logMethod(TAG, this);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logMethod(TAG, this);
        return new UserViewHolder(
              ActivityUserSubscriberItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        logMethod(TAG, this);
    }
}
