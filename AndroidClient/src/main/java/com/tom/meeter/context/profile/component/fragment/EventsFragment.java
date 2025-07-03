package com.tom.meeter.context.profile.component.fragment;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.tom.meeter.R;
import com.tom.meeter.databinding.FragmentEventsBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 14.12.2016.
 */
public class EventsFragment extends Fragment {

    private static final String TAG = EventsFragment.class.getCanonicalName();

    private FragmentEventsBinding binding;

    public EventsFragment() {
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

        ViewPagerAdapter adapter = createViewPagerAdapter(requireActivity());

        ViewPager2 viewPager = binding.fragmentEventsViewpager;
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        new TabLayoutMediator(
              binding.fragmentEventsTabs, viewPager,
              (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Disable tab scroll for GMaps fragment ([0]).
                viewPager.setUserInputEnabled(position != 0);
            }
        });
    }

    private static ViewPagerAdapter createViewPagerAdapter(FragmentActivity activity) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(activity);
        adapter.addFragment(new GoogleMapsFragment(), activity.getString(R.string.on_map));
        adapter.addFragment(new ActiveEventsFragment(), activity.getString(R.string.listed));
        adapter.addFragment(new ProfileEventsFragment(), activity.getString(R.string.your));
        return adapter;
    }

    @Override
    public void onDestroyView() {
        logMethod(TAG, this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    static class ViewPagerAdapter extends FragmentStateAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(
              @NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        public String getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
