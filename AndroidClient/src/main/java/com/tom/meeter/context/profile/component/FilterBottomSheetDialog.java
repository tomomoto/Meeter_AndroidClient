package com.tom.meeter.context.profile.component;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tom.meeter.R;
import com.tom.meeter.databinding.DialogFilterBottomSheetBinding;

import java.util.HashSet;
import java.util.Set;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {

    private DialogFilterBottomSheetBinding binding;
    private final Set<String> selectedStatuses = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState) {

        binding = DialogFilterBottomSheetBinding.inflate(inflater, container, false);

        Context ctx = requireContext();

        String[] statuses = ctx.getResources().getStringArray(R.array.statuses);

        for (String status : statuses) {
            CheckBox checkBox = new CheckBox(ctx);
            checkBox.setText(status);
            checkBox.setOnCheckedChangeListener(
                  (buttonView, isChecked) -> {
                      if (isChecked) {
                          selectedStatuses.add(status);
                      } else {
                          selectedStatuses.remove(status);
                      }
                  });
            binding.statusCheckboxContainer.addView(checkBox);
        }

        binding.applyButton.setOnClickListener(v -> {
            Toast.makeText(getContext(),
                  "Выбрано: " + selectedStatuses.toString(), Toast.LENGTH_SHORT).show();
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
