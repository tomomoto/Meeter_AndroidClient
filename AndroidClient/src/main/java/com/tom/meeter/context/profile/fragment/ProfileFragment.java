package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.setupTokenAction;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.databinding.FragmentProfileBinding;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by Tom on 14.12.2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getCanonicalName();

    private FragmentProfileBinding binding;

    @Inject
    ViewModelFactory viewModelFactory;

    private ProfileViewModel profileViewModel;

    private AccountManager accountManager;

    public ProfileFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this.getContext());
        logMethod(TAG, this);
    }

    @Override
    public View onCreateView(
          @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logMethod(TAG, this);

        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);

        setupTokenAction(accountManager, this.getActivity(),
              token -> profileViewModel.getProfile(Constants.getAuthHeader(token)));

        profileViewModel.getUserLiveData()
              .observe(getViewLifecycleOwner(), user -> {
                  if (user != null) {
                      binding.userId.setText(getString(R.string.profile_user_id, user.getId()));
                      binding.userName.setText(getString(R.string.profile_user_name, user.getName(), user.getSurname()));
                      binding.userGender.setText(getString(R.string.profile_gender, genderResolver(user.getGender())));
                      binding.userAge.setText(getString(R.string.profile_age, getAgeFromDate(user.getBirthday())));
                      binding.userInfo.setText(getString(R.string.profile_info, user.getInfo()));
                  }
              });
    }

    private String genderResolver(String gender) {
        return switch (gender.toLowerCase()) {
            case "female" -> getString(R.string.female_gender);
            case "male" -> getString(R.string.male_gender);
            default -> throw new IllegalArgumentException("#args " + gender);
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        logMethod(TAG, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        logMethod(TAG, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logMethod(TAG, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMethod(TAG, this);
    }

    private static String getAgeFromDate(String date) {
        if (date == null) {
            return "";
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        try {
            dob.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return String.valueOf(age);
    }
}
