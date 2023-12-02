package com.insightdev.both;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    int numPages;


    public MainPagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.numPages = numTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 5:
                return new SettingsFragment();
            case 4:
                return new InfoProfileFragment();
            case 3:
                return new ProfileFragment();
            case 2:
                return new ChatFragment();
            case 1:
                return new PremiumFragment();
            default:
                return new HomeFragment();

        }

    }


    @Override
    public int getCount() {
        return numPages;
    }
}
