package com.tom.meeter.context.event.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.activity.EventLocationMapActivity.createEventLocationMapActivityIntent;
import static com.tom.meeter.context.event.utils.Utils.createUpdateEventRequest;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;
import static com.tom.meeter.infrastructure.common.CommonHelper.dateOrNull;
import static com.tom.meeter.infrastructure.common.CommonHelper.textOrNull;
import static com.tom.meeter.infrastructure.common.DateHelper.showDateTimePicker;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.tom.meeter.R;
import com.tom.meeter.context.event.message.UpdateEventRequest;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.databinding.ActivityEventEditableBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileEventActivity extends BaseEventActivity {

    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";
    private ActivityResultLauncher<Intent> mapResult;
    private ActivityEventEditableBinding binding;

    @Override
    protected void initLayout(String token) {
        binding = ActivityEventEditableBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mapResult = registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                  if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                      double lat = result.getData().getDoubleExtra(EXTRA_LAT, 0.0);
                      double lng = result.getData().getDoubleExtra(EXTRA_LNG, 0.0);
                      if (binding instanceof ActivityEventEditableBinding eBinding) {
                          eBinding.eventLatitude.setText(String.valueOf(lat));
                          eBinding.eventLongitude.setText(String.valueOf(lng));
                      }
                  }
              });

        binding.saveEventButton.setOnClickListener(v -> {
            UpdateEventRequest req = createUpdateEventRequest(eventCache, binding);
            if (req.isEmpty()) {
                showMessage(this, getString(R.string.empty_update_request_is_not_sent));
                return;
            }
            eventService.updateEvent(Globals.getAuthHeader(token), eventCache.getId(), req).enqueue(
                  new HttpErrorLogger<>(getApplicationContext()) {
                      @Override
                      public void onResponse(Call<EventDTO> call, Response<EventDTO> res) {
                          super.onResponse(call, res);
                          if (res.isSuccessful()) {
                              eventCache = res.body();
                              updateEditableLayout();
                              showMessage(ProfileEventActivity.this, getString(R.string.saved));
                          }
                      }
                  });
        });

        binding.deleteEventButton.setOnClickListener(v -> showAlertDialog());

        /*
   TODO photoPath;
        * */

        updateEditableLayout();

        binding.selectStartingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.eventStarting));
        binding.selectEndingDateButton.setOnClickListener(
              v -> showDateTimePicker(this, binding.eventEnding));
        binding.btnEventLocationMap.setOnClickListener(
              v -> mapResult.launch(
                    createEventLocationMapActivityIntent(this, eventCache.getId())));
        binding.selectPhotoButton.setOnClickListener(
              v -> showMessage(this, "Кнопка пока не работает..."));

        eventViewModel.getEventPhotoLiveData()
              .observe(
                    this, photo -> {
                        photoCache = photo;
                        updateEditablePhoto();
                    });
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
              .setTitle(R.string.delete_event)
              .setMessage(R.string.are_you_sure_delete_event)
              .setPositiveButton(R.string.delete, (dialog, which) -> {
                  eventService.deleteEvent(getAuthHeader(accountManager), eventCache.getId())
                        .enqueue(new HttpErrorLogger<>(getApplicationContext()) {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> resp) {
                                super.onResponse(call, resp);
                                if (resp.isSuccessful()) {
                                    showMessage(ProfileEventActivity.this, getString(R.string.deleted));
                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    finish();
                                }
                            }
                        });
                  dialog.dismiss();
              })
              .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
              .show();
    }

    private void updateEditablePhoto() {
        if (binding instanceof ActivityEventEditableBinding eBinding) {
            eBinding.eventPhoto.setImageBitmap(circleImage(photoCache, 600, 600));
        }
    }

    private void updateEditableLayout() {
        binding.eventName.setText(eventCache.getName());
        binding.eventCreated.setText(UI_DATE_TIME_FORMAT.format(eventCache.getCreated()));

        binding.eventDescription.setText(eventCache.getDescription());
        binding.eventLatitude.setText(textOrNull(eventCache.getLatitude()));
        binding.eventLongitude.setText(textOrNull(eventCache.getLongitude()));
        binding.eventStarting.setText(dateOrNull(eventCache.getStarting()));
        binding.eventEnding.setText(dateOrNull(eventCache.getEnding()));
        binding.eventCity.setText(eventCache.getCity());
    }
}
