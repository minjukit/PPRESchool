package org.tensorflow.lite.examples.classification.wrong;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WrongPager extends FragmentPagerAdapter {
    int mNumOfTabs; //tab의 갯수

    public WrongPager(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm,numOfTabs);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                WrongFrag tab1 = new WrongFrag(1);
                return tab1;
            case 1:
                WrongFrag tab2 = new WrongFrag(2);
                return tab2;
            case 2:
                WrongFrag tab3 = new WrongFrag(3);
                return tab3;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return this.mNumOfTabs;
    }
}