package com.example.fitforlife.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fitforlife.Fragments.GoalTab_fragment;
import com.example.fitforlife.Fragments.Notification_Fragment;
import com.example.fitforlife.Fragments.root_Fragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs ;

    public PageAdapter(FragmentManager fm , int numOfTabs) {
        super(fm);
        this.numOfTabs=numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new root_Fragment();
            case 1 :
                return new GoalTab_fragment();

             case 2 :
                    return new Notification_Fragment();
        default:
            return null;}

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
