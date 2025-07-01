package com.tom.meeter.context.user.adapter;

import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.databinding.ActivityUserSubscriberItemBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseAdapter;
import com.tom.meeter.infrastructure.components.binder.UserBinder;
import com.tom.meeter.infrastructure.components.binder.UserBinderImpl;
import com.tom.meeter.infrastructure.components.viewholder.UserViewHolder;

import javax.inject.Inject;

public class UsersAdapter extends BaseAdapter<UserViewHolder, UserDTO> {

    private static final String TAG = UsersAdapter.class.getCanonicalName();

    private final UserBinder<UserViewHolder> binder;

    @Inject
    public UsersAdapter(UserBinderImpl binder) {
        super(binder);
        this.binder = binder;
        logMethod(TAG, this);
    }

    public void setupBinder(Context ctx, Runnable onAuthFail) {
        binder.setup(
              ctx, onAuthFail,
              user -> dispatchToUserActivity(ctx, user.getId()));
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
