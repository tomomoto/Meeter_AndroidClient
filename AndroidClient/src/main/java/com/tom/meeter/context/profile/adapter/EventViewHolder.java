package com.tom.meeter.context.profile.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class EventViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.event_card_view)
    CardView cardView;
    @BindView(R.id.event_name_card_view)
    TextView eventName;
    @BindView(R.id.event_description_card_view)
    TextView eventDescription;

    public EventViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public CardView getCardView() {
        return cardView;
    }

    public TextView getEventName() {
        return eventName;
    }

    public TextView getEventDescription() {
        return eventDescription;
    }
}
