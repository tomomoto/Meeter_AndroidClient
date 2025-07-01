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
import com.tom.meeter.infrastructure.components.binder.SubscriberBinderImpl;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class SubscribersAdapter
      extends BaseAdapter<SubscriberViewHolder, Subscriber> {

    private static final String TAG = SubscribersAdapter.class.getCanonicalName();

    private final UserService service;
    private final SubscriberBinder<SubscriberViewHolder> binder;

    private Context ctx;
    private Runnable onAuthFail;

    @Inject
    public SubscribersAdapter(
          UserService service, SubscriberBinderImpl binder) {
        super(binder);
        this.binder = binder;
        this.service = service;
        logMethod(TAG, this);
    }

    public void setupAdapter(Context ctx, Runnable onAuthFail) {
        this.ctx = ctx;
        this.onAuthFail = onAuthFail;

        this.binder.setup(
              ctx, onAuthFail,
              this::onSubUnSubClick,
              user -> dispatchToUserActivity(ctx, user.getId()));
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
