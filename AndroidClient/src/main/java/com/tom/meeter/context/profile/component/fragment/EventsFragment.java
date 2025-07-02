package com.tom.meeter.context.profile.component.fragment;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tom.meeter.R;
import com.tom.meeter.databinding.FragmentEventsBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 14.12.2016.
 */
public class EventsFragment extends Fragment {

    private static final String TAG = EventsFragment.class.getCanonicalName();

    FragmentEventsBinding binding;

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
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logMethod(TAG, this);
        binding.fragmentEventsViewpager.setAdapter(
              createViewPagerAdapter(
                    getChildFragmentManager(),
                    getString(R.string.map),
                    getString(R.string.events),
                    getString(R.string.your_events)));

        binding.fragmentEventsTabs.setupWithViewPager(binding.fragmentEventsViewpager);
    }

    private static ViewPagerAdapter createViewPagerAdapter(
          FragmentManager fMgr, String mapTitle,
          String eventsTitle, String yourEventsTitle) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fMgr);
        adapter.addFragment(new GoogleMapsFragment(), mapTitle);
        adapter.addFragment(new ActiveEventsFragment(), eventsTitle);
        adapter.addFragment(new ProfileEventsFragment(), yourEventsTitle);
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
