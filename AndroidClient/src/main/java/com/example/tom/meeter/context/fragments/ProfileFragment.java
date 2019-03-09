package com.example.tom.meeter.context.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tom.meeter.App;
import com.example.tom.meeter.R;
import com.example.tom.meeter.context.user.UserProfileViewModel;
import com.example.tom.meeter.infrastructure.viewmodule.ViewModelFactory;

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

    @BindView(R.id.user_name)
    TextView userNameTextView;

    @BindView(R.id.user_age)
    TextView userAgeTextView;

    @BindView(R.id.user_sex)
    TextView userGenderTextView;

    @BindView(R.id.user_info)
    TextView userInfoTextView;
    //@BindView(R.id.user_)  TextView userId;

    @Inject
    ViewModelFactory viewModelFactory;

    private UserProfileViewModel viewModel;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //String userId = getArguments().getString("uID");

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserProfileViewModel.class);
        viewModel.init("1");

        viewModel.getUser().observe(this, user -> {
            userNameTextView.setText(user.getName() + ' ' + user.getSurname());
            userGenderTextView.setText("Пол: " + user.getGender());
            userInfoTextView.setText("О себе: " + user.getInfo());
            userAgeTextView.setText("Возраст: " + getAgeFromDate(user.getBirthday()));
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private String getAgeFromDate(String date) {
        if (date == null) {
            return "";
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        try {
            dob.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date));
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
