package com.example.tom.meeter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tom.meeter.activities.ProfileActivity;
import com.example.tom.meeter.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tom on 14.12.2016.
 */
public class FragmentProfile extends Fragment {

    @BindView(R.id.user_name)
    TextView userNameTextView;

    @BindView(R.id.user_age)
    TextView userAgeTextView;

    @BindView(R.id.user_sex)
    TextView userGenderTextView;

    @BindView(R.id.user_info)
    TextView userInfoTextView;
    //@BindView(R.id.user_)  TextView userId;

    public FragmentProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileActivity activity = (ProfileActivity) getActivity();
        userNameTextView.setText(activity.getUser().getName() + ' ' + activity.getUser().getSurname());
        userGenderTextView.setText("Пол: {}" + activity.getUser().getGender());
        userInfoTextView.setText("О себе: " + activity.getUser().getInfo());
        userAgeTextView.setText("Возраст:" + activity.getUser().getBirthday());
    }

    private String GetAgeFromDate(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
