package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.event.activity.EventOnMapActivity.dispatchToEventOnMapActivity;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.textOrNull;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tom.meeter.databinding.ActivityEventReadableBinding;

public class UserEventActivity extends BaseEventActivity {
    ActivityEventReadableBinding binding;

    @Override
    protected void initLayout(String ign) {
        binding = ActivityEventReadableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        updateReadableLayout();

        binding.eventCreator.setOnClickListener(
              v -> dispatchToUserActivity(this, eventCache.getCreatorId()));
        binding.btnEventLocationMap.setOnClickListener(
              v -> dispatchToEventOnMapActivity(this, eventCache.getId()));

        eventViewModel.getEventPhotoLiveData()
              .observe(
                    this, photo -> binding.eventPhoto.setImageBitmap(
                          circleImage(photo, 600, 600)));
    }


    private void updateReadableLayout() {
        binding.eventName.setText(eventCache.getName());
        binding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(eventCache.getCreated()));
        binding.eventDescription.setText(eventCache.getDescription());
        binding.eventLatitude.setText(textOrNull(eventCache.getLatitude()));
        binding.eventLongitude.setText(textOrNull(eventCache.getLongitude()));
        binding.eventStarting.setText(dateOrNull(eventCache.getStarting()));
        binding.eventEnding.setText(dateOrNull(eventCache.getEnding()));
        binding.eventCity.setText(eventCache.getCity());
    }

    public static void dispatchToEventActivity(Context ctx, String eventId) {
        ctx.startActivity(createEventActivityIntent(ctx, eventId));
    }

    public static Intent createEventActivityIntent(Context ctx, String eventId) {
        return new Intent(ctx, UserEventActivity.class)
              .putExtra(UserEventActivity.EVENT_ID_KEY, eventId);
    }
}
