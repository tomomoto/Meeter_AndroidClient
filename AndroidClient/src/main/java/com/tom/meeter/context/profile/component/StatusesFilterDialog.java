package com.tom.meeter.context.profile.component;

import static com.tom.meeter.infrastructure.common.CommonHelper.resolveStatus;
import static com.tom.meeter.infrastructure.common.PreferencesHelper.savePrefsToServer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.message.SettingsCreateOrUpdate;
import com.tom.meeter.context.profile.service.SettingsService;
import com.tom.meeter.databinding.DialogFilterBottomSheetBinding;
import com.tom.meeter.infrastructure.common.PreferencesHelper;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class StatusesFilterDialog extends BottomSheetDialogFragment {

    private static final String TAG = StatusesFilterDialog.class.getCanonicalName();

    @Inject
    SettingsService service;

    private DialogFilterBottomSheetBinding binding;

    private final Set<EventDTO.EventStatus> selected = new HashSet<>();
    private Set<EventDTO.EventStatus> beforeSave;

    @Override
    public void onAttach(@NonNull Context ctx) {
        super.onAttach(ctx);
        beforeSave = PreferencesHelper.getVisibleEventsStatuses(ctx);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getActivity().getApplication()).getProfileComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {

        binding = DialogFilterBottomSheetBinding.inflate(inflater, container, false);

        Context ctx = requireContext();

        for (String statusName : ctx.getResources().getStringArray(R.array.statuses)) {
            EventDTO.EventStatus status = resolveStatus(ctx, statusName);
            CheckBox checkBox = new CheckBox(ctx);
            checkBox.setText(statusName);
            if (beforeSave.contains(status)) {
                selected.add(status);
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(
                  (buttonView, isChecked) -> {
                      if (isChecked) {
                          selected.add(status);
                      } else {
                          selected.remove(status);
                      }
                  });
            binding.statusCheckboxContainer.addView(checkBox);
        }

        binding.applyButton.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                Toast.makeText(ctx,
                      R.string.select_one_status_at_least,
                      Toast.LENGTH_SHORT).show();
                return;
            }
            if (beforeSave.equals(selected)) {
                Log.d(TAG, "Same items, not sending the request.");
                dismiss();
                return;
            }
            SettingsCreateOrUpdate req = new SettingsCreateOrUpdate();
            req.setVisibleEventStatuses(selected);
            FragmentActivity activity = requireActivity();
            savePrefsToServer(
                  activity, service, req,
                  activity::recreate);
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
