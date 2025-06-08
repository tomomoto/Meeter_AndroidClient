package com.tom.meeter.context.profile.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.R;

class EventViewHolder extends RecyclerView.ViewHolder {

    private final CardView cardView;
    private final TextView eventName;
    private final TextView eventDescription;

    public EventViewHolder(View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.event_card_view);
        eventName = itemView.findViewById(R.id.event_name_card_view);
        eventDescription = itemView.findViewById(R.id.event_description_card_view);
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
