package com.ka12.ayirimatrimony;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class host extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter mPagerAdapter;
    BubbleNavigationConstraintView bottom_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_host, container, false);
        tabLayout = v.findViewById(R.id.tablayout);
        // bottom_bar=v.findViewById(R.id.bottom_bar);
        /*
        FragmentManager goto_frag = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        goto_frag.beginTransaction().add(R.id.change_frags, new home()).commit();
        bottom_bar.setCurrentActiveItem(2);
        bottom_bar.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case 0:
                        bottom_bar.setBackgroundColor(Color.parseColor("#A374ED"));
                     //   main_layout.setVisibility(View.GONE);
                        FragmentManager received = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        received.beginTransaction().replace(R.id.change_frags, new received()).remove(new profile()).remove(new match()).commit();
                        break;
                    case 1:
                        bottom_bar.setBackgroundColor(Color.RED);
                      //  main_layout.setVisibility(View.GONE);
                        FragmentManager settings = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        settings.beginTransaction().replace(R.id.change_frags, new match()).remove(new profile()).commit();
                        break;
                    case 2:
                        bottom_bar.setBackgroundColor(Color.parseColor("#000000"));
                     //   main_layout.setVisibility(View.VISIBLE);
                        FragmentManager home = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        home.beginTransaction().replace(R.id.change_frags, new home()).remove(new match()).remove(new profile()).commit();
                        break;
                    case 3:
                        bottom_bar.setBackgroundColor(Color.parseColor("#66bb6a"));
                      //  main_layout.setVisibility(View.GONE);
                        FragmentManager sent =Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        sent.beginTransaction().replace(R.id.change_frags, new sent()).remove(new match()).commit();
                        break;
                    case 4:
                        bottom_bar.setBackgroundColor(Color.parseColor("#ED8A6B"));
                      //  main_layout.setVisibility(View.GONE);
                        FragmentManager profile = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                        profile.beginTransaction().replace(R.id.change_frags, new profile()).remove(new match()).commit();
                        break;
                }
            }
        });
         */
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setting up id's
        final BubbleNavigationConstraintView bottom_bar = view.findViewById(R.id.bottom_bar);
        final TabLayout tabLayout = view.findViewById(R.id.tablayout);
        final ViewPager viewPager = view.findViewById(R.id.viewpager);
        //setting up viewpager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        //adding fragments
        adapter.addfrag(new match());
        adapter.addfrag(new received());
        adapter.addfrag(new home());
        adapter.addfrag(new sent());
        adapter.addfrag(new profile());
        //setting up adapter
        viewPager.setAdapter(adapter);
        //attatching viewpager to tablayout
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(2);
        //playdate
        viewPager.setOffscreenPageLimit(6);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottom_bar.setCurrentActiveItem(0);
                        bottom_bar.setBackgroundColor(Color.parseColor("#A374ED"));
                        break;
                    case 1:
                        bottom_bar.setCurrentActiveItem(1);
                        bottom_bar.setBackgroundColor(Color.RED);
                        break;
                    case 2:
                        bottom_bar.setCurrentActiveItem(2);
                        bottom_bar.setBackgroundColor(Color.parseColor("#000000"));
                        break;
                    case 3:
                        bottom_bar.setCurrentActiveItem(3);
                        bottom_bar.setBackgroundColor(Color.parseColor("#66bb6a"));
                        break;
                    case 4:
                        bottom_bar.setCurrentActiveItem(4);
                        bottom_bar.setBackgroundColor(Color.parseColor("#ED8A6B"));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottom_bar.setCurrentActiveItem(2);
        bottom_bar.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        break;
                    case 3:
                        viewPager.setCurrentItem(3);
                        break;
                    case 4:
                        viewPager.setCurrentItem(4);
                        break;
                }
            }
        });
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<Fragment> mfragments;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            // this.mfragments=new ArrayList<>();
        }

        public ViewPagerAdapter(FragmentManager childFragmentManager) {
            super(childFragmentManager);
            this.mfragments = new ArrayList<>();
        }

        public void addfrag(Fragment fragment) {
            mfragments.add(fragment);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return mfragments.get(position);
        }

        @Override
        public int getCount() {
            return mfragments.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}













