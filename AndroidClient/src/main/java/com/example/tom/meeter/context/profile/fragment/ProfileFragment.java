package com.example.tom.meeter.context.profile.fragment;

import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tom.meeter.App;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.user.UserProfileViewModel;
import com.example.tom.meeter.infrastructure.viewmodel.ViewModelFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tom on 14.12.2016.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getCanonicalName();


    @BindView(R.id.user_photo)
    ImageView userImage;

    @BindView(R.id.user_id)
    TextView userIdView;

    @BindView(R.id.user_name)
    TextView userNameView;

    @BindView(R.id.user_age)
    TextView userAgeView;

    @BindView(R.id.user_gender)
    TextView userGenderView;

    @BindView(R.id.user_info)
    TextView userInfoView;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserProfileViewModel viewModel;

    public ProfileFragment() {
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        logMethod(TAG, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        logMethod(TAG, this);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logMethod(TAG, this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel.class);
        viewModel.init(getArguments().getString(USER_ID_KEY));

        viewModel.getUserLiveData().observe(this, user -> {
            userIdView.setText(getString(R.string.profile_user_id, user.getId()));
            userNameView.setText(getString(R.string.profile_user_name, user.getName(), user.getSurname()));
            userGenderView.setText(getString(R.string.profile_gender, user.getGender()));
            userAgeView.setText(getString(R.string.profile_age, getAgeFromDate(user.getBirthday())));
            userInfoView.setText(getString(R.string.profile_info, user.getInfo()));
        });
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
