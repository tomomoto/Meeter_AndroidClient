package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.databinding.ActivityProfileSubscriberItemBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseSubscriberAdapter;
import com.tom.meeter.infrastructure.components.binder.SubscriberBinder;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import retrofit2.Call;
import retrofit2.Response;

public class SubscribersAdapter extends BaseSubscriberAdapter<SubscriberViewHolder> {

    private static final String TAG = SubscribersAdapter.class.getCanonicalName();

    public SubscribersAdapter(SubscriberBinder<SubscriberViewHolder> binder) {
        super(binder);
        logMethod(TAG, this);
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

    public void onSubUnsubClick(
          UserService service, Subscriber sub, int position,
          Runnable onAuthFail, Context ctx, String auth) {
        if (sub.isAmISubscribedTo()) {
            service.unsubscribe(auth, sub.getUser().getId())
                  .enqueue(new BaseOnNotAuthenticatedCallback<>(ctx, onAuthFail) {
                      @Override
                      public void onResponse(Call<Void> call, Response<Void> resp) {
                          super.onResponse(call, resp);
                          if (resp.code() == HttpCodes.OK) {
                              sub.setAmISubscribedTo(false);
                              SubscribersAdapter.this.notifyItemChanged(position);
                          }
                      }
                  });
        } else {
            service.subscribe(auth, sub.getUser().getId())
                  .enqueue(new BaseOnNotAuthenticatedCallback<>(ctx, onAuthFail) {
                      @Override
                      public void onResponse(Call<Void> call, Response<Void> resp) {
                          super.onResponse(call, resp);
                          if (resp.code() == HttpCodes.OK) {
                              sub.setAmISubscribedTo(true);
                              SubscribersAdapter.this.notifyItemChanged(position);
                          }
                      }
                  });
        }
        return;
    }
}
