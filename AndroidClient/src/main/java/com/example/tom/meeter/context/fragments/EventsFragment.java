package com.example.tom.meeter.context.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tom.meeter.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;

/**
 * Created by Tom on 14.12.2016.
 */
public class EventsFragment extends Fragment {

    private static final String MAP_KEY = "Map";
    private static final String EVENTS_KEY = "Events";
    private static final String USER_EVENTS_KEY = "User events";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        Bundle args = new Bundle();
        args.putString(USER_ID_KEY, getArguments().getString(USER_ID_KEY));

        GoogleMapsFragment gmaps = new GoogleMapsFragment();
        gmaps.setArguments(args);

        EventListFragment events = new EventListFragment();
        events.setArguments(args);

        UserEventsFragment userEvents = new UserEventsFragment();
        userEvents.setArguments(args);

        adapter.addFragment(gmaps, MAP_KEY);
        adapter.addFragment(events, EVENTS_KEY);
        adapter.addFragment(userEvents, USER_EVENTS_KEY);

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
