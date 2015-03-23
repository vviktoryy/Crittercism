package com.crittercism.ui_utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.crittercism.fragments.FragmentError;
import com.crittercism.fragments.FragmentLog;
import com.crittercism.fragments.FragmentNetwork;
import com.crittercism.fragments.FragmentOther;
import com.crittercism.fragments.FragmentTransaction;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new FragmentError();
            case 1:
                return new FragmentNetwork();
            case 2:
                return new FragmentTransaction();
            case 3:
                return new FragmentOther();
            case 4:
                return new FragmentLog();
        }
        return null;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 5; //No of Tabs
    }
}
