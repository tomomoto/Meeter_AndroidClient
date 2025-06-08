package com.tom.meeter.context.profile.fragment;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tom.meeter.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tom on 14.12.2016.
 */
public class EventsFragment extends Fragment {

    private static final String TAG = EventsFragment.class.getCanonicalName();

    @BindView(R.id.fragment_events_tabs)
    TabLayout tabLayout;
    @BindView(R.id.fragment_events_viewpager)
    ViewPager viewPager;

    public EventsFragment() {
        // Required empty public constructor
        logMethod(TAG, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethod(TAG, this);
    }

    @Override
    public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logMethod(TAG, this);
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
        viewPager.setAdapter(
              createViewPagerAdapter(
                    getChildFragmentManager(),
                    getString(R.string.map),
                    getString(R.string.events),
                    getString(R.string.your_events)));

        tabLayout.setupWithViewPager(viewPager);
    }

    private static ViewPagerAdapter createViewPagerAdapter(
          FragmentManager fMgr, String mapTitle,
          String eventsTitle, String yourEventsTitle) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fMgr);
        adapter.addFragment(new GoogleMapsFragment(), mapTitle);
        adapter.addFragment(new ActiveEventsFragment(), eventsTitle);
        adapter.addFragment(new UserEventsFragment(), yourEventsTitle);
        return adapter;
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitles.get(position);
        }
    }
}
