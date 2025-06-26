package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.databinding.ActivityProfileSubscriberItemBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseAdapter;
import com.tom.meeter.infrastructure.components.binder.SubscriberBinder;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import retrofit2.Call;
import retrofit2.Response;

public class SubscribersAdapter
      extends BaseAdapter<SubscriberViewHolder, Subscriber> {

    private static final String TAG = SubscribersAdapter.class.getCanonicalName();

    private final UserService service;
    private final Runnable onAuthFail;
    private final Context ctx;

    public SubscribersAdapter(
          UserService service, Runnable onAuthFail, Context ctx,
          SubscriberBinder<SubscriberViewHolder> binder) {
        super(binder);
        logMethod(TAG, this);
        binder.setup(
              this::onSubUnSubClick,
              user -> dispatchToUserActivity(ctx, user.getId()));
        this.service = service;
        this.onAuthFail = onAuthFail;
        this.ctx = ctx;
    }

    @Override
    public SubscriberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logMethod(TAG, this);
        return new SubscriberViewHolder(
              ActivityProfileSubscriberItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        logMethod(TAG, this);
    }

    public void onSubUnSubClick(Subscriber sub, int position) {
        if (sub.isAmISubscribedTo()) {
            service.unsubscribe(getAuthHeader(AccountManager.get(ctx)), sub.getUser().getId())
                  .enqueue(new BaseOnNotAuthenticatedCallback<>(ctx, onAuthFail) {
                      @Override
                      public void onResponse(Call<Void> call, Response<Void> resp) {
                          super.onResponse(call, resp);
                          if (resp.code() != HttpCodes.OK) {
                              return;
                          }
                          sub.setAmISubscribedTo(false);
                          SubscribersAdapter.this.notifyItemChanged(position);
                      }
                  });
        } else {
            service.subscribe(getAuthHeader(AccountManager.get(ctx)), sub.getUser().getId())
                  .enqueue(new BaseOnNotAuthenticatedCallback<>(ctx, onAuthFail) {
                      @Override
                      public void onResponse(Call<Void> call, Response<Void> resp) {
                          super.onResponse(call, resp);
                          if (resp.code() != HttpCodes.OK) {
                              return;
                          }
                          sub.setAmISubscribedTo(true);
                          SubscribersAdapter.this.notifyItemChanged(position);
                      }
                  });
        }
        return;
    }
}
