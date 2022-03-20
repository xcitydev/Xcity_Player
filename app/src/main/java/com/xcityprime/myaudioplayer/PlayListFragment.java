package com.xcityprime.myaudioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class PlayListFragment extends Fragment {
    View view;
    ViewPager viewPager;
    TabLayout IndicatorTab;
    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play_list, container, false);
        // Inflate the layout for this fragment
        viewPager = view.findViewById(R.id.viewFrag);
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new Playlist1(), "Playlist1");
        viewPager.setAdapter(viewPagerAdapter);
        IndicatorTab = view.findViewById(R.id.indicator);
        IndicatorTab.setupWithViewPager(viewPager);
        return view;
    }
    public static class viewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public viewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}