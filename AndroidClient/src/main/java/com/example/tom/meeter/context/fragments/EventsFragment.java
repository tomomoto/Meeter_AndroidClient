package com.example.tom.meeter.context.fragments;

import static com.example.tom.meeter.context.fragments.EventListFragment.createEventListFragment;
import static com.example.tom.meeter.context.fragments.GoogleMapsFragment.createGoogleMapsFragment;
import static com.example.tom.meeter.context.fragments.UserEventsFragment.createUserEventsFragment;
import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;
import static com.example.tom.meeter.infrastructure.common.InfrastructureHelper.createBundle;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tom.meeter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 14.12.2016.
 */
public class EventsFragment extends Fragment {

    private static final String TAG = EventsFragment.class.getCanonicalName();

    //@BindView(R.id.tabs)
    TabLayout tabLayout;
    //@BindView(R.id.viewpager)
    ViewPager viewPager;

    public EventsFragment() {
        // Required empty public constructor
        Log.d(TAG, "EventsFragment()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "EventsFragment.onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "EventsFragment.onCreateView()");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "EventsFragment.onViewCreated()");

        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(
                createViewPagerAdapter(
                        getChildFragmentManager(),
                        createBundle(USER_ID_KEY, getArguments().getString(USER_ID_KEY)),
                        getString(R.string.map),
                        getString(R.string.events),
                        getString(R.string.your_events)));

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static ViewPagerAdapter createViewPagerAdapter(
            FragmentManager fMgr, Bundle bundle, String mapTitle,
            String eventsTitle, String yourEventsTitle) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fMgr);
        adapter.addFragment(createGoogleMapsFragment(bundle), mapTitle);
        adapter.addFragment(createEventListFragment(bundle), eventsTitle);
        adapter.addFragment(createUserEventsFragment(bundle), yourEventsTitle);
        return adapter;
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
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
